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

    val singleTypeChecks = listOf(
        "plus", "minus", "times",
        "inc", "dec",
        "unaryMinus",
        "rem",
    )

    override fun visitDeclaration(declaration: IrDeclarationBase): IrStatement {
        if (declaration.annotations.any { it.isAnnotationWithEqualFqName(skipCheckAnnotation) })
            return declaration // skip checking this and all children

        return super.visitDeclaration(declaration)
    }

    override fun visitCall(expression: IrCall): IrExpression {
        if (!expression.type.isPrimitiveType()) return super.visitCall(expression)
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
