package sschr15.aocsolutions.util

import java.math.BigDecimal
import java.math.BigInteger
import java.util.*

inline val <T> Iterable<T>.seq get() = asSequence()

inline fun <T> Sequence<T>.only(vararg indices: Int): Sequence<T> {
    val indexSet = BitSet()
    indices.forEach { indexSet.set(it) }

    return filterIndexed { i, _ -> indexSet[i] }
}

inline fun Sequence<Int>.mul() = fold(1) { acc, i -> acc * i }
inline fun Sequence<Long>.mul() = fold(1L) { acc, i -> acc * i }
inline fun Sequence<Float>.mul() = fold(1f) { acc, i -> acc * i }
inline fun Sequence<Double>.mul() = fold(1.0) { acc, i -> acc * i }
inline fun Sequence<BigInteger>.mul() = fold(1.toBigInteger()) { acc, i -> acc * i }
inline fun Sequence<BigDecimal>.mul() = fold(1.toBigDecimal()) { acc, i -> acc * i }

inline fun <T> Sequence<T>.allEqual() = windowed(2).all { (a, b) -> a == b }

inline fun <T> Sequence<T>.repeat(n: Int): Sequence<T> {
    return sequence {
        repeat(n) {
            yieldAll(this@repeat)
        }
    }
}

inline fun <T> Sequence<T>.countIndexed(predicate: (Int, T) -> Boolean): Int {
    var count = 0
    var index = 0
    for (element in this) {
        if (predicate(index++, element)) count++
    }
    return count
}

inline fun <T> Sequence<T>.allIndexed(predicate: (Int, T) -> Boolean): Boolean {
    var index = 0
    for (element in this) {
        if (!predicate(index++, element)) return false
    }
    return true
}

inline fun <T> Sequence<T>.anyIndexed(predicate: (Int, T) -> Boolean): Boolean {
    var index = 0
    for (element in this) {
        if (predicate(index++, element)) return true
    }
    return false
}

inline fun <T> Sequence<T>.noneIndexed(predicate: (Int, T) -> Boolean): Boolean {
    var index = 0
    for (element in this) {
        if (predicate(index++, element)) return false
    }
    return true
}
