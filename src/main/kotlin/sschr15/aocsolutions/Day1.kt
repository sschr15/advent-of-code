@file:OptIn(ExperimentalTime::class)

package sschr15.aocsolutions

import sschr15.aocsolutions.util.Challenge
import sschr15.aocsolutions.util.ReflectivelyUsed
import sschr15.aocsolutions.util.getChallenge
import sschr15.aocsolutions.util.ints
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

/**
 * AOC 2022 [Day 1](https://adventofcode.com/2022/day/1)
 * Challenge: Find elf with most Calories then find sum of the highest three
 */
object Day1 : Challenge {
    @ReflectivelyUsed
    override fun solve() = measureTime {
        val raw = getChallenge(2022, 1, "\n\n")

        val data = raw.map { it.split("\n").ints() }

        val best = data.maxOf { it.sum() }
        println("Part 1: $best")
        val bestThree = data.sortedBy { it.sum() }.takeLast(3).sumOf { it.sum() }
        println("Part 2: $bestThree")
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
