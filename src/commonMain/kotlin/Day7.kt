package sschr15.aocsolutions

import sschr15.aocsolutions.util.*
import kotlin.jvm.JvmStatic
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

//        val mutex = Mutex()
        val result = object {
            var count = 0L
            val succeeded = mutableListOf<String>()
        }

        part1 {
            inputLines.mapParallel { 
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
                        return@mapParallel it
                    }
                }

                null
            }.forEach { 
                if (it != null) {
                    result.succeeded.add(it)
                    result.count += it.substringBefore(':').toLong()
                }
            }

            result.count
        }

        part2 {
            inputLines.filter { it !in result.succeeded }.mapParallel { 
                val (answer, values) = it.split(": ")
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
                            2 -> {
                                var a = acc
                                var b = valueInts[i]
                                while (b > 0) {
                                    a *= 10
                                    b /= 10
                                }
                                a + valueInts[i]
                            }
                            else -> error("Impossible")
                        }

                        current = remaining % 3
                        remaining /= 3
                    }

                    return acc == answerInt
                }

                var i = 0
                while (log2(i.toDouble()) < valueInts.size) {
                    if (testSolution(i++)) {
                        return@mapParallel answerInt
                    }
                }

                0L
            }.sum()
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
