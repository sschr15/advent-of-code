package com.sschr15.aoc.compiler.internal

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.createIrBuilder
import org.jetbrains.kotlin.builtins.PrimitiveType
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.jvm.compiler.report
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.declarations.IrDeclarationBase
import org.jetbrains.kotlin.ir.declarations.IrDeclarationParent
import org.jetbrains.kotlin.ir.declarations.IrDeclarationWithName
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.expressions.IrMemberAccessExpression
import org.jetbrains.kotlin.ir.symbols.IrBindableSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

@OptIn(UnsafeDuringIrConstructionAPI::class)
class OverflowChecker(private val context: IrPluginContext, private val config: CompilerConfiguration) : IrElementTransformerVoid() {
    private val typeSystem = IrTypeSystemContextImpl(context.irBuiltIns)
    private val sequenceClass = context.referenceClass(ClassId(FqName("kotlin.sequences"), Name.identifier("Sequence")))!!
    private val skipCheckAnnotation = FqName("com.sschr15.aoc.annotations.SkipOverflowChecks")

    private val singleTypeChecks = setOf(
        "plus", "minus", "times",
        "inc", "dec",
        "unaryMinus", "abs",
        "rem",
    )

    private val singleArgumentTypeChecks = setOf(
        "inc", "dec", "unaryMinus", "abs",
    )

    private val collectionChecks = setOf("sum", "sumOf", "sumBy").flatMap { s ->
        listOf("kotlin.collections", "kotlin.sequences").map(::FqName).map { it.child(Name.identifier(s)) }
    }

    private val inputTypes = with(context.irBuiltIns) {
        setOf(byteType, charType, shortType, intType, longType, null)
    }

    private val outputTypes = with(context.irBuiltIns) {
        setOf(byteType, shortType, intType, longType)
    }

    private val intConversions = setOf("toFloat")
    private val longConversions = setOf("toInt", "toDouble", "toFloat")
    private val absoluteValueName = Name.special("<get-absoluteValue>")

    private fun visitConversionCall(expression: IrCall): IrExpression {
        val par0 = expression.dispatchReceiver ?: expression.getValueArgument(0)!!
        return context.irBuiltIns
            .createIrBuilder(expression.symbol, expression.startOffset, expression.endOffset)
            .irCall(
                context.referenceFunctions(
                    CallableId(
                        FqName("com.sschr15.aoc.annotations"),
                        null,
                        expression.symbol.owner.name,
                    )
                ).single { it.owner.valueParameters.single().type == par0.type }
            ).apply {
                putValueArgument(0, par0)
            }
    }

    lateinit var parent: IrDeclarationParent

    private fun IrType.isSumCandidate() =
        isSubtypeOfClass(context.irBuiltIns.iterableClass) || isArray() || isPrimitiveArray() || isSubtypeOfClass(sequenceClass)

    private fun visitSumCall(expression: IrCall): IrExpression {
        val collection = expression.extensionReceiver ?: error("Expected extension receiver")

        if (expression.valueArgumentsCount == 0) {
            return context.irBuiltIns
                .createIrBuilder(expression.symbol, expression.startOffset, expression.endOffset)
                .irCall(
                    context.referenceFunctions(CallableId(FqName("com.sschr15.aoc.annotations"), null, Name.identifier("sum")))
                        .single { collection.type.isSubtypeOf(it.owner.valueParameters.single().type, typeSystem) }
                ).apply {
                    putValueArgument(0, collection)
                }
        } else if (expression.valueArgumentsCount != 1) return super.visitCall(expression)

        // Lambda
        val lambda = expression.getValueArgument(0) ?: error("Expected lambda")
        if (lambda !is IrFunctionExpression) {
            error("Expected lambda to be a function expression")
        }

        if (!lambda.type.isFunctionTypeOrSubtype()) error("Expected lambda to be of function type")

        val (input, output) = (lambda.type as IrSimpleType).arguments

        if (input.typeOrNull !in inputTypes) return super.visitCall(expression)
        if (output.typeOrFail !in outputTypes) return super.visitCall(expression)

        if (expression.symbol.owner.name.asString() == "sumBy") {
            config.report(CompilerMessageSeverity.WARNING, "sumBy is deprecated. Overflow checking is silently replacing with sumOf.")
        }

        val newLambda = lambda.deepCopyWithSymbols(parent)
        return context.irBuiltIns.createIrBuilder(expression.symbol, expression.startOffset, expression.endOffset)
            .irCall(context.referenceFunctions(CallableId(
                FqName("com.sschr15.aoc.annotations"),
                null,
                Name.identifier("sumOf"),
                )).filter { collection.type.isSubtypeOfClass(it.owner.valueParameters[0].type.classOrFail) }
                .single { (it.owner.valueParameters[1].type as? IrSimpleType)?.arguments?.get(1)?.typeOrFail == output.typeOrFail }
            ).apply {
                putValueArgument(0, collection)
                putValueArgument(1, newLambda)
            }
    }

