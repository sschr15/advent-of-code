package sschr15.aocsolutions

import com.sschr15.chekt.Memoize
import sschr15.aocsolutions.util.*
import java.util.TreeSet

/**
 * AOC 2025 [Day 5](https://adventofcode.com/2025/day/5)
 * Challenge: the cafeteria doesn't know how to do inventory management
 */
object Day5 : Challenge {
    override fun solve() = challenge(2025, 5) {
//        test()
        splitBy("\n\n")

        val ranges: List<LongRange>
        part1 {
            val (rangesString, ids) = inputLines
            ranges = rangesString.lines().map { it.split("-").longs() }.map { (a, b) -> a..b }
            ids.lines().longs().count {
                ranges.any { range -> it in range }
            }
        }
        part2 {
            var prevMax = Long.MIN_VALUE
            ranges.sortedBy { it.first }.sumOf { range ->
                if (range.last < prevMax) return@sumOf 0L
                val start = maxOf(prevMax + 1, range.first)
                prevMax = range.last
                range.last - start + 1
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
