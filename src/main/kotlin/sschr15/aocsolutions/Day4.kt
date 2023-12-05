package sschr15.aocsolutions

import sschr15.aocsolutions.util.*
import kotlin.math.pow

/**
 * AOC 2023 [Day 4](https://adventofcode.com/2023/day/4)
 * Challenge: Win the lottery, or something like that
 */
object Day4 : Challenge {
    @ReflectivelyUsed
    override fun solve() = challenge(2023, 4) {
//        test()
        part1 {
            inputLines.sumOf {
                val (winningNums, nums) =
                    it.substringAfter(":")
                        .split("|")
                        .map(String::findNumbers)

                2 pow (winningNums.toSet() intersect nums.toSet()).size - 1
            }
        }
        part2 {
            val cardsToProcess = mutableListOf<Int>()
            var cardsProcessed = 0
            for (line in inputLines) {
                val (winningNums, nums) =
                    line.substringAfter(":")
                        .split("|")
                        .map(String::findNumbers)

                val cards = cardsToProcess.removeFirstOrNull() ?: 1
                cardsProcessed += cards
                repeat((winningNums.toSet() intersect nums.toSet()).size) {
                    if (it >= cardsToProcess.size) cardsToProcess.add(1) // Add the original card
                    cardsToProcess[it] += cards // Add all the copies
                }
            }
            cardsProcessed
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
