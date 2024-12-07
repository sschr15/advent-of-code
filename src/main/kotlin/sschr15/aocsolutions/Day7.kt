package sschr15.aocsolutions

import sschr15.aocsolutions.util.*
import kotlin.concurrent.thread
import kotlin.math.log
import kotlin.math.log2

/**
 * AOC 2024 [Day 7](https://adventofcode.com/2024/day/7)
 * Challenge: Figure out what kinds of operations some elephants stole
 */
object Day7 : Challenge {
    override fun solve() = challenge(2024, 7) {
//        test()

        // The easy way out: just brute forcing both parts
        // (but to speed things up, we'll use multiple threads)

        val result = object {
            var count = 0L
            val succeeded = mutableListOf<String>()
        }

        part1 {
            val threads = inputLines.divided(System.getProperty("aoc.threads")?.toInt() ?: 16).map { threadLines ->
                thread {
                    val succeeded = threadLines.filter {
                        val (answer, values) = it.split(": ")
                        val valueInts = values.split(" ").map(String::toLong)
                        val answerInt = answer.toLong()

                        fun testSolution(bits: Int): Boolean {
                            return valueInts.reduceIndexed { i, acc, int -> 
                                if (bits and (1 shl i) != 0) acc * int else acc + int
                            } == answerInt
                        }

                        var i = 0
                        while (log2(i.toDouble()) < valueInts.size) {
                            if (testSolution(i++)) {
                                return@filter true
                            }
                        }

                        false
                    }

                    val sum = succeeded.sumOf {
                        it.substringBefore(':').toLong()
                    }

                    synchronized(result) {
                        result.count += sum
                        result.succeeded.addAll(succeeded)
                    }
                }
            }

            threads.forEach(Thread::join)

            result.count
        }

        part2 {
            val threads = inputLines
                .filter { it !in result.succeeded }
                .divided(System.getProperty("aoc.threads")?.toInt() ?: 16)
                .map { threadLines ->
                    thread {
                        val sum = threadLines.sumOf { s ->
                            val (answer, values) = s.split(": ")
                            val valueInts = values.split(" ").map(String::toLong)
                            val answerInt = answer.toLong()

                            fun testSolution(bits: Int): Boolean {
                                var remaining = bits / 3
                                var current = bits % 3

                                var acc = valueInts[0]

                                for (i in 1..<valueInts.size) {
                                    acc = when (current) {
                                        0 -> acc * valueInts[i]
                                        1 -> acc + valueInts[i]
                                        2 -> "$acc${valueInts[i]}".toLong()
                                        else -> error("Unexpected non-ternary digit")
                                    }

                                    current = remaining % 3
                                    remaining /= 3
                                }

                                return acc == answerInt
                            }

                            var i = 0
                            while (log(i.toDouble(), 3.0) < valueInts.size) {
                                if (testSolution(i++)) {
                                    return@sumOf answerInt
                                }
                            }

                            0L
                        }

                        synchronized(result) {
                            result.count += sum
                        }
                    }
                }

            threads.forEach(Thread::join)

            result.count
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
