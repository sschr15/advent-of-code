package sschr15.aocsolutions

import sschr15.aocsolutions.util.Challenge
import sschr15.aocsolutions.util.ReflectivelyUsed
import sschr15.aocsolutions.util.challenge
import sschr15.aocsolutions.util.ints

/**
 * AOC 2023 [Day 1](https://adventofcode.com/2023/day/1)
 * Challenge: Find the first and last number, make them into a two digit number, and add them all together.
 */
object Day1 : Challenge {
    @ReflectivelyUsed
    override fun solve() = challenge(2023, 1) {
//        test()
        part1 {
            inputLines.map { s ->
                "${s.first { it.isDigit() }}${s.last { it.isDigit() }}"
            }.ints().sum()
        }
        part2 {
            val nums = mapOf(
                "zero" to 0,
                "one" to 1,
                "two" to 2,
                "three" to 3,
                "four" to 4,
                "five" to 5,
                "six" to 6,
                "seven" to 7,
                "eight" to 8,
                "nine" to 9,
            )

            val searchValues = nums.entries.flatMap { (name, num) -> listOf(name, num.toString()) }
            inputLines.sumOf { s ->
                val first = s.findAnyOf(searchValues)!!.let { (_, result) -> nums[result] ?: result.toInt() }
                val second = s.findLastAnyOf(searchValues)!!.let { (_, result) -> nums[result] ?: result.toInt() }
                first * 10 + second
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
