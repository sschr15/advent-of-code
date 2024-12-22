package sschr15.aocsolutions

import sschr15.aocsolutions.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * AOC 2024 [Day 22](https://adventofcode.com/2024/day/22)
 * Challenge: There's a *very* competitive hide-and-seek game going on between a bunch of monkeys, so much so
 * that the auction house is full of prospective hiders
 */
object Day22 : Challenge {
    override fun solve() = challenge(2024, 22) {
//        test()

        val nums: List<Int>
        part1 {
            inputLines.ints().map {
                var n = it
                repeat(2000) {
                    n = n xor (n shl 6) and 0xffffff
                    n = n xor (n ushr 5) and 0xffffff
                    n = n xor (n shl 11) and 0xffffff
                }
                n
            }.also { nums = it }.sumOf { it.toLong() }
        }
        part2 {
            val counts = ConcurrentHashMap<List<Int>, Int>()

            inputLines.ints().forEachParallel { it ->
                val outputs = (0..2000).runningFold(it) { acc, _ ->
                    var n = acc
                    n = n xor (n shl 6) and 0xffffff
                    n = n xor (n ushr 5) and 0xffffff
                    n = n xor (n shl 11) and 0xffffff
                    n
                }

                val prices = outputs.map { it % 10 }
                val diffs = prices.windowed(2) { (a, b) -> b - a }
                val windows = diffs.windowed(4)
                val seen = mutableSetOf<List<Int>>()
                for ((price, window) in prices.drop(4).zip(windows)) {
                    if (seen.add(window)) {
                        counts.merge(window, price, Int::plus)
                    }
                }
            }

            counts.maxOf { it.value }.also { check(it == 2423) }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
