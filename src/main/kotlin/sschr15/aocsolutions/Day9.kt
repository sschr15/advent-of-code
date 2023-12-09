package sschr15.aocsolutions

import sschr15.aocsolutions.util.*
import java.util.Stack

/**
 * AOC 2023 [Day 9](https://adventofcode.com/2023/day/9)
 * Challenge: Extrapolate data that the Oasis And Sand Instability Sensor (OASIS) is giving you
 * (first because you'd like to predict the future, and second because "more history" by extrapolating backwards is good)
 */
object Day9 : Challenge {
    @ReflectivelyUsed
    override fun solve() = challenge(2023, 9) {
//        test()
        part1 {
            inputLines.sumOf { line ->
                val nums = line.findLongs()
                val differences = Stack<List<Long>>()
                differences.push(nums)

                var working = nums.toMutableList()
                while (working.any { it != 0L }) {
                    val next = working.mapIndexedNotNull { index, l ->
                        if (index == 0) null
                        else l - working[index - 1]
                    }
                    differences.push(next)
                    working = next.toMutableList()
                }

                var current = 0L
                while (differences.isNotEmpty()) {
                    val diff = differences.pop()
                    if (diff.isEmpty()) {
                        continue
                    }
                    current += diff.lastOrNull() ?: 0
                }
                current
            }
        }
        part2 {
            inputLines.sumOf { line ->
                val nums = line.findLongs()
                val differences = Stack<List<Long>>()
                differences.push(nums)

                var working = nums.toMutableList()
                while (working.any { it != 0L }) {
                    val next = working.mapIndexedNotNull { index, l ->
                        if (index == 0) null
                        else l - working[index - 1]
                    }
                    differences.push(next)
                    working = next.toMutableList()
                }

                var current = 0L
                while (differences.isNotEmpty()) {
                    val diff = differences.pop()
                    if (diff.isEmpty()) {
                        continue
                    }
                    current = diff.first() - current
                }
                current
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
