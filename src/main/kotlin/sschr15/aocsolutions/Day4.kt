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
            inputLines.sumOf { s ->
                val winningNums = s.substringAfter(": ").substringBefore(" |").split(" ").mapNotNull { it.toIntOrNull() }.toSet()
                val nums = s.substringAfter("| ").split(" ").mapNotNull { it.toIntOrNull() }.toSet()
                2.0.pow((winningNums intersect nums).size - 1).toInt()
            }
        }
        part2 {
            val cardsToProcess = mutableListOf<Int>()
            var cardsProcessed = 0
            for (line in inputLines) {
                val winningNums = line.substringAfter(": ").substringBefore(" |").split(" ").mapNotNull { it.toIntOrNull() }.toSet()
                val nums = line.substringAfter("| ").split(" ").mapNotNull { it.toIntOrNull() }.toSet()
                val copies = cardsToProcess.removeFirstOrNull() ?: 0
                cardsProcessed += copies + 1
                val wins = (winningNums intersect nums).size
                repeat(wins) {
                    if (it >= cardsToProcess.size) cardsToProcess.add(0)
                    cardsToProcess[it] += 1 + copies
                }
            }
            cardsProcessed
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
