package com.sschr15.aoc.compiler.internal

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.starProjectedType
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.getPropertyGetter
import org.jetbrains.kotlin.ir.util.properties
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class ExtraPluginStuff(val pluginContext: IrPluginContext) {
    val unitType = pluginContext.irBuiltIns.unitType
    val booleanType = pluginContext.irBuiltIns.booleanType
    val intType = pluginContext.irBuiltIns.intType
    val stringType = pluginContext.irBuiltIns.stringType

    val collectionClass = pluginContext.irBuiltIns.collectionClass
    val collectionStarType = collectionClass.starProjectedType

    val printStreamClass = pluginContext.referenceClass(ClassId(FqName("java.io"), Name.identifier("PrintStream")))!!
    val printStreamType = printStreamClass.defaultType

    val systemClass = pluginContext.referenceClass(ClassId(FqName("java.lang"), Name.identifier("System")))!!
    val systemErrField = systemClass.owner.properties.single { it.name.asString() == "err" }.backingField!!.symbol

    val andAnd = pluginContext.irBuiltIns.andandSymbol
    val eqEq = pluginContext.irBuiltIns.eqeqSymbol
    val not = pluginContext.irBuiltIns.booleanNotSymbol

    val sizeFunctionSymbol = collectionClass.getPropertyGetter("size")!!
    val printStreamPrintlnFunctionSymbol =
        printStreamClass.functions.single { it.owner.name.asString() == "println" && it.owner.valueParameters.singleOrNull()?.type == pluginContext.irBuiltIns.anyNType }

    val illegalArgumentExceptionCtor = (
            pluginContext.referenceConstructors(ClassId(FqName("kotlin"), Name.identifier("IllegalArgumentException"))) +
                    pluginContext.referenceConstructors(ClassId(FqName("java.lang"), Name.identifier("IllegalArgumentException")))
            ).first { it.owner.valueParameters.singleOrNull()?.type?.classFqName?.asString()?.matches("""(kotlin|java\.lang)\.String""".toRegex()) == true }
}
