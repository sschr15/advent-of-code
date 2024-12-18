package com.sschr15.aoc.compiler.internal

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.createIrBuilder
import org.jetbrains.kotlin.backend.common.lower.irIfThen
import org.jetbrains.kotlin.backend.common.lower.irNot
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.builders.declarations.addBackingField
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrReturn
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.symbols.impl.IrPropertySymbolImpl
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import kotlin.reflect.full.allSupertypes

@OptIn(UnsafeDuringIrConstructionAPI::class)
class Memoizer(private val context: IrPluginContext) : IrElementTransformerVoid() {
    private val memoizedOrigin = object : IrDeclarationOrigin {
        override val isSynthetic = true
        override val name = "From Memoize annotation"
    }

    private val mapGet = context.irBuiltIns.mapClass.functions.single { it.owner.name.asString() == "get" && it.owner.valueParameters.size == 1 }
    private val mapPut = context.irBuiltIns.mutableMapClass.functions.single { it.owner.name.asString() == "put" && it.owner.valueParameters.size == 2 }

    private val mutableMapOf = context.referenceFunctions(CallableId(
        FqName("kotlin.collections"),
        null,
        Name.identifier("mutableMapOf")
    )).single { it.owner.valueParameters.isEmpty() }

    private val pair = context.referenceClass(ClassId(FqName("kotlin"), FqName("Pair"), false))!!
    private val triple = context.referenceClass(ClassId(FqName("kotlin"), FqName("Triple"), false))!!

    private val memoizeAnnotation = FqName("com.sschr15.aoc.annotations.Memoize")

    private fun IrPluginContext.keyFor(declaration: IrFunction): IrType = when (declaration.valueParameters.size) {
        1 -> declaration.valueParameters.single().type
        2 -> pair.typeWith(declaration.valueParameters.map { it.type })
        3 -> triple.typeWith(declaration.valueParameters.map { it.type })
        else -> irBuiltIns.arrayClass.typeWith(irBuiltIns.anyType)
    }

    override fun visitFunction(declaration: IrFunction): IrStatement {
        if (declaration.annotations.none { it.isAnnotationWithEqualFqName(memoizeAnnotation) })
            return super.visitFunction(declaration)

        val memoizeMap = context.irFactory.createProperty(
            name = Name.identifier("\$memoized-${declaration.name}-map"),
            origin = memoizedOrigin,
            visibility = DescriptorVisibilities.PRIVATE,
            modality = Modality.FINAL,
            symbol = IrPropertySymbolImpl(),
            isVar = false,
            isConst = false,
            isLateinit = false,
            isDelegated = false,
            startOffset = UNDEFINED_OFFSET,
            endOffset = UNDEFINED_OFFSET,
        )

        var parent = declaration.parent
        while (parent !is IrDeclarationContainer) {
            if (parent !is IrDeclaration) {
                error(parent::class.allSupertypes + "\n" + parent.dump())
            }
            parent = parent.parent
        }
        parent.addChild(memoizeMap)

        val memoizeMapField = memoizeMap.addBackingField {
            type = context.irBuiltIns.mutableMapClass.typeWith(
                context.keyFor(declaration),
                declaration.returnType
            )
            isStatic = parent is IrFile || declaration.isStatic
        }
        memoizeMapField.initializer = context.irFactory.createExpressionBody(
            expression = context.irBuiltIns.createIrBuilder(memoizeMap.symbol).run {
                irCall(mutableMapOf).apply {
                    type = memoizeMapField.type
                    putTypeArgument(0, this@Memoizer.context.keyFor(declaration))
                    putTypeArgument(1, declaration.returnType)
                }
            },
            startOffset = UNDEFINED_OFFSET,
            endOffset = UNDEFINED_OFFSET
        )

        val body = declaration.body ?: error("Memoized function must have a body")

        body.transformChildrenVoid(object : IrElementTransformerVoid() {
            override fun visitReturn(expression: IrReturn): IrExpression {
                return context.irBuiltIns.createIrBuilder(expression.returnTargetSymbol).irBlock {
                    val value = createTmpVariable(expression.value)
                    +irCall(mapPut).apply {
                        dispatchReceiver = irGetField(declaration.dispatchReceiverParameter?.let(::irGet), memoizeMapField)
                        putValueArgument(0, createKeyFor(declaration))
                        putValueArgument(1, irGet(value))
                    }
                    +irReturn(irGet(value))
                }
            }
        })

        val statements = body.statements.toMutableList()

        val toAdd = context.irBuiltIns.createIrBuilder(declaration.symbol).run { 
            irIfThen(
                condition = irNot(irEqualsNull(irCall(mapGet).apply { 
                    dispatchReceiver = irGetField(declaration.dispatchReceiverParameter?.let(::irGet), memoizeMapField)
                    putValueArgument(0, createKeyFor(declaration))
                })),
                thenPart = irReturn(irCall(mapGet).apply { 
                    dispatchReceiver = irGetField(declaration.dispatchReceiverParameter?.let(::irGet), memoizeMapField)
                    putValueArgument(0, createKeyFor(declaration))
                })
            )
        }
        statements.add(0, toAdd)

        declaration.body = context.irFactory.createBlockBody(body.startOffset, body.endOffset, statements)

        return declaration
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun IrBuilderWithScope.createKeyFor(function: IrFunction): IrExpression = when (function.valueParameters.size) {
        1 -> irGet(function.valueParameters.single())
        2 -> irCall(pair.constructors.single()).apply {
            putValueArgument(0, irGet(function.valueParameters[0]))
            putValueArgument(1, irGet(function.valueParameters[1]))
        }
        3 -> irCall(triple.constructors.single()).apply {
            putValueArgument(0, irGet(function.valueParameters[0]))
            putValueArgument(1, irGet(function.valueParameters[1]))
            putValueArgument(2, irGet(function.valueParameters[2]))
        }
        else -> irCall(context.irBuiltIns.arrayOf).apply {
            function.valueParameters.forEachIndexed { index, parameter ->
                putValueArgument(index, irGet(parameter))
            }
        }
    }
}
