package com.sschr15.aoc.compiler.internal

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.createIrBuilder
import org.jetbrains.kotlin.builtins.PrimitiveType
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.jvm.compiler.report
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.declarations.IrDeclarationBase
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.getPrimitiveType
import org.jetbrains.kotlin.ir.types.isPrimitiveType
import org.jetbrains.kotlin.ir.util.isAnnotationWithEqualFqName
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName

@OptIn(UnsafeDuringIrConstructionAPI::class)
class OverflowUnderflowChecker(private val context: IrPluginContext, private val config: CompilerConfiguration) : IrElementTransformerVoid() {
    val skipCheckAnnotation = FqName("sschr15.aoc.annotations.SkipOverflowUnderflowCheck")

    /*
    val arithmeticExceptionCtor =
        context.referenceConstructors(ClassId(FqName("kotlin"), FqName("ArithmeticException"), false)).singleOrNull { it.owner.valueParameters.size == 1 }
        ?: context.referenceConstructors(ClassId(FqName("java.lang"), FqName("ArithmeticException"), false)).singleOrNull { it.owner.valueParameters.size == 1 }
        ?: error("Could not find ArithmeticException constructor")

    fun IrBuilderWithScope.exception(text: String) = irThrow(irCall(arithmeticExceptionCtor).apply {
        putValueArgument(0, irString(text))
    })

    val plus = mapOf(
        "int" to context.irBuiltIns.intClass.functions.single { it.owner.name.asString() == "plus" && it.owner.valueParameters.singleOrNull()?.type == context.irBuiltIns.intType },
        "long" to context.irBuiltIns.longClass.functions.single { it.owner.name.asString() == "plus" && it.owner.valueParameters.singleOrNull()?.type == context.irBuiltIns.longType },
        "byte" to context.irBuiltIns.byteClass.functions.single { it.owner.name.asString() == "plus" && it.owner.valueParameters.singleOrNull()?.type == context.irBuiltIns.byteType },
        "short" to context.irBuiltIns.shortClass.functions.single { it.owner.name.asString() == "plus" && it.owner.valueParameters.singleOrNull()?.type == context.irBuiltIns.shortType },
    )

    val minus = mapOf(
        "int" to context.irBuiltIns.intClass.functions.single { it.owner.name.asString() == "minus" && it.owner.valueParameters.singleOrNull()?.type == context.irBuiltIns.intType },
        "long" to context.irBuiltIns.longClass.functions.single { it.owner.name.asString() == "minus" && it.owner.valueParameters.singleOrNull()?.type == context.irBuiltIns.longType },
        "byte" to context.irBuiltIns.byteClass.functions.single { it.owner.name.asString() == "minus" && it.owner.valueParameters.singleOrNull()?.type == context.irBuiltIns.byteType },
        "short" to context.irBuiltIns.shortClass.functions.single { it.owner.name.asString() == "minus" && it.owner.valueParameters.singleOrNull()?.type == context.irBuiltIns.shortType },
    )

    val times = mapOf(
        "int" to context.irBuiltIns.intClass.functions.single { it.owner.name.asString() == "times" && it.owner.valueParameters.singleOrNull()?.type == context.irBuiltIns.intType },
        "long" to context.irBuiltIns.longClass.functions.single { it.owner.name.asString() == "times" && it.owner.valueParameters.singleOrNull()?.type == context.irBuiltIns.longType },
        "byte" to context.irBuiltIns.byteClass.functions.single { it.owner.name.asString() == "times" && it.owner.valueParameters.singleOrNull()?.type == context.irBuiltIns.byteType },
        "short" to context.irBuiltIns.shortClass.functions.single { it.owner.name.asString() == "times" && it.owner.valueParameters.singleOrNull()?.type == context.irBuiltIns.shortType },
    )

    val div = mapOf(
        "int" to context.irBuiltIns.intClass.functions.single { it.owner.name.asString() == "div" && it.owner.valueParameters.singleOrNull()?.type == context.irBuiltIns.intType },
        "long" to context.irBuiltIns.longClass.functions.single { it.owner.name.asString() == "div" && it.owner.valueParameters.singleOrNull()?.type == context.irBuiltIns.longType },
        "byte" to context.irBuiltIns.byteClass.functions.single { it.owner.name.asString() == "div" && it.owner.valueParameters.singleOrNull()?.type == context.irBuiltIns.byteType },
        "short" to context.irBuiltIns.shortClass.functions.single { it.owner.name.asString() == "div" && it.owner.valueParameters.singleOrNull()?.type == context.irBuiltIns.shortType },
    )

    val inc = mapOf(
        "int" to context.irBuiltIns.intClass.functions.single { it.owner.name.asString() == "inc" && it.owner.valueParameters.isEmpty() },
        "long" to context.irBuiltIns.longClass.functions.single { it.owner.name.asString() == "inc" && it.owner.valueParameters.isEmpty() },
        "byte" to context.irBuiltIns.byteClass.functions.single { it.owner.name.asString() == "inc" && it.owner.valueParameters.isEmpty() },
        "short" to context.irBuiltIns.shortClass.functions.single { it.owner.name.asString() == "inc" && it.owner.valueParameters.isEmpty() },
    )

    val dec = mapOf(
        "int" to context.irBuiltIns.intClass.functions.single { it.owner.name.asString() == "dec" && it.owner.valueParameters.isEmpty() },
        "long" to context.irBuiltIns.longClass.functions.single { it.owner.name.asString() == "dec" && it.owner.valueParameters.isEmpty() },
        "byte" to context.irBuiltIns.byteClass.functions.single { it.owner.name.asString() == "dec" && it.owner.valueParameters.isEmpty() },
        "short" to context.irBuiltIns.shortClass.functions.single { it.owner.name.asString() == "dec" && it.owner.valueParameters.isEmpty() },
    )

    fun handleIntExpression(expression: IrCall): IrExpression {
//        error(expression.dump())
        return context.irBuiltIns.createIrBuilder(expression.symbol, expression.startOffset, expression.endOffset).irBlock {
            val value = createTmpVariable(expression)
            val maxValue = IrConstImpl.int(UNDEFINED_OFFSET, UNDEFINED_OFFSET, context.irBuiltIns.intType, Int.MAX_VALUE)
            val minValue = IrConstImpl.int(UNDEFINED_OFFSET, UNDEFINED_OFFSET, context.irBuiltIns.intType, Int.MIN_VALUE)
            when (expression.symbol.owner.name.asString()) {
                "plus" -> {
                    +irIfThen(
                        condition = irCall(context.irBuiltIns.greaterFunByOperandType[context.irBuiltIns.intClass]!!).apply {
                            dispatchReceiver = irGet(value)
                            putValueArgument(0, irCall(minus["int"]!!).apply {
                                dispatchReceiver = maxValue
                                putValueArgument(0, expression.getValueArgument(0))
                            })
                        },
                        thenPart = exception("Integer Overflow"),
                        type = context.irBuiltIns.unitType
                    )
                    +irIfThen(
                        condition = irCall(context.irBuiltIns.lessFunByOperandType[context.irBuiltIns.intClass]!!).apply {
                            dispatchReceiver = irGet(value)
                            putValueArgument(0, irCall(plus["int"]!!).apply {
                                dispatchReceiver = minValue
                                putValueArgument(0, expression.getValueArgument(0))
                            })
                        },
                        thenPart = exception("Integer Underflow"),
                        type = context.irBuiltIns.unitType
                    )
                    +irReturn(irGet(value))
                }
                "minus" -> {
                    +irIfThen(
                        condition = irCall(context.irBuiltIns.lessFunByOperandType[context.irBuiltIns.intClass]!!).apply {
                            dispatchReceiver = irGet(value)
                            putValueArgument(0, irCall(plus["int"]!!).apply {
                                dispatchReceiver = maxValue
                                putValueArgument(0, expression.getValueArgument(0))
                            })
                        },
                        thenPart = exception("Integer Overflow"),
                        type = context.irBuiltIns.unitType
                    )
                    +irIfThen(
                        condition = irCall(context.irBuiltIns.greaterFunByOperandType[context.irBuiltIns.intClass]!!).apply {
                            dispatchReceiver = irGet(value)
                            putValueArgument(0, irCall(minus["int"]!!).apply {
                                dispatchReceiver = minValue
                                putValueArgument(0, expression.getValueArgument(0))
                            })
                        },
                        thenPart = exception("Integer Underflow"),
                        type = context.irBuiltIns.unitType
                    )
                    +irReturn(irGet(value))
                }
                "times" -> {
                    +irIfThen(
                        condition = irCall(context.irBuiltIns.greaterFunByOperandType[context.irBuiltIns.intClass]!!).apply {
                            dispatchReceiver = irGet(value)
                            putValueArgument(0, irCall(div["int"]!!).apply {
                                dispatchReceiver = maxValue
                                putValueArgument(0, expression.getValueArgument(0))
                            })
                        },
                        thenPart = exception("Integer Overflow"),
                        type = context.irBuiltIns.unitType
                    )
                    +irIfThen(
                        condition = irCall(context.irBuiltIns.lessFunByOperandType[context.irBuiltIns.intClass]!!).apply {
                            dispatchReceiver = irGet(value)
                            putValueArgument(0, irCall(div["int"]!!).apply {
                                dispatchReceiver = minValue
                                putValueArgument(0, expression.getValueArgument(0))
                            })
                        },
                        thenPart = exception("Integer Underflow"),
                        type = context.irBuiltIns.unitType
                    )
                    +irReturn(irGet(value))
                }
                "inc" -> {
                    +irIfThen(
                        condition = irEquals(irGet(value), minValue),
                        thenPart = exception("Integer Overflow"),
                        type = context.irBuiltIns.unitType
                    )
                    +irReturn(irGet(value))
                }
                "dec" -> {
                    +irIfThen(
                        condition = irEquals(irGet(value), maxValue),
                        thenPart = exception("Integer Underflow"),
                        type = context.irBuiltIns.unitType
                    )
                    +irReturn(irGet(value))
                }
                else -> return super.visitCall(expression)
            }
        }
    }

    fun handleLongExpression(expression: IrCall): IrExpression {
        return context.irBuiltIns.createIrBuilder(expression.symbol, expression.startOffset, expression.endOffset).irBlock {
            val value = createTmpVariable(expression)
            val maxValue = IrConstImpl.long(UNDEFINED_OFFSET, UNDEFINED_OFFSET, context.irBuiltIns.longType, Long.MAX_VALUE)
            val minValue = IrConstImpl.long(UNDEFINED_OFFSET, UNDEFINED_OFFSET, context.irBuiltIns.longType, Long.MIN_VALUE)
            when (expression.symbol.owner.name.asString()) {
                "plus" -> {
                    +irIfThen(
                        condition = irCall(context.irBuiltIns.greaterFunByOperandType[context.irBuiltIns.longClass]!!).apply {
                            dispatchReceiver = irGet(value)
                            putValueArgument(0, irCall(minus["long"]!!).apply {
                                dispatchReceiver = maxValue
                                putValueArgument(0, expression.getValueArgument(0))
                            })
                        },
                        thenPart = exception("Long Overflow"),
                        type = context.irBuiltIns.unitType
                    )
                    +irIfThen(
                        condition = irCall(context.irBuiltIns.lessFunByOperandType[context.irBuiltIns.longClass]!!).apply {
                            dispatchReceiver = irGet(value)
                            putValueArgument(0, irCall(plus["long"]!!).apply {
                                dispatchReceiver = minValue
                                putValueArgument(0, expression.getValueArgument(0))
                            })
                        },
                        thenPart = exception("Long Underflow"),
                        type = context.irBuiltIns.unitType
                    )
                    +irGet(value)
                }
                "minus" -> {
                    +irIfThen(
                        condition = irCall(context.irBuiltIns.lessFunByOperandType[context.irBuiltIns.longClass]!!).apply {
                            dispatchReceiver = irGet(value)
                            putValueArgument(0, irCall(plus["long"]!!).apply {
                                dispatchReceiver = maxValue
                                putValueArgument(0, expression.getValueArgument(0))
                            })
                        },
                        thenPart = exception("Long Overflow"),
                        type = context.irBuiltIns.unitType
                    )
                    +irIfThen(
                        condition = irCall(context.irBuiltIns.greaterFunByOperandType[context.irBuiltIns.longClass]!!).apply {
                            dispatchReceiver = irGet(value)
                            putValueArgument(0, irCall(minus["long"]!!).apply {
                                dispatchReceiver = minValue
                                putValueArgument(0, expression.getValueArgument(0))
                            })
                        },
                        thenPart = exception("Long Underflow"),
                        type = context.irBuiltIns.unitType
                    )
                    +irGet(value)
                }
                "times" -> {
                    +irIfThen(
                        condition = irCall(context.irBuiltIns.greaterFunByOperandType[context.irBuiltIns.longClass]!!).apply {
                            dispatchReceiver = irGet(value)
                            putValueArgument(0, irCall(div["long"]!!).apply {
                                dispatchReceiver = maxValue
                                putValueArgument(0, expression.getValueArgument(0))
                            })
                        },
                        thenPart = exception("Long Overflow"),
                        type = context.irBuiltIns.unitType
                    )
                    +irIfThen(
                        condition = irCall(context.irBuiltIns.lessFunByOperandType[context.irBuiltIns.longClass]!!).apply {
                            dispatchReceiver = irGet(value)
                            putValueArgument(0, irCall(div["long"]!!).apply {
                                dispatchReceiver = minValue
                                putValueArgument(0, expression.getValueArgument(0))
                            })
                        },
                        thenPart = exception("Long Underflow"),
                        type = context.irBuiltIns.unitType
                    )
                    +irGet(value)
                }
                "inc" -> {
                    +irIfThen(
                        condition = irEquals(irGet(value), minValue),
                        thenPart = exception("Long Overflow"),
                        type = context.irBuiltIns.unitType
                    )
                    +irGet(value)
                }
                "dec" -> {
                    +irIfThen(
                        condition = irEquals(irGet(value), maxValue),
                        thenPart = exception("Long Underflow"),
                        type = context.irBuiltIns.unitType
                    )
                    +irGet(value)
                }
                else -> return super.visitCall(expression)
            }
        }
    }
     */

