package sschr15.aocsolutions

import sschr15.aocsolutions.util.*

/**
 * AOC 2025 [Day 2](https://adventofcode.com/2025/day/2)
 * Challenge: elves messed up the database, we'll need to fix it
 */
object Day2 : Challenge {
    override fun solve() = challenge(2025, 2) {
//        test()

        part1 {
            inputLines.csv().sumOf { ranges ->
                val ranges = ranges.map { it.split("-").longs() }.map { (a, b) -> a..b }
                ranges.sumOf { range ->
                    range.asSequence()
                        .filter { it.toString().length and 1 == 0 }
                        .filter { it.toString().toList().divided(2).map(List<Char>::joinToString).let { (a, b) -> a == b } }
                        .sum()
                }
            }
        }
        part2 {
            inputLines.csv().sumOf { ranges ->
                val ranges = ranges.map { it.split("-").longs() }.map { (a, b) -> a..b }
                ranges.sumOf { range ->
                    range.asSequence()
                        .map { it.toString() }
                        .filter {
                            (1..it.length).any { i -> it.chunked(i).allEqual() && it.chunked(i).size >= 2 }
                        }
                        .sumOf { it.toLong() }
                }
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
