package sschr15.aocsolutions

import java.io.BufferedReader
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.bufferedReader
import kotlin.io.path.exists

const val maxValue = 2147483647

/**
 * Get a challenge's file
 * @param year the year of the challenge, or `0` for the part 3 challenge
 * @param day the day of the challenge, according to the Advent of Code website.
 * @return a [BufferedReader] pointing to the challenge's file
 */
fun getChallenge(year: Int, day: Int, separator: String? = "\n") =
    (if (year == 0) "part3" else "inputs/$year/day$day").let {
        if (Path(it).exists()) Path(it).bufferedReader()
        else Path("$it.txt").bufferedReader()
    }.use { it.readText() }.let {
        // return the input as a list of lines, or as a singleton list if the separator is null
        if (separator != null) it.split(separator) else listOf(it)
    }

class Grid<E> private constructor(val data: MutableList<MutableList<E>>) : Iterable<Iterable<E>> {
    val height: Int
        get() = data.size
    val width: Int
        get() = (data.map { it.size }.firstOrNull() ?: 0)

    fun getRow(row: Int) = data[row]
    fun getColumn(col: Int) = data.map { it[col] }.toMutableList()

    operator fun get(x: Int, y: Int) = data[y][x]
    operator fun get(point: Point) = data[point.y][point.x]
    operator fun set(x: Int, y: Int, value: E) {
        data[y][x] = value
    }
    operator fun set(point: Point, value: E) {
        data[point.y][point.x] = value
    }

    fun getNeighbors(point: Point, includeDiagonals: Boolean = true, searchDistance: Int = 1): Map<Point, E> {
        val points = getNeighboringPoints(point, includeDiagonals, searchDistance)
            .filter { it.x in 0 until width && it.y in 0 until height } // only get points in the grid
        return points.associateWith { this[it] }
    }

    /**
     * Returns a map where the keys are points and the values are the grid's values at their points.
     * ---
     *     12
     *     34
     * This example will create a map of `(0, 0) to 1, (0, 1) to 2, (1, 0) to 3, (1, 1) to 4`.
     */
    fun toPointMap() = map { it.mapIndexed { index, c -> index to c }.toMap() }
        .mapIndexed { y, map -> map.asIterable().associate { (x, c) -> Point(x, y) to c } }
        .reduce { acc, map -> acc.toMutableMap().also { it.putAll(map) } }

    companion object {
        fun <E> of(data: List<List<E>> = listOf()) = Grid(data.map { it.toMutableList() }.toMutableList())

        /**
         * Get the neighboring points of a given point
         * @param point the point you want neighboring points of
         * @param includeDiagonals should points diagonal to that point be included?
         * @return a list of neighboring points
         */
        fun getNeighboringPoints(point: Point, includeDiagonals: Boolean = true, searchDistance: Int = 1) =
            if (includeDiagonals) {
                (-searchDistance..searchDistance).flatMap { x ->
                    (-searchDistance..searchDistance).filter { x != 0 || it != 0 }.map { y ->
                        Point(point.x + x, point.y + y)
                    }
                }
            } else {
                (-searchDistance..searchDistance).flatMap { x ->
                    (-searchDistance..searchDistance).filter { x != it && x != -it }.map { y ->
                        Point(point.x + x, point.y + y)
                    }
                }
            }
    }

    override fun iterator(): Iterator<Iterable<E>> = data.iterator()
}

fun <T> Iterable<Iterable<T>>.toGrid() = Grid.of(this.toList().map { it.toList() })

fun Grid<Char>.stringify() = this.joinToString("\n") { it.joinToString("") }

data class Point(val x: Int, val y: Int) {
    override operator fun equals(other: Any?): Boolean {
        return when (other) {
            is Point -> x == other.x && y == other.y
            is MutablePoint -> x == other.x && y == other.y
            is Pair<*, *> -> x == other.first && y == other.second
            else -> false
        }
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }

    override fun toString(): String {
        return "($x, $y)"
    }
}
data class MutablePoint(var x: Int, var y: Int) {
    override operator fun equals(other: Any?): Boolean {
        return when (other) {
            is Point -> x == other.x && y == other.y
            is MutablePoint -> x == other.x && y == other.y
            is Pair<*, *> -> x == other.first && y == other.second
            else -> false
        }
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }

    override fun toString(): String {
        return "($x, $y)"
    }
}
data class MutableLongPoint(var x: Long, var y: Long) {
    override fun toString(): String {
        return "($x, $y)"
    }
}

private fun reconstruct(origins: Map<Point, Point>, current: Point): List<Point> {
    var thing = current
    val path = mutableListOf(thing)
    while (origins.containsKey(thing)) {
        thing = origins[thing]!!
        path.add(0, thing)
    }
    return path
}

fun Int.toString(chars: Int): String {
    return if (toString().length < chars) "${"0" * (chars - toString().length)}$this"
    else this.toString()
}

operator fun String.times(amount: Int) = this.repeat(amount)

operator fun String.get(start: Int, end: Int = length) = substring(start, end)

/**
 * Perform a binary tree traversal to get an index.
 * @param items a list of instructions
 * @param lo the instruction to state "the value is in the lower half of the remaining region"
 * @param hi the instruction to state "the value is in the upper half of the remaining region"
 * @param throwException should the method throw an exception if an item is neither [lo] or [hi]?
 * @return an index that would point to one specific spot in a list
 */
fun <T> binarySearch(items: List<T>, lo: T, hi: T, throwException: Boolean = true): Int {
    val len = items.size
    var lowBound = 0
    var highBound = (1 shl len) - 1
    for (i in 0 until len) {
        val bound = (highBound - lowBound) / 2
        if (items[i] == lo) highBound = lowBound + bound
        else if (items[i] == hi) lowBound = highBound - bound
        else if (throwException) throw IllegalArgumentException("${items[i]}")
    }
    return lowBound
}

operator fun <A> Pair<A, A>.get(index: Int) = when(index) {
    0 -> first
    1 -> second
    else -> throw IllegalArgumentException(index.toString())
}
