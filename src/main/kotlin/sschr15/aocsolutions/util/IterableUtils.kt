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
        list.removeAll { it == value }
    }
}

inline fun <T> I1d<T>.containsAll(other: I1d<T>): Boolean {
    return other.all { this.contains(it) }
}

inline fun <T> I1d<T>.containsAll(vararg other: T): Boolean {
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

inline fun <T> L1d<T>.only(vararg indices: Int) = indices.map { this[it] }

inline fun <T> A1d<T>.only(vararg indices: Int) = indices.map { this[it] }
inline fun CharArray.only(vararg indices: Int) = indices.map { this[it] }
inline fun ByteArray.only(vararg indices: Int) = indices.map { this[it] }
inline fun ShortArray.only(vararg indices: Int) = indices.map { this[it] }
inline fun IntArray.only(vararg indices: Int) = indices.map { this[it] }
inline fun LongArray.only(vararg indices: Int) = indices.map { this[it] }
inline fun FloatArray.only(vararg indices: Int) = indices.map { this[it] }
inline fun DoubleArray.only(vararg indices: Int) = indices.map { this[it] }

inline fun I1d<Int>.mul() = this.fold(1) { acc, i -> acc * i }
inline fun I1d<Long>.mul() = this.fold(1L) { acc, i -> acc * i }
inline fun I1d<Float>.mul() = this.fold(1f) { acc, i -> acc * i }
inline fun I1d<Double>.mul() = this.fold(1.0) { acc, i -> acc * i }
inline fun I1d<BigInteger>.mul() = this.fold(1.toBigInteger()) { acc, i -> acc * i }
inline fun I1d<BigDecimal>.mul() = this.fold(1.toBigDecimal()) { acc, i -> acc * i }

inline fun <T> I1d<T>.multiplyBy(block: (T) -> Int) = this.map(block).mul()
inline fun <T> I1d<T>.multiplyBy(block: (T) -> Long) = this.map(block).mul()
inline fun <T> I1d<T>.multiplyBy(block: (T) -> Float) = this.map(block).mul()
inline fun <T> I1d<T>.multiplyBy(block: (T) -> Double) = this.map(block).mul()
inline fun <T> I1d<T>.multiplyBy(block: (T) -> BigInteger) = this.map(block).mul()
inline fun <T> I1d<T>.multiplyBy(block: (T) -> BigDecimal) = this.map(block).mul()

inline fun <T> I1d<T>.allEqual() = this.all { it == this.first() }

fun <T> I2d<T>.transpose(throwIfUneven: Boolean = true): L2d<T> {
    val i = iterator()
    if (!i.hasNext()) return emptyList()
    val result = mutableListOf<List<T>>()
    val iters = map { it.iterator() }
    while (iters.all { it.hasNext() }) {
        result.add(iters.map { it.next() })
    }

    if (throwIfUneven && iters.any { it.hasNext() }) {
        throw IllegalArgumentException("Not all iterables are the same length!")
    }

    return result
}

operator fun <T> L2d<T>.get(point: AbstractPoint) = this[point.y()][point.x()]
operator fun <T> A2d<T>.get(point: AbstractPoint) = this[point.y()][point.x()]

fun <T> I1d<T>.combinations(count: Int): List<List<T>> {
    if (count == 0) return emptyList()
    if (count == 1) return this.map { listOf(it) }
    if (count == 2) return this.mapIndexed { i, t -> this.drop(i + 1).map { listOf(t, it) } }.flatten()
    return this.mapIndexed { i, t -> this.drop(i + 1).combinations(count - 1).map { listOf(t) + it } }.flatten()
}

fun <A : Any, B> Iterable<Pair<A?, B>>.filterFirstNotNull() = filter { it.first != null }.map { it.first!! to it.second }
fun <A, B : Any> Iterable<Pair<A, B?>>.filterSecondNotNull() = filter { it.second != null }.map { it.first to it.second!! }

val IntRange.range get() = last - first + 1
val LongRange.range get() = last - first + 1
val CharRange.range get() = last - first + 1

val ClosedFloatingPointRange<Float>.range get() = endInclusive - start
@get:JvmName("doubleRange")
val ClosedFloatingPointRange<Double>.range get() = endInclusive - start

fun <T> Iterable<T>.repeat(times: Int): List<T> = List(times) { this }.flatten()
