package sschr15.aocsolutions

import sschr15.aocsolutions.util.*

/**
 * AOC 2024 [Day 11](https://adventofcode.com/2024/day/11)
 * Challenge: 
 */
object Day11 : Challenge {
    override fun solve() = challenge(2024, 11) {
//        test()

        splitBy(" ")

        var state: List<Long>
        part1 {
            state = inputLines.map { it.toLong() }
            repeat(25) {
                val newState = mutableListOf<Long>()
                for (s in state) {
                    if (s == 0L) {
                        newState.add(1)
                    } else if (log10iSmall(s) % 2 == 1) {
                        val half = (log10iSmall(s) + 1) / 2
                        val pow10 = powi(10, half)
                        newState.add(s / pow10)
                        newState.add(s % pow10)
                    } else {
                        newState.add(s * 2024)
                    }
                }
                state = newState
//                println(state.joinToString(" "))
            }
            state.size
        }
        part2 {
            var state1 = inputLines.map { it.toLong() }.counts().mapValues { (_, v) -> v.toLong() }
            repeat(75) {
                val newState = mutableMapOf<Long, Long>().default(0)
                for ((s, n) in state1.entries) {
                    if (s == 0L) newState[1L] += n
                    else if (log10iSmall(s) % 2 == 1) {
                        val half = (log10iSmall(s) + 1) / 2
                        val pow10 = powi(10, half)
                        newState[s / pow10] += n
                        newState[s % pow10] += n
                    } else {
                        newState[s * 2024] += n
                    }
                }
                state1 = newState
            }
            state1.values.sum()
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