    private fun absoluteValueCheck(expression: IrMemberAccessExpression<*>): IrExpression? {
        val symbol = expression.symbol as? IrBindableSymbol<*, *> ?: return null
        val owner = symbol.owner as? IrDeclarationWithName ?: return null
        val extension = expression.extensionReceiver ?: return null
        val primitiveType = extension.type.getPrimitiveType() ?: return null
        if (primitiveType != PrimitiveType.INT && primitiveType != PrimitiveType.LONG) return null
        if (owner.name != absoluteValueName) return null
        return context.irBuiltIns.createIrBuilder(expression.symbol, expression.startOffset, expression.endOffset)
            .irCall(context.referenceFunctions(CallableId(
                FqName("com.sschr15.aoc.annotations"),
                null,
                Name.identifier("abs"),
            )).single { it.owner.valueParameters.single().type == extension.type })
            .apply {
                putValueArgument(0, extension)
            }
    }

    override fun visitDeclaration(declaration: IrDeclarationBase): IrStatement {
        if (declaration.annotations.any { it.isAnnotationWithEqualFqName(FqName("com.sschr15.aoc.annotations.ExportIr")) }) {
            config.report(CompilerMessageSeverity.WARNING, declaration.dumpKotlinLike())
            config.report(CompilerMessageSeverity.WARNING, declaration.dump())
        }

        if (declaration.annotations.any { it.isAnnotationWithEqualFqName(skipCheckAnnotation) })
            return declaration

        return super.visitDeclaration(declaration)
    }

    override fun visitExpression(expression: IrExpression): IrExpression {
        if (expression is IrMemberAccessExpression<*>) {
            val abs = absoluteValueCheck(expression)
            if (abs != null) return abs
        }
        return super.visitExpression(expression)
    }

    override fun visitFunction(declaration: IrFunction): IrStatement {
        parent = declaration
        return super.visitFunction(declaration)
    }

    private val IrMemberAccessExpression<*>.totalParameterCount: Int
        get() = if (extensionReceiver != null || dispatchReceiver != null) valueArgumentsCount + 1 else valueArgumentsCount

    private fun IrMemberAccessExpression<*>.getTotalParameter(index: Int): IrExpression? {
        if (extensionReceiver != null) {
            if (index == 0) return extensionReceiver
        } else if (dispatchReceiver != null) {
            if (index == 0) return dispatchReceiver
        } else if (index == 0) return getValueArgument(0)
        return getValueArgument(if (extensionReceiver != null || dispatchReceiver != null) index - 1 else index)
    }

    override fun visitCall(expression: IrCall): IrExpression {
        if (!expression.type.isPrimitiveType()) return super.visitCall(expression)

        val extension = expression.extensionReceiver
        if (extension != null && extension.type.isSumCandidate()) {
            if (expression.symbol.owner.kotlinFqName in collectionChecks) {
                return visitSumCall(expression)
            }
        }

        val par0 = expression.dispatchReceiver ?: expression.valueArguments.firstOrNull() ?: return super.visitCall(expression)
        if (par0.type.isPrimitiveType()) {
            val primitiveType = par0.type.getPrimitiveType()!!
            if (primitiveType == PrimitiveType.INT && expression.symbol.owner.name.asString() in intConversions) {
                return visitConversionCall(expression)
            } else if (primitiveType == PrimitiveType.LONG && expression.symbol.owner.name.asString() in longConversions) {
                return visitConversionCall(expression)
            }
        }

        val primitiveType = expression.type.getPrimitiveType() ?: return super.visitCall(expression)
        if (primitiveType != PrimitiveType.INT && primitiveType != PrimitiveType.LONG) return super.visitCall(expression)
        if (expression.symbol.owner.name.asString() !in singleTypeChecks) return super.visitCall(expression)
        if (
            (expression.totalParameterCount != 2 || expression.getTotalParameter(0)!!.type != expression.getTotalParameter(1)!!.type) &&
            (expression.totalParameterCount != 1 || expression.symbol.owner.name.asString() !in singleArgumentTypeChecks)
        ) {
            config.report(CompilerMessageSeverity.WARNING, "Unexpected number of arguments for ${expression.symbol.owner.name}, skipping")
            return super.visitCall(expression)
        }

        return context.irBuiltIns.createIrBuilder(expression.symbol, expression.startOffset, expression.endOffset)
            .irCall(context.referenceFunctions(CallableId(
                FqName("com.sschr15.aoc.annotations"),
                null,
                expression.symbol.owner.name,
            )).single { it.owner.valueParameters.first().type == expression.type }).apply {
                expression.dispatchReceiver
                val offset = if (expression.dispatchReceiver != null) {
                    putValueArgument(0, expression.dispatchReceiver!!)
                    1
                } else 0

                for (i in 0 until expression.valueArgumentsCount) {
                    putValueArgument(i + offset, expression.getValueArgument(i))
                }
            }
    }
}
