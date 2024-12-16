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
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.getPrimitiveType
import org.jetbrains.kotlin.ir.types.isPrimitiveType
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import org.jetbrains.kotlin.ir.util.isAnnotationWithEqualFqName
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName

@OptIn(UnsafeDuringIrConstructionAPI::class)
class OverflowUnderflowChecker(private val context: IrPluginContext, private val config: CompilerConfiguration) : IrElementTransformerVoid() {
    val skipCheckAnnotation = FqName("sschr15.aoc.annotations.SkipOverflowUnderflowCheck")

    val singleTypeChecks = setOf(
        "plus", "minus", "times",
        "inc", "dec",
        "unaryMinus",
        "rem",
    )

    val intConversions = setOf("toFloat")
    val longConversions = setOf("toInt", "toDouble", "toFloat")

    fun visitConversionCall(expression: IrCall): IrExpression {
        val par0 = expression.dispatchReceiver ?: expression.getValueArgument(0)!!
        return context.irBuiltIns
            .createIrBuilder(expression.symbol, expression.startOffset, expression.endOffset)
            .irCall(
                context.referenceFunctions(
                    CallableId(
                        FqName("sschr15.aoc.annotations"),
                        null,
                        expression.symbol.owner.name,
                    )
                ).single { it.owner.valueParameters.single().type == par0.type }
            ).apply {
                putValueArgument(0, par0)
            }
    }

    override fun visitDeclaration(declaration: IrDeclarationBase): IrStatement {
        if (declaration.annotations.any { it.isAnnotationWithEqualFqName(skipCheckAnnotation) })
            return declaration // skip checking this and all children

        if (declaration.annotations.any { it.isAnnotationWithEqualFqName(FqName("sschr15.aoc.annotations.ExportIr")) }) {
            config.report(CompilerMessageSeverity.WARNING, declaration.dumpKotlinLike())
            config.report(CompilerMessageSeverity.WARNING, declaration.dump())
        }

        return super.visitDeclaration(declaration)
    }

    override fun visitCall(expression: IrCall): IrExpression {
        if (!expression.type.isPrimitiveType()) return super.visitCall(expression)

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
        if (expression.valueArgumentsCount == 2 && expression.getValueArgument(0)!!.type != expression.getValueArgument(1)!!.type) {
            config.report(CompilerMessageSeverity.WARNING, "Arguments to arithmetic operations are of different types, skipping overflow check")
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
