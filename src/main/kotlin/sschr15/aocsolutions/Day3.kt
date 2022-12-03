package sschr15.aocsolutions

import sschr15.aocsolutions.util.Challenge
import sschr15.aocsolutions.util.ReflectivelyUsed
import sschr15.aocsolutions.util.getChallenge
import kotlin.time.measureTime

/**
 * AOC 2022 [Day 3](https://adventofcode.com/2022/day/3)
 * Challenge: find duplicates
 */
object Day3 : Challenge {
    @ReflectivelyUsed
    override fun solve() = measureTime {
        val data = getChallenge(2022, 3)

        val inBoth = mutableListOf<Char>()
        for (sack in data) {
            val half = sack.length / 2
            val first = sack.substring(0, half)
            val second = sack.substring(half)

            val intersection = first.toSet() intersect second.toSet()
            assert(intersection.size == 1)
            inBoth.add(intersection.first())
        }
        val priorities = inBoth.map { when (it) {
            in 'a'..'z' -> it - 'a' + 1
            in 'A'..'Z' -> it - 'A' + 27
            else -> error("Invalid character: $it")
        } }
        println("Part 1: ${priorities.sum()}")

        // part two: find the one that's a duplicate in three different lines
        val inThree = mutableListOf<Char>()
        var i = 0
        while (i < data.size) {
            val sacks = data.subList(i, i + 3)
            i += 3
            val (first, second, third) = sacks
            val intersection = first.toSet() intersect second.toSet() intersect third.toSet()
            assert(intersection.size == 1)
            inThree.add(intersection.first())
        }
        val priorities2 = inThree.map { when (it) {
            in 'a'..'z' -> it - 'a' + 1
            in 'A'..'Z' -> it - 'A' + 27
            else -> error("Invalid character: $it")
        } }
        println("Part 2: ${priorities2.sum()}")
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
