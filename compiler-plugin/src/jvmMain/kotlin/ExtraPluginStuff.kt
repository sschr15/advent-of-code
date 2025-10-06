@file:OptIn(UnsafeDuringIrConstructionAPI::class)

package com.sschr15.aoc.compiler.internal

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.util.getPropertyGetter
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class ExtraPluginStuff(pluginContext: IrPluginContext) {
    val collectionClass = pluginContext.irBuiltIns.collectionClass
    val not = pluginContext.irBuiltIns.booleanNotSymbol
    val sizeFunctionSymbol = collectionClass.getPropertyGetter("size")!!

    val illegalArgumentExceptionCtor = (
            pluginContext.referenceConstructors(ClassId(FqName("kotlin"), Name.identifier("IllegalArgumentException"))) +
                    pluginContext.referenceConstructors(ClassId(FqName("java.lang"), Name.identifier("IllegalArgumentException")))
            ).first { it.owner.parameters.singleOrNull()?.type?.classFqName?.asString()?.matches("""(kotlin|java\.lang)\.String""".toRegex()) == true }
}
