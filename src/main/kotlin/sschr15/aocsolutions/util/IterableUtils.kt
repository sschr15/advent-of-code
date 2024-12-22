@file:Suppress("NOTHING_TO_INLINE", "unused")

package sschr15.aocsolutions.util

import it.unimi.dsi.fastutil.objects.Object2IntMap
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import java.util.function.Consumer

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
    if (count == 2) return this.flatMapIndexed { i, t -> this.drop(i + 1).map { listOf(t, it) } }
    return this.flatMapIndexed { i, t -> this.drop(i + 1).combinations(count - 1).map { listOf(t) + it } }
}

fun <T> L1d<T>.pairs(): List<Pair<T, T>> {
    val result = arrayOfNulls<Pair<T, T>?>(size * (size - 1) / 2) as Array<Pair<T, T>>
    var index = 0
    for (i in indices) {
        for (j in i + 1..<size) {
            result[index++] = this[i] to this[j]
        }
    }
    return result.asList()
}

fun <T> L1d<T>.pairSequence(): Sequence<Pair<T, T>> {
    return sequence {
        for (i in indices) {
            for (j in i + 1 until size) {
                yield(this@pairSequence[i] to this@pairSequence[j])
            }
        }
    }
}

inline fun <T, R> Iterable<T>.mapParallel(crossinline transform: suspend (T) -> R): List<R> = runBlocking { 
    map { async(Dispatchers.Default) { transform(it) } }.awaitAll()
}

inline fun <T, R> Iterable<T>.mapIndexedParallel(crossinline transform: suspend (Int, T) -> R): List<R> = runBlocking {
    mapIndexed { index, t -> async(Dispatchers.Default) { transform(index, t) } }.awaitAll()
}

