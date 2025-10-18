package sschr15.aocsolutions.util

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun <T> setOnce() = object : ReadWriteProperty<Any?, T> {
    private val UNINITIALIZED = Any()
    @Suppress("UNCHECKED_CAST")
    private var value: T = UNINITIALIZED as T

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (value === UNINITIALIZED) {
            throw IllegalStateException("Property ${property.name} not initialized")
        }
        return value
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        if (this.value !== UNINITIALIZED) {
            throw IllegalStateException("Property ${property.name} already initialized")
        }
        this.value = value
    }
}
