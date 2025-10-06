package sschr15.aocsolutions.util

inline val <T> Iterable<T>.seq get() = asSequence()

//inline fun <T> Sequence<T>.only(vararg indices: Int): Sequence<T> {
//    val indexSet = BitSet()
//    indices.forEach { indexSet.set(it) }
//
//    return filterIndexed { i, _ -> indexSet[i] }
//}

inline fun Sequence<Int>.mul() = fold(1) { acc, i -> acc * i }
inline fun Sequence<Long>.mul() = fold(1L) { acc, i -> acc * i }
inline fun Sequence<Float>.mul() = fold(1f) { acc, i -> acc * i }
inline fun Sequence<Double>.mul() = fold(1.0) { acc, i -> acc * i }
//inline fun Sequence<BigInteger>.mul() = fold(1.toBigInteger()) { acc, i -> acc * i }
//inline fun Sequence<BigDecimal>.mul() = fold(1.toBigDecimal()) { acc, i -> acc * i }

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

class LimitedSequence<T>(
    limit: Int,
    private val first: T = NOT_INITIALIZED as T,
    private val generateFirst: () -> T = { error("No generation passed") },
    private val generateNext: (T) -> T
) : Iterator<T> {
    private var current = if (first === NOT_INITIALIZED) generateFirst() else first
    private var left = limit

    override fun hasNext(): Boolean {
        return left > 0
    }

    override fun next(): T {
        if (left-- <= 0) throw NoSuchElementException()
        val result = current
        current = generateNext(current)
        return result
    }

    companion object {
        private val NOT_INITIALIZED = Any()
    }
}

fun <T> generateLimitedSequence(limit: Int, seed: T, generateNext: (T) -> T): Sequence<T> {
    return object : Sequence<T> {
        override fun iterator(): Iterator<T> {
            return LimitedSequence(limit, seed, generateNext = generateNext)
        }
    }
}

fun <T> generateLimitedSequence(limit: Int, generateFirst: () -> T, generateNext: (T) -> T): Sequence<T> {
    return object : Sequence<T> {
        override fun iterator(): Iterator<T> {
            return LimitedSequence(limit, generateFirst = generateFirst, generateNext = generateNext)
        }
    }
}
