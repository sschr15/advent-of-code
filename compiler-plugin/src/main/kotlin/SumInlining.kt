package com.sschr15.aoc.compiler.internal

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.ir.inline
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.declarations.buildVariable
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.lazy.IrLazyFunction
import org.jetbrains.kotlin.ir.declarations.lazy.IrLazyFunctionBase
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrSyntheticBody
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.isArray
import org.jetbrains.kotlin.ir.types.isSubtypeOfClass
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.getPackageFragment
import org.jetbrains.kotlin.ir.util.isAnnotationWithEqualFqName
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid
import org.jetbrains.kotlin.name.FqName

@OptIn(UnsafeDuringIrConstructionAPI::class)
class SumInlining(val context: IrPluginContext, val config: CompilerConfiguration) : IrElementTransformerVoid() {
    val skipCheckAnnotation = FqName("com.sschr15.aoc.annotations.SkipOverflowChecks")

    override fun visitFunction(declaration: IrFunction): IrStatement {
        if (declaration.annotations.any { it.isAnnotationWithEqualFqName(skipCheckAnnotation) }) {
            return declaration // Skip this function
        }

        val body = declaration.body ?: return super.visitFunction(declaration) // No body to transform
        if (body is IrSyntheticBody) return super.visitFunction(declaration) // Synthetic body has no statements

//        val statements = body.statements.toMutableList()
//        statements.transformFlat { statement ->
//            if (statement !is IrCall) return@transformFlat (statement.transform(this, null) as? IrStatement)?.let(::listOf)
//            val function = statement.symbol.owner
//            config.report(CompilerMessageSeverity.WARNING, "Attempting function call: ${function.name}")
//            if (function.getPackageFragment().packageFqName != FqName("kotlin.collections"))
//                return@transformFlat listOf(statement.transform(this, null))
//            if (function.name.asString() !in listOf("sum", "sumBy", "sumOf")) return@transformFlat listOf(statement.transform(this, null))
////            config.report(CompilerMessageSeverity.WARNING, "Attempting sum function call: ${function.name}")
//            if (statement.extensionReceiver == null) return@transformFlat listOf(statement.transform(this, null))
//            val receiver = statement.extensionReceiver!!
//            val receiverType = receiver.type
//            if (!receiverType.isArray() && !receiverType.isSubtypeOfClass(context.irBuiltIns.iterableClass)) return@transformFlat listOf(statement.transform(this, null))
//
//            // Narrowed it to a summation call on a collection, time to forcefully inline it!
//            if (function.valueParameters.isEmpty()) {
//                // sum()
//                listOf(function.inline(declaration))
//            } else {
//                // sumBy { ... } or sumOf { ... }
//                val lambda = buildVariable(declaration, function.startOffset, function.endOffset, IrDeclarationOrigin.LOCAL_FUNCTION_FOR_LAMBDA, function.name, context.irBuiltIns.functionN(1).typeWith(receiverType))
//                lambda.initializer = statement.getValueArgument(0)
//                listOf(
//                    lambda,
//                    function.inline(declaration, listOf(lambda))
//                )
//            }
//        }

        declaration.transformChildrenVoid(object : IrElementTransformerVoid() {
            override fun visitCall(expression: IrCall): IrExpression {
                val function = expression.symbol.owner
                if (function.getPackageFragment().packageFqName != FqName("kotlin.collections"))
                    return super.visitCall(expression)
                if (function.name.asString() !in listOf("sum", "sumBy", "sumOf")) return super.visitCall(expression)
                if (expression.extensionReceiver == null) return super.visitCall(expression)
                val receiver = expression.extensionReceiver!!
                val receiverType = receiver.type
                if (!receiverType.isArray() && !receiverType.isSubtypeOfClass(context.irBuiltIns.iterableClass)) return super.visitCall(expression)

                if (function.valueParameters.isEmpty()) {
                    // sum()
                    return function.inline(declaration)
                } else {
                    // sumBy { ... } or sumOf { ... }
                    val lambda = buildVariable(declaration, function.startOffset, function.endOffset, IrDeclarationOrigin.LOCAL_FUNCTION_FOR_LAMBDA, function.name, context.irBuiltIns.functionN(1).typeWith(receiverType))
                    lambda.initializer = expression.getValueArgument(0)

                    return function.inline(declaration, listOf(lambda))
                }
            }
        })

        return declaration
    }
}
