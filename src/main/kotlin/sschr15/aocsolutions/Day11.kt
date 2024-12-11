package sschr15.aocsolutions

import sschr15.aocsolutions.util.*
import sschr15.aocsolutions.util.watched.*
import java.math.BigInteger

/**
 * AOC 2024 [Day 11](https://adventofcode.com/2024/day/11)
 * Challenge: 
 */
object Day11 : Challenge {
    override fun solve() = challenge(2024, 11) {
//        test()

        splitBy(" ")

        fun iterate(existing: Map<Long, Long>): Map<Long, Long> {
            val newState = mutableMapOf<Long, Long>().default(0)
            for ((s, n) in existing.entries) {
                if (s == 0L) {
                    newState[1] += n
                } else if (log10iSmall(s) % 2 == 1) {
                    val half = (log10iSmall(s) + 1) / 2
                    val pow10 = powi(10, half)
                    newState[s / pow10] += n
                    newState[s % pow10] += n
                } else {
                    newState[s * 2024] += n
                }
            }

            return newState
        }

        var state: Map<Long, Long>
        part1 {
            state = inputLines.map { it.toLong() }.counts().mapValues { (_, v) -> v.toLong() }

            repeat(25) {
                state = iterate(state)
            }
            state.values.sum()
        }
        part2 {
            repeat(50) {
                state = iterate(state)
            }
            state.values.sum()

            try { assert(false) } catch (_: AssertionError) {
                var longRunningState = inputLines.map { it.toLong().w }.counts().mapValues { (_, v) -> v.toBigInteger() }
                val zero = 0.toBigInteger()

                repeat(100000) {
                    val newState = mutableMapOf<WatchedLong, BigInteger>().default(zero)
                    for ((s, n) in longRunningState.entries) {
                        if (s == 0L.w) {
                            newState[1L.w] += n
                        } else if (log10iSmall(s.value) % 2 == 1) {
                            val half = (log10iSmall(s.value) + 1) / 2
                            val pow10 = 10L.pow(half.toInt())
                            newState[s / pow10] += n
                            newState[s % pow10] += n
                        } else {
                            newState[s * 2024] += n
                        }
                    }
                    longRunningState = newState
                }

                return@part2 longRunningState.values.reduce { a, b -> a + b }.also {
                    println(it.toString().length)
                }
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
