package sschr15.aocsolutions.util

import java.io.BufferedReader
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.math.*

const val maxValue = 2147483647

/**
 * Get a challenge's file
 * @param year the year of the challenge, or `0` for the part 3 challenge
 * @param day the day of the challenge, according to the Advent of Code website.
 * @return a [BufferedReader] pointing to the challenge's file
 */
fun getChallenge(year: Int, day: Int, separator: String? = "\n") =
    (if (year == 0) "part3" else "inputs/$year/day$day").let {
        val text = (if (Path(it).exists()) Path(it) else Path("$it.txt")).readText()
        // return the input as a list of lines, or as a singleton list if the separator is null
        if (separator != null) text.split(separator) else listOf(text)
    }.let {
        // if the file ends with a newline, remove it
        if (it.last().isBlank()) it.dropLast(1) else it
    }

class Grid<T> private constructor(private val data: MutableList<MutableList<T>>) : Iterable<Iterable<T>> {
    val height: Int
        get() = data.size
    val width: Int
        get() = (data.map { it.size }.firstOrNull() ?: 0)

    fun getRow(row: Int) = data[row]
    fun getColumn(col: Int) = data.map { it[col] }.toMutableList()

    operator fun get(x: Int, y: Int) = data[y][x]
    operator fun get(point: Point) = data[point.y][point.x]
    operator fun set(x: Int, y: Int, value: T) {
        data[y][x] = value
    }
    operator fun set(point: Point, value: T) {
        data[point.y][point.x] = value
    }

    fun getNeighbors(point: Point, includeDiagonals: Boolean = true, searchDistance: Int = 1): Map<Point, T> {
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
    fun toPointMap() = data.mapIndexed { y, row ->
        row.mapIndexed { x, value -> Point(x, y) to value }
    }.flatten().toMap()

    override fun toString(): String {
        val allStrings = data.map { it.map { t -> t.toString() } }
        val longestLength = allStrings.map { it.maxByOrNull { s -> s.length }?.length ?: 0 }.maxOrNull() ?: 0
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
            invoke(Array(width) { Array(height) { defaultValue }.toMutableList() }.toMutableList())

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

    override fun iterator(): Iterator<Iterable<T>> = data.iterator()
}

fun <T> Iterable<Iterable<T>>.toGrid() = Grid(this.toList().map { it.toList() })

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

data class Line(val start: Point, val end: Point) {
    val length = sqrt((start.x - end.x).toDouble().pow(2) + (start.y - end.y).toDouble().pow(2))
    val slope = (start.y - end.y).toDouble() / (start.x - end.x).toDouble()
    private val yIntercept = start.y - slope * start.x
    val wholeNumberPoints by lazy {
        if (slope.isFinite()) {
            // not a vertical line
            val min = min(start.x, end.x)
            val max = max(start.x, end.x)
            (min..max)
                .map { it to it * slope + yIntercept }
                .filter { (_, y) -> y.roundToInt().toDouble() == y }
                .map { (x, y) -> Point(x, y.roundToInt()) }
        } else {
            // vertical line
            val min = min(start.y, end.y)
            val max = max(start.y, end.y)
            (min..max)
                .map { y -> Point(start.x, y) }
        }
    }

    companion object {
        operator fun invoke(x1: Int, y1: Int, x2: Int, y2: Int) = Line(Point(x1, y1), Point(x2, y2))
        operator fun invoke(points: Pair<Point, Point>) = Line(points.first, points.second)
    }
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
