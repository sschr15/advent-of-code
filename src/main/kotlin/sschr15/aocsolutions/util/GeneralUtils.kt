package sschr15.aocsolutions.util

import java.io.BufferedReader
import java.io.File
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.time.Duration

const val maxValue = 2147483647

annotation class ReflectivelyUsed

interface Challenge {
    fun solve(): Duration
}

/**
 * Get a challenge's file
 * @param year the year of the challenge, or `0` for the part 3 challenge
 * @param day the day of the challenge, according to the Advent of Code website.
 * @return a [BufferedReader] pointing to the challenge's file
 */
fun getChallenge(year: Int, day: Int, separator: String? = "\n") =
    (if (year == 0) "part3" else "inputs/$year/day$day").let {
        val text = (if (Path(it).exists()) Path(it) else Path("$it.txt")).readText()
            .replace("\r\n", "\n")
        // return the input as a list of lines, or as a singleton list if the separator is null
        if (separator != null) text.split(separator) else listOf(text)
    }.let {
        // if the file ends with a newline, remove it
        if (it.last().isBlank()) it.dropLast(1) else it
    }

fun List<String>.ints() = map(String::toInt)
fun List<String>.csv() = map { it.split(",") }

class Grid<T> private constructor(private val data: MutableList<MutableList<T>>) : Iterable<Iterable<T>> {
    val height: Int
        get() = data.size
    val width: Int
        get() = (data.map { it.size }.firstOrNull() ?: 0)

    fun getRow(row: Int) = data[row]
    fun getColumn(col: Int) = data.map { it[col] }.toMutableList()

    operator fun get(x: Int, y: Int) = data[y][x]
    operator fun get(point: AbstractPoint) = data[point.y()][point.x()]
    operator fun set(x: Int, y: Int, value: T) {
        data[y][x] = value
    }
    operator fun set(point: AbstractPoint, value: T) {
        data[point.y()][point.x()] = value
    }

    fun getNeighbors(point: AbstractPoint, includeDiagonals: Boolean = true, searchDistance: Int = 1): Map<AbstractPoint, T> {
        val points = getNeighboringPoints(point, includeDiagonals, searchDistance)
            .filter { it.x() in 0 until width && it.y() in 0 until height } // only get points in the grid
        return points.associateWith { this[it] }
    }

    /**
     * Returns a map where the keys are points and the values are the grid's values at their points.
     * ---
     *     12
     *     34
     * This example will create a map of `(0, 0) to 1, (0, 1) to 2, (1, 0) to 3, (1, 1) to 4`.
     */
    fun toPointMap() = data.mapIndexed { y, row ->
        row.mapIndexed { x, value -> Point(x, y) to value }
    }.flatten().toMap()

    override fun toString(): String {
        val allStrings = data.map { it.map { t -> t.toString() } }
        val longestLength = allStrings.maxOfOrNull { it.maxByOrNull { s -> s.length }?.length ?: 0 } ?: 0
        val paddedStrings = allStrings.map { it.map { s -> s.padEnd(longestLength) } }
        return paddedStrings.joinToString("\n") { it.joinToString("") }
    }

    init {
        // remove empty rows because who knows what the input will be
        data.removeAll { it.isEmpty() }

        // ensure that the grid is rectangular
        require(data.all { it.size == data[0].size }) { "Grid must be rectangular" }
    }

    companion object {
        operator fun <E> invoke(data: List<List<E>> = listOf()) = Grid(data.map { it.toMutableList() }.toMutableList())

        inline operator fun <reified E> invoke(width: Int, height: Int, defaultValue: E) =
            invoke(Array(height) { Array(width) { defaultValue }.toMutableList() }.toMutableList())

        /**
         * Get the neighboring points of a given point
         * @param point the point you want neighboring points of
         * @param includeDiagonals should points diagonal to that point be included?
         * @return a list of neighboring points
         */
        fun getNeighboringPoints(point: AbstractPoint, includeDiagonals: Boolean = true, searchDistance: Int = 1) =
            (listOf(
                AbstractPoint::up,
                AbstractPoint::down,
                AbstractPoint::left,
                AbstractPoint::right,
            ) + if (includeDiagonals) listOf<(AbstractPoint) -> AbstractPoint>(
                { it.up().left() },
                { it.up().right() },
                { it.down().left() },
                { it.down().right() },
            ) else emptyList()).flatMap { buildList {
                var prev = point
                repeat(searchDistance) {
                    prev = it(prev)
                    add(prev)
                }
            } }
    }

    override fun iterator(): Iterator<Iterable<T>> = data.iterator()
}

fun <T> Iterable<Iterable<T>>.toGrid() = Grid(this.toList().map { it.toList() })

/**
 * Perform a binary tree traversal to get an index.
 * @param items a list of instructions
 * @param value the value to search for
 * @return an index that would point to one specific spot in a list
 */
fun <T : Comparable<T>> binarySearch(items: List<T>, value: T): Int {
    val len = items.size
    var lowBound = 0
    var highBound = (1 shl len) - 1
    for (i in 0 until len) {
        val bound = (highBound - lowBound) / 2
        when {
            value < items[i] -> highBound -= bound + 1
            value > items[i] -> lowBound += bound + 1
            else -> return lowBound + bound
        }
    }
    return lowBound
}

operator fun <T> Pair<T, T>.get(index: Int) = when(index) {
    0 -> first
    1 -> second
    else -> throw IllegalArgumentException(index.toString())
}

operator fun <E> List<E>.component6() = this[5]

/**
 * Get the least common multiple of a list of numbers.
 */
fun List<Int>.lcm() = reduce { acc, i -> lcm(acc, i) }

/**
 * Get the greatest common factor of a list of numbers.
 */
fun List<Int>.gcf() = reduce { acc, i -> gcf(acc, i) }

/**
 * Get the least common multiple of two numbers.
 */
fun lcm(a: Int, b: Int) = a * b / gcf(a, b)

/**
 * Get the greatest common factor of two numbers.
 */
@Suppress("NAME_SHADOWING")
fun gcf(a: Int, b: Int): Int {
    var a = a
    var b = b
    while (b != 0) {
        val temp = b
        b = a % b
        a = temp
    }
    return a
}

fun modAdd(a: Long, b: Long, mod: Int) = (a + b) % mod

fun modMult(a: Long, b: Long, mod: Int) = ((a % mod) * (b % mod)) % mod

interface Truthy {
    fun asBoolean(): Boolean
}

val Any?.truthiness get() = when (this) {
    null -> false
    is Array<*> -> isNotEmpty()
    is Boolean -> this
    is File -> exists()
    is Path -> exists() // must come before Iterable because Path implements Iterable
    is Iterable<*> -> iterator().hasNext()
    is Map<*, *> -> isNotEmpty()
    is Number -> this != 0
    is String -> isNotEmpty()
    is Truthy -> asBoolean()
    else -> true
}
