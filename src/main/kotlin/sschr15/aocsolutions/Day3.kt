package sschr15.aocsolutions

import sschr15.aocsolutions.util.*
import sschr15.aocsolutions.util.watched.*

/**
 * AOC 2024 [Day 3](https://adventofcode.com/2024/day/3)
 * Challenge: Do our best to recover some data from a corrupted memory
 */
object Day3 : Challenge {
    override fun solve() = challenge(2024, 3) {
//        test()
        splitBy(null)
        val regex = Regex("""mul\((\d{1,3}),(\d{1,3})\)""")
        part1 {
            val input = inputLines.single()
            regex.findAll(input).toList().sumOf { 
                val (a, b) = it.groupValues.drop(1).ints()
                a * b
            }
        }
        part2 {
            val input = inputLines.single()
            val sanitized = buildString {
                var allow = true
                var pos = 0
                while (pos < input.length) {
                    if (input.substring(pos).startsWith("do()")) {
                        allow = true
                        append(' ')
                        pos += 4
                    } else if (input.substring(pos).startsWith("don't()")) {
                        allow = false
                        append(' ')
                        pos += 7
                    } else {
                        if (allow) append(input[pos])
                        pos++
                    }
                }
            }

            regex.findAll(sanitized).toList().sumOf {
                val (a, b) = it.groupValues.drop(1).ints()
                a * b
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
