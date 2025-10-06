package sschr15.aocsolutions

import com.sschr15.aoc.annotations.Memoize
import sschr15.aocsolutions.util.*
import kotlin.jvm.JvmStatic

/**
 * AOC 2024 [Day 19](https://adventofcode.com/2024/day/19)
 * Challenge: 
 */
object Day19 : Challenge {
    override fun solve() = challenge(2024, 19) {
//        test()

        splitBy("\n\n")

        lateinit var patterns: List<String>
        val designs: List<String>

        @Memoize
        fun patternCount(target: String): Long = patterns.seq
            .filter { target.startsWith(it) }
            .map { target.substringAfter(it) to it }
            .sumOf { (left, pattern) ->
                if (left.isEmpty()) 1L else patternCount(left)
            }

        part1 {
            patterns = inputLines.first().split(", ")
            designs = inputLines.last().lines()

            designs.count { patternCount(it) != 0L }
        }
        part2 {
            designs.sumOf { patternCount(it) }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
