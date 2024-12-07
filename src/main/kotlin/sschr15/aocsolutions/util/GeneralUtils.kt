@file:Suppress("NAME_SHADOWING", "unused", "NOTHING_TO_INLINE")

package sschr15.aocsolutions.util

import kotlinx.datetime.*
import org.jsoup.Jsoup
import java.io.BufferedReader
import java.io.File
import java.net.URI
import java.nio.file.Path
import java.time.Month
import kotlin.io.path.*
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.time.Duration
import kotlin.time.toJavaDuration

const val maxValue = 2147483647

annotation class ReflectivelyUsed

interface Challenge {
    fun solve(): Duration
}

sealed class Direction {
    abstract fun mod(point: AbstractPoint): Point

    fun turnLeft() = when (this) {
        North -> West
        South -> East
        West -> South
        East -> North
    }

    fun turnRight() = when (this) {
        North -> East
        South -> West
        West -> North
        East -> South
    }

    object North : Direction() {
        override fun mod(point: AbstractPoint) = point.up()
    }

    object South : Direction() {
        override fun mod(point: AbstractPoint) = point.down()
    }

    object West : Direction() {
        override fun mod(point: AbstractPoint) = point.left()
    }

    object East : Direction() {
        override fun mod(point: AbstractPoint) = point.right()
    }

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
            Path("session.txt").exists() && day in 1..25 -> {
                println("\u001b[1;103;30mWarning\u001b[0m: Could not find challenge file for $year day $day, downloading...")

                val date = LocalDate(year, Month.DECEMBER, day)
                val dateTime = date.atTime(LocalTime(0, 0))
                val puzzleReleaseInstant = dateTime.toInstant(UtcOffset(hours = -5))

                val now = Clock.System.now()
                if (now < puzzleReleaseInstant) {
                    val timeUntilRelease = puzzleReleaseInstant - now
                    println("Waiting for puzzle release... ($timeUntilRelease)")
                    Thread.sleep(timeUntilRelease.toJavaDuration())
                }

                val session = Path("session.txt").readText().trim()
                val url = URI("https://adventofcode.com/$year/day/$day/input").toURL()
                val result = url.openConnection().apply {
                    setRequestProperty("Cookie", "session=$session")
                }.getInputStream().reader().readText()
                Path(it).apply { parent.createDirectories() }.writeText(result)
                result
            }
            Path("session.txt").exists() && day in 31..55 -> {
                val nonTestDay = day - 30
                println("\u001b[1;103;30mWarning\u001b[0m: Could not find test case for $year day $nonTestDay, guessing in download...")
                attemptDownloadTest(year, nonTestDay).also { s -> Path(it).writeText(s) }
            }
            day !in 1..25 && day !in 31..55 -> error("Day $day is not a valid day")
            else -> error("Could not find challenge file for $year day $day")
        }.replace("\r\n", "\n") // remove crlf, it breaks too many things (thanks windows)
            .trim()

        // return the input as a list of lines, or as a singleton list if the separator is null
        if (separator != null) text.split(separator) else listOf(text)
    }.let {
        // if the file ends with a newline, remove it
        if (it.last().isBlank()) it.dropLast(1) else it
    }

/**
 * Get the best guess of a challenge's test case. This isn't guaranteed to be correct.
 */
fun attemptDownloadTest(year: Int, day: Int): String {
    val session = Path("session.txt").readText().trim()
    val url = URI("https://adventofcode.com/$year/day/$day").toURL()
    val result = url.openConnection().apply {
        setRequestProperty("Cookie", "session=$session")
    }.getInputStream().reader().readText()

    val element = Jsoup.parse(result).select("main article pre code").first()
    requireNotNull(element) { "No test case found" }

    return element.text()
}

fun List<String>.ints() = map(String::toInt).map { sschr15.aocsolutions.util.watched.WatchedInt(it) } // WatchedInt checks for accidental overflow and underflow
fun List<String>.csv() = map { it.split(",") }

class Grid<T> private constructor(private val data: MutableList<MutableList<T>>) : Iterable<Iterable<T>> {
    val height: Int = data.size
    val width: Int = data.firstOrNull()?.size ?: 0

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

    operator fun contains(point: AbstractPoint) =
        point.x().let { it >= 0 && it < width } && point.y().let { it >= 0 && it < height }

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

