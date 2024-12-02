package sschr15.aocsolutions

import sschr15.aocsolutions.util.*
import sschr15.aocsolutions.util.watched.*

/**
 * AOC 2024 [Day 1](https://adventofcode.com/2024/day/1)
 * Challenge: Make sure two historic location lists are "close enough"
 */
object Day1 : Challenge {
    override fun solve() = challenge(2024, 1) {
        val parts: List<List<WatchedInt>>
        val a: List<WatchedInt>
        val b: List<WatchedInt>
        part1 {
            parts = inputLines.map { it.split("   ").ints() }
            a = parts.map { it[0] }
            b = parts.map { it[1] }
            a.sorted().zip(b.sorted())
                .sumOf { (a, b) -> (b - a).absoluteValue }
        }
        part2 {
            val freqs = b.counts()
            a.sumOf { (freqs[it] ?: 0) * it }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
