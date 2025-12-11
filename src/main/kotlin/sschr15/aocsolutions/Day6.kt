package sschr15.aocsolutions

import sschr15.aocsolutions.util.*

/**
 * AOC 2025 [Day 6](https://adventofcode.com/2025/day/6)
 * Challenge: 
 */
object Day6 : Challenge {
    override fun solve() = challenge(2025, 6) {
//        test()

        part1 {
            inputLines.map { it.trim().split(Regex(" +")) }.transpose().sumOf {
                val numbers = it.dropLast(1).longs()
                val operation = it.last()

                when (operation) {
                    "+" -> numbers.sum()
                    "*" -> numbers.mul()
                    else -> error("Unknown operation $operation")
                }
            }
        }
        part2 {
            val newLines = List(inputLines.size) { mutableListOf<String>() }
            val constructors = List(inputLines.size) { StringBuilder() }
            var i = 0
            while (i < inputLines.first().length) {
                val chars = inputLines.map { if (i in it.indices) it[i] else ' ' }
                if (chars.all { it == ' ' }) {
                    for (j in newLines.indices) {
                        newLines[j].add(constructors[j].toString())
                        constructors[j].clear()
                    }
                } else {
                    for (j in newLines.indices) constructors[j].append(chars[j])
                }
                i++
            }

            for (j in newLines.indices) newLines[j].add(constructors[j].toString())

            newLines.transpose().sumOf { strings ->
                val numbersNow = strings.dropLast(1)
                val operation = strings.last()

                val fixedNumbers = numbersNow.chars().transpose()
                    .map { c -> c.joinToString("") }
                    .filter { it.isNotBlank() }
                    .map { it.trim() }
                    .longs()

//                println(fixedNumbers to operation)

                when (operation.trim()) {
                    "+" -> fixedNumbers.sum()
                    "*" -> fixedNumbers.mul()
                    else -> error("Unknown operation $operation")
                }
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
