package sschr15.aocsolutions

import sschr15.aocsolutions.util.Challenge
import sschr15.aocsolutions.util.ReflectivelyUsed
import sschr15.aocsolutions.util.challenge
import sschr15.aocsolutions.util.ints
import sschr15.aocsolutions.util.watched.sum
import sschr15.aocsolutions.util.watched.watched

/**
 * AOC 2023 [Day 2](https://adventofcode.com/2023/day/2)
 * Challenge: TODO (based on the day's description)
 */
object Day2 : Challenge {
    @ReflectivelyUsed
    override fun solve() = challenge(2023, 2) {
//        test()
        part1 {
            val (maxRed, maxGreen, maxBlue) = arrayOf(12, 13, 14)
            inputLines.filter { 
                val (_, data) = it.split(": ")
                val matches = data.split("; ")
                matches.all { s ->
                    val pulls = s.split(", ")
                    pulls.none { pull ->
                        val (amount, color) = pull.split(" ")
                        when (color.trimEnd(',')) {
                            "red" -> amount.toInt() > maxRed
                            "green" -> amount.toInt() > maxGreen
                            "blue" -> amount.toInt() > maxBlue
                            else -> error("Unknown color: $color")
                        }
                    }
                }
            }.map { it.substringBefore(":").substringAfter(" ") }.ints().sum()
        }
        part2 {
            inputLines.map {
                val (_, data) = it.split(": ")
                val matches = data.split("; ")
                val pulls = matches.flatMap { s -> s.split(", ") }
                var maxRed = 0
                var maxGreen = 0
                var maxBlue = 0

                pulls.forEach { pull ->
                    val (amount, color) = pull.split(" ")
                    when (color.trimEnd(',')) {
                        "red" -> maxRed = maxRed.coerceAtLeast(amount.toInt())
                        "green" -> maxGreen = maxGreen.coerceAtLeast(amount.toInt())
                        "blue" -> maxBlue = maxBlue.coerceAtLeast(amount.toInt())
                        else -> error("Unknown color: $color")
                    }
                }

                maxRed.watched() * maxGreen * maxBlue
            }.sum()
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