inline fun <K, V> Iterable<Map<K, V>>.combineMaps(): Map<K, List<V>> {
    val result = mutableMapOf<K, MutableList<V>>()
    for (map in this) {
        for ((key, value) in map) {
            result.getOrPut(key) { mutableListOf() }.add(value)
        }
    }
    return result
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

infix fun IntRange.rangeIntersect(other: IntRange): IntRange? {
    val start = maxOf(this.first, other.first)
    val end = minOf(this.last, other.last)
    return if (start <= end) start..end else null
}

infix fun LongRange.rangeIntersect(other: LongRange): LongRange? {
    val start = maxOf(this.first, other.first)
    val end = minOf(this.last, other.last)
    return if (start <= end) start..end else null
}

infix fun CharRange.rangeIntersect(other: CharRange): CharRange? {
    val start = maxOf(this.first, other.first)
    val end = minOf(this.last, other.last)
    return if (start <= end) start..end else null
}

@Suppress("UNCHECKED_CAST")
class EmptyCollection<T> : List<T>, Set<T>, Sequence<T>, Flow<T> {
    object EmptyIterator : ListIterator<Any?>, Spliterator<Any?> {
        override fun forEachRemaining(action: Consumer<in Any?>) = Unit
        override fun hasNext() = false
        override fun hasPrevious() = false
        override fun next() = throw NoSuchElementException("This data structure is empty")
        override fun nextIndex() = 0
        override fun previous() = throw NoSuchElementException("This data structure is empty")
        override fun previousIndex() = 0
        override fun tryAdvance(action: Consumer<in Any?>?) = false
        override fun trySplit(): Spliterator<Any?> = this
        override fun estimateSize() = 0L
        override fun characteristics() = Spliterator.IMMUTABLE
    }

    override val size = 0
    override fun contains(element: T) = false
    override fun containsAll(elements: Collection<T>) = false
    override fun get(index: Int): T = throw IndexOutOfBoundsException("This data structure is empty")
    override fun indexOf(element: T) = -1
    override fun isEmpty() = true
    override fun iterator() = EmptyIterator as Iterator<T>
    override fun lastIndexOf(element: T) = -1
    override fun listIterator() = EmptyIterator as ListIterator<T>
    override fun listIterator(index: Int) = EmptyIterator as ListIterator<T>
    override fun spliterator() = EmptyIterator as Spliterator<T>
    override fun subList(fromIndex: Int, toIndex: Int) = this
    override suspend fun collect(collector: FlowCollector<T>) = Unit

    companion object {
        val instance = EmptyCollection<Any?>()
    }
}

@Suppress("UNCHECKED_CAST")
fun <T> empty() = EmptyCollection.instance as EmptyCollection<T>

class MaxStatesSet<T> private constructor(
    private val statesToBits: Object2IntMap<T>,
    private val statesArray: Array<T>,
    private val currentState: BitSet
) : MutableSet<T> {
    constructor(states: Set<T>) : this(Object2IntOpenHashMap(states.size, 0.99f), arrayOfNulls<Any?>(states.size) as Array<T>, BitSet()) {
        for ((i, state) in states.withIndex()) {
            statesToBits.put(state, i)
            statesArray[i] = state
        }
    }

    override var size = 0
        private set

    override fun add(element: T): Boolean {
        val idx = statesToBits.getInt(element)
        val current = currentState[idx]
        currentState.set(idx)
        if (!current) size++
        return !current
    }

    override fun addAll(elements: Collection<T>): Boolean {
        var added = false
        for (t in elements) {
            val idx = statesToBits.getInt(t)
            val current = currentState[idx]
            added = added || current
            currentState.set(idx)
            if (!current) size++
        }

        return added
    }

    override fun clear() {
        currentState.clear()
        size = 0
    }

    override fun isEmpty() = size == 0

    override fun containsAll(elements: Collection<T>) = elements.stream().mapToInt(statesToBits::getInt).allMatch(currentState::get)

    override fun contains(element: T) = currentState[statesToBits.getInt(element)]

    override fun iterator() = object : MutableIterator<T> {
        var currentIndex = 0
        var hasRemoved = true

        override fun hasNext() = currentIndex != -1
        override fun next(): T {
            currentIndex = currentState.nextSetBit(currentIndex)
            val presentItem = statesArray[currentIndex++]
            hasRemoved = false
            return presentItem
        }

        override fun remove() {
            if (hasRemoved) throw IllegalStateException("Must call next before removal is possible")
            currentState.clear(currentIndex - 1)
            hasRemoved = true
        }
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        val itr = iterator()
        var removed = false
        while (itr.hasNext()) {
            if (itr.next() !in elements) {
                itr.remove()
                removed = true
            }
        }

        return removed
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        val itr = iterator()
        var removed = false
        while (itr.hasNext()) {
            if (itr.next() in elements) {
                itr.remove()
                removed = true
            }
        }

        return removed
    }

    override fun remove(element: T): Boolean {
        val idx = statesToBits.getInt(element)
        if (currentState[idx]) {
            currentState.clear(idx)
            return true
        }
        return false
    }

    fun immutable() = Immutable()

    inner class Immutable : Set<T> by this {
        operator fun plus(t: T) = MaxStatesSet(statesToBits, statesArray, BitSet().also { 
            it.or(currentState)
            it.set(statesToBits.getInt(t))
        }).Immutable()

        operator fun minus(t: T) = MaxStatesSet(statesToBits, statesArray, BitSet().also { 
            it.or(currentState)
            it.clear(statesToBits.getInt(t))
        }).Immutable()
    }
}

/**
 * Count the number of occurrences of each element in the iterable, and return the result as a map.
 */
fun <T> Iterable<T>.counts(): Map<T, Int> = groupingBy { it }.eachCount()

class DefaultMap<K, V> internal constructor(
    private val map: MutableMap<K, V>,
    private val defaultValue: V = USE_FUNCTION as V,
    private val defaultFunction: (K) -> V = { defaultValue },
) : MutableMap<K, V> by map {
    override operator fun get(key: K): V = map[key] ?: if (defaultValue != USE_FUNCTION) defaultValue else defaultFunction(key)
    override fun getOrDefault(key: K, defaultValue: V) = map.getOrDefault(key, defaultValue)

    companion object {
        private val USE_FUNCTION = Any()
    }
}

fun <K, V> Map<K, V>.default(default: V): DefaultMap<K, V> = DefaultMap(toMutableMap(), defaultValue = default)
fun <K, V> Map<K, V>.default(default: (K) -> V): DefaultMap<K, V> = DefaultMap(toMutableMap(), defaultFunction = default)
fun <K, V> Map<K, V>.default(default: () -> V): DefaultMap<K, V> = DefaultMap(toMutableMap()) { default() }
fun <K, V> defaultMap(default: V, vararg pairs: Pair<K, V>) = mutableMapOf(*pairs).default(default)

/**
 * Similar to [Iterable.chunked], but instead of creating a dynamic number of lists of a given size,
 * creates a fixed number of lists of a dynamic size.
 */
fun <T> List<T>.divided(divisions: Int): List<List<T>> {
    val chunkSize = (size + divisions - 1) / divisions
    return chunked(chunkSize)
}

/**
 * Similar to [Iterable.chunked], but instead of creating a dynamic number of lists of a given size,
 * creates a fixed number of lists of a dynamic size.
 */
fun <T, R> List<T>.divided(divisions: Int, transform: (List<T>) -> R): List<R> {
    val chunkSize = (size + divisions - 1) / divisions
    return chunked(chunkSize, transform)
}

/**
 * Similar to [Iterable.chunked], but instead of creating a dynamic number of lists of a given size,
 * creates a fixed number of lists of a dynamic size.
 *
 * This function doesn't maintain order.
 * Elements are placed in the lists in a round-robin fashion.
 */
fun <T> Iterable<T>.divided(divisions: Int): List<List<T>> {
    val lists = List(divisions) { mutableListOf<T>() }
    var i = 0
    for (element in this) {
        lists[i++ % divisions].add(element)
    }
    return lists
}

/**
 * Similar to [Iterable.chunked], but instead of creating a dynamic number of lists of a given size,
 * creates a fixed number of lists of a dynamic size.
 *
 * This function doesn't maintain order.
 * Elements are placed in the lists in a round-robin fashion.
 */
fun <T, R> Iterable<T>.divided(divisions: Int, transform: (List<T>) -> R): List<R> {
    val lists = List(divisions) { mutableListOf<T>() }
    var i = 0
    for (element in this) {
        lists[i++ % divisions].add(element)
    }
    return lists.map(transform)
}

inline fun <T> Iterable<T>.countIndexed(predicate: (Int, T) -> Boolean): Int {
    var count = 0
    var index = 0
    for (element in this) {
        if (predicate(index++, element)) count++
    }
    return count
}

inline fun <T> Iterable<T>.allIndexed(predicate: (Int, T) -> Boolean): Boolean {
    var index = 0
    for (element in this) {
        if (!predicate(index++, element)) return false
    }
    return true
}

inline fun <T> Iterable<T>.anyIndexed(predicate: (Int, T) -> Boolean): Boolean {
    var index = 0
    for (element in this) {
        if (predicate(index++, element)) return true
    }
    return false
}

inline fun <T> Iterable<T>.noneIndexed(predicate: (Int, T) -> Boolean): Boolean {
    var index = 0
    for (element in this) {
        if (predicate(index++, element)) return false
    }
    return true
}

inline fun List<Int>.indexOfMin(): Int {
    if (isEmpty()) return -1
    if (size == 1) return 0

    var min = Int.MAX_VALUE
    var minIndex = -1
    for ((i, value) in this.withIndex()) {
        if (value < min) {
            min = value
            minIndex = i
        }
    }
    if (minIndex == -1) return 0 // All values are Int.MAX_VALUE
    return minIndex
}

@JvmName("longIndexOfMin")
inline fun List<Long>.indexOfMin(): Int {
    if (isEmpty()) return -1
    if (size == 1) return 0

    var min = Long.MAX_VALUE
    var minIndex = -1
    for ((i, value) in this.withIndex()) {
        if (value < min) {
            min = value
            minIndex = i
        }
    }
    if (minIndex == -1) return 0
    return minIndex
}

@JvmName("floatIndexOfMin")
inline fun List<Float>.indexOfMin(): Int {
    if (isEmpty()) return -1
    if (size == 1) return 0

    var min = Float.POSITIVE_INFINITY
    var minIndex = -1
    for ((i, value) in this.withIndex()) {
        if (value < min) {
            min = value
            minIndex = i
        }
    }
    if (minIndex == -1) return 0
    return minIndex
}

@JvmName("doubleIndexOfMin")
inline fun List<Double>.indexOfMin(): Int {
    if (isEmpty()) return -1
    if (size == 1) return 0

    var min = Double.POSITIVE_INFINITY
    var minIndex = -1
    for ((i, value) in this.withIndex()) {
        if (value < min) {
            min = value
            minIndex = i
        }
    }
    if (minIndex == -1) return 0
    return minIndex
}

inline fun List<Int>.indexOfMax(): Int {
    if (isEmpty()) return -1
    if (size == 1) return 0

    var max = Int.MIN_VALUE
    var maxIndex = -1
    for ((i, value) in this.withIndex()) {
        if (value > max) {
            max = value
            maxIndex = i
        }
    }
    if (maxIndex == -1) return 0
    return maxIndex
}

@JvmName("longIndexOfMax")
inline fun List<Long>.indexOfMax(): Int {
    if (isEmpty()) return -1
    if (size == 1) return 0

    var max = Long.MIN_VALUE
    var maxIndex = -1
    for ((i, value) in this.withIndex()) {
        if (value > max) {
            max = value
            maxIndex = i
        }
    }
    if (maxIndex == -1) return 0
    return maxIndex
}

@JvmName("floatIndexOfMax")
inline fun List<Float>.indexOfMax(): Int {
    if (isEmpty()) return -1
    if (size == 1) return 0

    var max = Float.NEGATIVE_INFINITY
    var maxIndex = -1
    for ((i, value) in this.withIndex()) {
        if (value > max) {
            max = value
            maxIndex = i
        }
    }
    if (maxIndex == -1) return 0
    return maxIndex
}

@JvmName("doubleIndexOfMax")
inline fun List<Double>.indexOfMax(): Int {
    if (isEmpty()) return -1
    if (size == 1) return 0

    var max = Double.NEGATIVE_INFINITY
    var maxIndex = -1
    for ((i, value) in this.withIndex()) {
        if (value > max) {
            max = value
            maxIndex = i
        }
    }
    if (maxIndex == -1) return 0
    return maxIndex
}
