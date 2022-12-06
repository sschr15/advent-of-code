package sschr15.aocsolutions

import sschr15.aocsolutions.util.Challenge
import sschr15.aocsolutions.util.ReflectivelyUsed
import sschr15.aocsolutions.util.challenge

/**
 * AOC 2022 [Day 6](https://adventofcode.com/2022/day/6)
 * Challenge: weird number things
 */
object Day6 : Challenge {
    @ReflectivelyUsed
    override fun solve() = challenge(2022, 6) {
        part1 {
            val input = inputLines.first()
            // where's the first sequence of 4 unique letters?
            for ((i, chars) in input.windowed(4).mapIndexed { i, s -> i to s }) {
                if (chars.toSet().size == 4) {
                    submit(i + 4) // first character that isn't the fourth in a repeated sequence
                    return@part1
                }
            }
        }
        part2 {
            // 14 characters instead of 4
            val input = inputLines.first()
            for ((i, chars) in input.windowed(14).mapIndexed { i, s -> i to s }) {
                if (chars.toSet().size == 14) {
                    submit(i + 14)
                    return@part2
                }
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
