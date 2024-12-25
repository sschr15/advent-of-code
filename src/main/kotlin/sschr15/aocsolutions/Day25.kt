package sschr15.aocsolutions

import sschr15.aocsolutions.util.*

/**
 * AOC 2024 [Day 25](https://adventofcode.com/2024/day/25)
 * Challenge: Figure out which keys and locks might be for the Chief Historian's office
 */
object Day25 : Challenge {
    override fun solve() = challenge(2024, 25) {
//        test()

        data class Thing(val a: Int, val b: Int, val c: Int, val d: Int, val e: Int)

        splitBy("\n\n")

        part1 {
            val locks = mutableListOf<IntArray>()
            val keys = mutableListOf<IntArray>()
            
            for (possibility in inputLines) {
                val grid = possibility.lines().toGrid()
                val list = IntArray(5)
                val isLock = grid.first().all { it == '#' }
                val toParse = if (!isLock) {
                    grid.reversed()
                } else grid

                val transposed = toParse.transpose()

                for (i in list.indices) {
                    list[i] = transposed[i].count { it == '#' } - 1
                }

                if (isLock) locks.add(list)
                else keys.add(list)
            }

            var sum = 0

            for (lock in locks) for (key in keys) {
                if (lock.toList().allIndexed { i, pos -> key[i] + pos < 6 }) {
//                    println("success: ${lock.s} ${key.s}")
                    sum++
                } else {
//                    println("failure: ${lock.s} ${key.s}")
                }
            }

            sum
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())

    val IntArray.s get() = joinToString(",")
}
