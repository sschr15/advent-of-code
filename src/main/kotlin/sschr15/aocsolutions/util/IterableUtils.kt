@file:Suppress("NOTHING_TO_INLINE")

package sschr15.aocsolutions.util

import it.unimi.dsi.fastutil.objects.Object2IntMap
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
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

    override var size = currentState.cardinality()
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
        var idx = 0
        var hasRemoved = true

        override fun hasNext() = currentState.nextSetBit(idx).also { idx = it } != -1

        override fun next(): T {
            if (!hasNext()) throw NoSuchElementException("No more elements")
            hasRemoved = false
            return statesArray[idx++]
        }

        override fun remove() {
            if (hasRemoved) throw IllegalStateException("Cannot remove the same element twice")
            currentState.clear(idx - 1)
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

    override fun toString() = "MaxStatesSet(${statesArray.filterIndexed { i, _ -> currentState[i] }.joinToString(", ", "{", "}")})"

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

        override fun toString() = statesArray.filterIndexed { i, _ -> currentState[i] }.joinToString(", ", "{", "}")
    }
}

/**
 * Creates a sequence with an infinite number of elements, looping through the elements of this list.
 */
fun <T> List<T>.looping() = object : Sequence<T> {
    override fun iterator() = object : Iterator<T> {
        var idx = 0

        override fun hasNext() = true

        override fun next(): T {
            val result = this@looping[idx++]
            idx %= size
            return result
        }
    }
}

/**
 * Creates a sequence with an infinite number of elements, looping through the elements of this list and transforming each element.
 */
inline fun <T, R> List<T>.looping(crossinline transform: (T) -> R) = object : Sequence<R> {
    override fun iterator() = object : Iterator<R> {
        var idx = 0

        override fun hasNext() = true

        override fun next(): R {
            val result = transform(this@looping[idx++])
            idx %= size
            return result
        }
    }
}
