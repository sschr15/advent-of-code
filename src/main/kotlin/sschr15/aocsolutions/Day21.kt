package sschr15.aocsolutions

import com.sschr15.aoc.annotations.Memoize
import sschr15.aocsolutions.util.*

/**
 * AOC 2024 [Day 21](https://adventofcode.com/2024/day/21)
 * Challenge: 
 */
object Day21 : Challenge {
    override fun solve() = challenge(2024, 21) {
        test()

        // Adapted from lukebemish's solution - this puzzle completely destroyed my hopes and dreams

        val keypad = """
                789
                456
                123
                 0A
            """.trimIndent().lines().toGrid()
        val keyMap = keypad.toPointMap().map { (k, v) -> v to k }.toMap()

        val directions = """
                | ^A
                |<v>
            """.trimMargin().lines().toGrid()
        val dirMap = directions.toPointMap().map { (k, v) -> v to k }.toMap()

        fun getWays(start: Point, value: Char, map: Map<Char, Point>): List<String> {
            var reverse = true
            val s = buildString {
                val end = map[value]!!
                var startX = start.x
                if (startX == 0 && end.y == map[' ']!!.y) {
                    if (0 < end.x) {
                        append(">".repeat(end.x))
                        startX = end.x
                    }
                    reverse = false
                } else if (start.y == map[' ']!!.y && end.x == 0) {
                    reverse = false
                }

                if (start.y > end.y) append("^".repeat(start.y - end.y))
                if (start.y < end.y) append("v".repeat(end.y - start.y))
                if (startX > end.x) append("<".repeat(startX - end.x))
                if (startX < end.x) append(">".repeat(end.x - startX))
            }

            if (s == s.reversed() || !reverse) return listOf(s)
            return listOf(s, s.reversed())
        }

        fun findWays(sequence: String, map: Map<Char, Point>): List<String> {
            var results = listOf(emptyList<Char>() to map['A']!!)
            sequence.forEach { c ->
                results = results.flatMap { (path, start) ->
                    getWays(start, c, map).map { s -> (path + s.toList()  + 'A') to map[c]!! }
                }
            }
            val min = results.minOf { (path) -> path.size }
            return results.mapNotNull { (path) -> path.takeIf { it.size == min }?.joinToString("") }
        }

        @Memoize
        fun shortestDirectionalPath(iterations: Int, goal: String): Long {
            if (iterations == -1) return goal.length.toLong()
            val sections = goal.split('A').dropLastWhile { it.isEmpty() }.map { it + "A" }
            return sections.sumOf { section -> findWays(section, dirMap).minOf { shortestDirectionalPath(iterations - 1, it) } }
        }

        fun shortestKeypadPath(iterations: Int, goal: String): Long {
            // Separate function to keep from memoization
            return findWays(goal, keyMap).minOf { shortestDirectionalPath(iterations - 1, it) }
        }

        part1 {
            inputLines.sumOf { shortestKeypadPath(2, it) * it.takeWhile(Char::isDigit).toLong() }
        }
        part2 {
            inputLines.sumOf { shortestKeypadPath(25, it).also(::println) * it.takeWhile(Char::isDigit).toLong() }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
