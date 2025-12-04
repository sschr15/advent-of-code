package sschr15.aocsolutions

import sschr15.aocsolutions.util.*

/**
 * AOC 2025 [Day 1](https://adventofcode.com/2025/day/1)
 * Challenge: What a complicated way to retrieve a password, from a combination lock of all things
 */
object Day1 : Challenge {
    override fun solve() = challenge(2025, 1) {
//        test()

        part1 {
            inputLines.runningFold(50) { prev, line ->
                val dir = if (line.first() == 'R') 1 else -1
                val amount = line.drop(1).toInt()
                (prev + dir * amount) mod 100
            }.count { it == 0 }
        }
        part2 {
            var dial = 50
            var zeroes = 0
            for (line in inputLines) {
                val dir = if (line.first() == 'R') 1 else -1
                val amount = line.drop(1).toInt()

                repeat(amount) {
                    dial = (dial + dir) mod 100
                    zeroes += if (dial == 0) 1 else 0
                }
            }
            zeroes
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