    fun columns(): List<List<T>> = (0 until width).map { getColumn(it) }
    fun rows(): List<List<T>> = data

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

fun lcm(a: Long, b: Long) = a * b / gcf(a, b)

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

/**
 * Get the greatest common factor of two numbers.
 */
@Suppress("NAME_SHADOWING")
fun gcf(a: Long, b: Long): Long {
    var a = a
    var b = b
    while (b != 0L) {
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

fun pow(base: Number, exponent: Number) = base.toDouble().pow(exponent.toDouble())

fun powi(base: Int, exponent: Int): Int {
    // Special bases can always succeed (or fail for 0^0)
    if (base == 0) return if (exponent == 0) throw ArithmeticException("Undefined 0^0") else 0
    if (base == 1) return 1
    if (base == -1) return if (exponent and 1 == 0) 1 else -1

    // negative exponents result in fractions (not possible), and large exponents are just *too* large
    if (exponent < 0) throw IllegalArgumentException("Exponent must be non-negative")
    if (exponent >= 32) throw ArithmeticException("Overflow")

    // One last special base: 2
    if (base == 2) return 1 shl exponent

    // Otherwise, exponentiation by squaring (usually a pretty good solution)
    var base = base
    var exponent = exponent
    var result = 1
    while (exponent != 0) {
        if (exponent and 1 != 0) {
            result *= base
        }
        base *= base
        exponent = exponent ushr 1
    }

    return result
}

fun powi(base: Long, exponent: Int): Long {
    if (base == 0L) return if (exponent == 0) throw ArithmeticException("Undefined 0^0") else 0
    if (base == 1L) return 1
    if (base == -1L) return if (exponent and 1 == 0) 1 else -1

    if (exponent < 0) throw IllegalArgumentException("Exponent must be non-negative")
    if (exponent >= 64) throw ArithmeticException("Overflow")

    if (base == 2L) return 1L shl exponent

    var base = base
    var exponent = exponent
    var result = 1L
    while (exponent != 0) {
        if (exponent and 1 != 0) {
            result *= base
        }
        base *= base
        exponent = exponent ushr 1
    }

    return result
}

fun Long.pow(exponent: Int) = powi(this, exponent)

fun Iterable<Number>.stdDev(): Double {
    val mean = sumOf { it.toDouble() } / count()
    val sumOfSquares = sumOf { (it.toDouble() - mean).pow(2) }
    return sqrt(sumOfSquares / count())
}

fun Double.floorToInt() = floor(this).toInt()
fun Double.floorToLong() = floor(this).toLong()
fun Double.ceilingToInt() = ceil(this).toInt()
fun Double.ceilingToLong() = ceil(this).toLong()

class RecursiveCaller<T, R>(val f: RecursiveCaller<T, R>.(T) -> R) {
    fun recurse(value: T): R = f(value)
}

inline fun <T, R> memoized(crossinline f: RecursiveCaller<T, R>.(T) -> R): (T) -> R {
    val cache = mutableMapOf<T, R>()
    val caller = RecursiveCaller { value ->
        cache.getOrPut(value) { f(value) }
    }
    return caller::recurse
}

inline fun <A, B, R> RecursiveCaller<Pair<A, B>, R>.recurse(a: A, b: B) = recurse(a to b)
inline fun <A, B, C, R> RecursiveCaller<Triple<A, B, C>, R>.recurse(a: A, b: B, c: C) = recurse(Triple(a, b, c))

inline operator fun <A, B, R> ((Pair<A, B>) -> R).invoke(a: A, b: B) = invoke(a to b)
inline operator fun <A, B, C, R> ((Triple<A, B, C>) -> R).invoke(a: A, b: B, c: C) = invoke(Triple(a, b, c))

inline fun <A, B, R> memoized(crossinline f: RecursiveCaller<Pair<A, B>, R>.(A, B) -> R): (Pair<A, B>) -> R = memoized { (a, b) -> f(a, b) }
inline fun <A, B, C, R> memoized(crossinline f: RecursiveCaller<Triple<A, B, C>, R>.(A, B, C) -> R): (Triple<A, B, C>) -> R = memoized { (a, b, c) -> f(a, b, c) }

infix fun <A, B, C> Pair<A, B>.and(c: C) = Triple(first, second, c)

/**
 * Calculate the integer logarithm of a number. Efficient for small numbers.
 */
fun logiSmall(n: Int, base: Int): Int {
    if (n == 1) return 0

    var i = 0
    var p = 1
    while (p <= n) {
        p *= base
        i++
    }
    return i - 1
}

/**
 * Calculate the integer logarithm-base-10 of a number. Efficient for small numbers.
 */
fun log10iSmall(n: Int): Int {
    if (n == 1) return 0

    var i = 0
    var p = 1
    while (p <= n) {
        p *= 10
        i++
    }
    return i - 1
}

/**
 * Calculate the integer logarithm-base-10 of a number. Reasonably efficient for all numbers,
 * but [log10iSmall] is more efficient for small numbers.
 */
fun log10i(n: Int): Int {
    // The master of if statements
    // first, divide at e4 (giving less detriment to small numbers)
    return if (n >= 10_000) {
        // divide at e7
        if (n >= 10_000_000) {
            // check e7, e8, and e9 uniquely
            when {
                n >= 1_000_000_000 -> 9
                n >= 100_000_000 -> 8
                else -> 7
            }
        } else { // n < e7
            // check e4, e5, and e6 uniquely
            when {
                n >= 1_000_000 -> 6
                n >= 100_000 -> 5
                else -> 4
            }
        }
    } else { // n < e4
        // check e1, e2, and e3 (bonus: e0)
        when {
            n >= 1_000 -> 3
            n >= 100 -> 2
            n >= 10 -> 1
            else -> 0
        }
    }
}

/**
 * Calculate the integer logarithm of a number. Efficient for small numbers.
 */
fun logiSmall(n: Long, base: Long): Int {
    if (n == 1L) return 0

    var i = 0
    var p = 1L
    while (p <= n) {
        p *= base
        i++
    }
    return i - 1
}

/**
 * Calculate the integer logarithm-base-10 of a number. Efficient for small numbers.
 */
fun log10iSmall(n: Long): Int {
    if (n == 1L) return 0

    var i = 0
    var p = 1L
    while (p <= n) {
        p *= 10
        i++
    }
    return i - 1
}
