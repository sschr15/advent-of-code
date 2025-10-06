package sschr15.aocsolutions

import sschr15.aocsolutions.util.*
import kotlin.jvm.JvmStatic

/**
 * AOC 2024 [Day 11](https://adventofcode.com/2024/day/11)
 * Challenge: what if [lanternfish](https://adventofcode.com/2021/day/6) were actually stones
 * that change when you blink? (not to be confused with weeping angels)
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
            println(state.values.sum())

            // Bonus: a really big version by reason of taking the puzzle to its logical conclusion
            day11JvmBonus()
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}

internal expect fun AdvancedChallenge.day11JvmBonus()