    override fun visitDeclaration(declaration: IrDeclarationBase): IrStatement {
        if (declaration.annotations.any { it.isAnnotationWithEqualFqName(skipCheckAnnotation) })
            return declaration // skip checking this and all children

        return super.visitDeclaration(declaration)
    }

    override fun visitCall(expression: IrCall): IrExpression {
        if (!expression.type.isPrimitiveType()) return super.visitCall(expression)
        val primitiveType = expression.type.getPrimitiveType() ?: return super.visitCall(expression)
        if (primitiveType != PrimitiveType.INT && primitiveType != PrimitiveType.LONG) return super.visitCall(expression)
        if (expression.symbol.owner.name.asString() !in listOf("plus", "minus", "times", "inc", "dec")) return super.visitCall(expression)
        if (expression.valueArgumentsCount == 2 && expression.getValueArgument(0)!!.type != expression.getValueArgument(1)!!.type) {
            config.report(CompilerMessageSeverity.WARNING, "Arguments to arithmetic operations are of different types, skipping overflow/underflow check")
            return super.visitCall(expression)
        }

        return context.irBuiltIns.createIrBuilder(expression.symbol, expression.startOffset, expression.endOffset)
            .irCall(context.referenceFunctions(CallableId(
                FqName("sschr15.aoc.annotations"),
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
