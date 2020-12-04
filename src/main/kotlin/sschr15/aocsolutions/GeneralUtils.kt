package sschr15.aocsolutions

import java.io.BufferedReader
import java.nio.file.Files
import java.nio.file.Path

val maxValue = 2147483647

/**
 * Get a challenge's file
 * @param year the year of the challenge, or `0` for the part 3 challenge
 * @param day the day of the challenge, according to the Advent of Code website.
 * @return a [BufferedReader] pointing to the challenge's file
 */
fun getChallenge(year: Int, day: Int): BufferedReader =
    Files.newBufferedReader(Path.of(if (year == 0) "part3" else "inputs/$year", "day$day"))

class Grid<E> private constructor(val data: MutableList<MutableList<E>>) : Iterable<Iterable<E>> {
    val height: Int
        get() = data.size
    val width: Int
        get() = (data.map { it.size }.firstOrNull() ?: 0)

    operator fun get(x: Int, y: Int) = data[y][x]
    operator fun get(point: Point) = data[point.y][point.x]
    operator fun set(x: Int, y: Int, value: E) {
        data[y][x] = value
    }
    operator fun set(point: Point, value: E) {
        data[point.y][point.x] = value
    }

    fun flatten() = data.flatten()

    companion object {
        fun <E> of(data: List<List<E>> = listOf()) = Grid(data.map { it.toMutableList() }.toMutableList())
    }

    override fun iterator(): Iterator<Iterable<E>> = data.iterator()
}

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

private fun reconstruct(origins: Map<Point, Point>, current: Point): List<Point> {
    var thing = current
    val path = mutableListOf(thing)
    while (origins.containsKey(thing)) {
        thing = origins[thing]!!
        path.add(0, thing)
    }
    return path
}

/**
 * A* algorithm implemented in (hopefully partially) easy-to-understand Kotlin.
 * @param start the start point
 * @param end the goal point
 * @param h heuristic function: a calculator for how hard it is to reach the goal point, as an estimate
 * @param neighbors a list of all neighbors of a given input point
 * @param d the weight of the distance from a point to a neighbor
 * @return a path to follow to get from [start] to [end]
 */
fun aStarAlgorithm(start: Point, end: Point, h: (Point) -> Int, neighbors: (Point) -> List<Point>, d: (Point, Point) -> Int): List<Point>? {
    val openNodes = mutableListOf(start)
    val origins = mutableMapOf<Point, Point>()
    val g = mutableMapOf(start to 0)
    val f = mutableMapOf(start to h(start))

    while (openNodes.isNotEmpty()) {
        val current = openNodes.minByOrNull { f.getOrDefault(it, maxValue) }!!
        if (current == end) return reconstruct(origins, current)

        openNodes.remove(current)

        neighbors(current).forEach {
            val tent = g.getOrDefault(it, maxValue) + d(current, it)
            if (tent < g.getOrDefault(it, maxValue)) {
                origins[it] = current
                g[it] = tent
                f[it] = tent + h(it)
                if (!openNodes.contains(it)) openNodes.add(it)
            }
        }
    }

    return null
}