@file:Suppress("NOTHING_TO_INLINE")

package sschr15.aocsolutions.util

import java.math.BigDecimal
import java.math.BigInteger

/**
 * A [Map] which returns non-null values for missing keys.
 */
class SolidMap<K, V> internal constructor(private val map: Map<K, V>, private val default: (K) -> V) : Map<K, V> by map {
    override operator fun get(key: K) = map[key] ?: default(key)
    override fun getOrDefault(key: K, defaultValue: V) = map.getOrDefault(key, defaultValue)
}

fun <K, V> Map<K, V>.solid(default: V): SolidMap<K, V> = SolidMap(this) { default }
fun <K, V> Map<K, V>.solid(default: (K) -> V): SolidMap<K, V> = SolidMap(this, default)

inline fun <K, V> Map<K, MutableList<V>>.removeAll(value: V) {
    this.forEach { (_, list) ->
        list.remove(value)
    }
}

inline fun <T> Iterable<T>.containsAll(other: Iterable<T>): Boolean {
    return other.all { this.contains(it) }
}

inline fun <T> Iterable<T>.containsAll(vararg other: T): Boolean {
    return other.all { this.contains(it) }
}

inline fun MutableList<in Char>.addAll(other: CharSequence) {
    other.forEach { this.add(it) }
}

inline fun <K, V> Map<K, MutableList<V>>.addToAll(value: V, vararg keys: K) {
    keys.forEach { this[it]?.add(value) }
}

inline fun <K> Map<K, MutableList<in Char>>.addAllToAll(other: CharSequence, vararg keys: K) {
    keys.forEach { this[it]?.addAll(other) }
}

inline fun <T> List<T>.only(vararg indices: Int) = indices.map { this[it] }

inline fun <T> Array<T>.only(vararg indices: Int) = indices.map { this[it] }
inline fun CharArray.only(vararg indices: Int) = indices.map { this[it] }
inline fun ByteArray.only(vararg indices: Int) = indices.map { this[it] }
inline fun ShortArray.only(vararg indices: Int) = indices.map { this[it] }
inline fun IntArray.only(vararg indices: Int) = indices.map { this[it] }
inline fun LongArray.only(vararg indices: Int) = indices.map { this[it] }
inline fun FloatArray.only(vararg indices: Int) = indices.map { this[it] }
inline fun DoubleArray.only(vararg indices: Int) = indices.map { this[it] }

inline fun Iterable<Int>.mul() = this.fold(1) { acc, i -> acc * i }
inline fun Iterable<Long>.mul() = this.fold(1L) { acc, i -> acc * i }
inline fun Iterable<Float>.mul() = this.fold(1f) { acc, i -> acc * i }
inline fun Iterable<Double>.mul() = this.fold(1.0) { acc, i -> acc * i }
inline fun Iterable<BigInteger>.mul() = this.fold(1.toBigInteger()) { acc, i -> acc * i }
inline fun Iterable<BigDecimal>.mul() = this.fold(1.toBigDecimal()) { acc, i -> acc * i }

inline fun <T> Iterable<T>.multiplyBy(block: (T) -> Int) = this.map(block).mul()
inline fun <T> Iterable<T>.multiplyBy(block: (T) -> Long) = this.map(block).mul()
inline fun <T> Iterable<T>.multiplyBy(block: (T) -> Float) = this.map(block).mul()
inline fun <T> Iterable<T>.multiplyBy(block: (T) -> Double) = this.map(block).mul()
inline fun <T> Iterable<T>.multiplyBy(block: (T) -> BigInteger) = this.map(block).mul()
inline fun <T> Iterable<T>.multiplyBy(block: (T) -> BigDecimal) = this.map(block).mul()

inline fun <T> Iterable<T>.allEqual() = this.all { it == this.first() }
