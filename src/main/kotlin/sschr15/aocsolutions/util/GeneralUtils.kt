package sschr15.aocsolutions.util

import java.io.BufferedReader
import java.io.File
import java.net.URI
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText
import kotlin.time.Duration

const val maxValue = 2147483647

annotation class ReflectivelyUsed

interface Challenge {
    fun solve(): Duration
}

sealed class Direction(val mod: (Point) -> Point) {
    object North : Direction(Point::up)
    object South : Direction(Point::down)
    object West : Direction(Point::left)
    object East : Direction(Point::right)

    override fun toString() = this::class.simpleName!![0].toString()
}

/**
 * Get a challenge's file
 * @param year the year of the challenge, or `0` for the part 3 challenge
 * @param day the day of the challenge, according to the Advent of Code website.
 * @return a [BufferedReader] pointing to the challenge's file
 */
fun getChallenge(year: Int, day: Int, separator: String? = "\n") =
    (if (year == 0) "part3" else "inputs/$year/day$day").let {
        val text = when {
            Path(it).exists() -> Path(it).readText()
            Path("$it.txt").exists() -> Path("$it.txt").readText()
            Path("session.txt").exists() -> {
                println("\u001b[1;103;30mWarning\u001b[0m: Could not find challenge file for $year day $day, downloading...")
                val session = Path("session.txt").readText().trim()
                val url = URI("https://adventofcode.com/$year/day/$day/input").toURL()
                val result = url.openConnection().apply {
                    setRequestProperty("Cookie", "session=$session")
                }.getInputStream().reader().readText()
                Path(it).writeText(result)
                result
            }
            else -> error("Could not find challenge file for $year day $day")
        }.replace("\r\n", "\n") // remove crlf, it breaks too many things (thanks windows)

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

    operator fun contains(point: AbstractPoint) = point.x() in 0 until width && point.y() in 0 until height

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

fun <T : Any> I2d<T>.stringify(converter: (T) -> String = Any::toString) = buildString {
    for (iter in this@stringify) {
        for (item in iter) {
            append(converter(item))
        }
        appendLine()
    }
}

fun <T : Any> A2d<T>.stringify(converter: (T) -> String = Any::toString) = buildString {
    for (arr in this@stringify) {
        for (item in arr) {
            append(converter(item))
        }
        appendLine()
    }
}

operator fun Int.plus(other: Boolean) = this + if (other) 1 else 0
operator fun Int.minus(other: Boolean) = this - if (other) 1 else 0
operator fun Int.times(other: Boolean) = if (other) this else 0
operator fun Int.div(other: Boolean) = if (other) this else throw ArithmeticException("Cannot divide by false")

/**
 * Gets the modulo of a number, but guarantees that the result is in the range `[0, mod)`.
 * It acts similar to Python's `%` operator, instead of Java's `%` operator
 * (which produces a negative result if the dividend is negative).
 *
 * Kotlin's `%` operator is equivalent to Java's `%` operator when running on the JVM, since
 * Kotlin compiles `%` to `irem` or `lrem` (depending on the type of the operands).
 */
infix fun Int.mod(other: Int) = (this % other + other) % other
