package sschr15.aocsolutions

import sschr15.aocsolutions.util.*

/**
 * AOC 2024 [Day 22](https://adventofcode.com/2024/day/22)
 * Challenge: 
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
            val buyers = inputLines.ints().map {
                val outputs = sequence {
                    var n = it
                    repeat(2000) {
                        n = n xor (n shl 6) and 0xffffff
                        n = n xor (n ushr 5) and 0xffffff
                        n = n xor (n shl 11) and 0xffffff
                        yield(n)
                    }
                }

                val prices = outputs.map { it % 10 }
                val diffs = prices.windowed(2) { (a, b) -> b - a }
                val windows = diffs.windowed(4)
                val output = mutableMapOf<List<Int>, Int>()
                prices.drop(4).zip(windows).forEach { (price, window) -> 
                    if (window !in output) output[window] = price
                }
                output
            }

            buyers.flatMap { it.entries }.groupBy({ it.key }) { it.value }.maxOf { it.value.sum() }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
