package sschr15.aocsolutions

import sschr15.aocsolutions.util.*

/**
 * AOC 2023 [Day 8](https://adventofcode.com/2023/day/8)
 * Challenge: Ride a camel through a desert, but do it the way ghosts do it
 */
object Day8 : Challenge {
    @ReflectivelyUsed
    override fun solve() = challenge(2023, 8) {
//        test()

        val nodes = mutableMapOf<String, Pair<String, String>>()
        val instructions: String
        part1 {
            inputLines.drop(2).forEach { 
                val (parent, _, left, right) = it.split(" ")
                nodes[parent] = Pair(left.drop(1).dropLast(1), right.dropLast(1))
            }

            instructions = inputLines.first()
            var node = nodes["AAA"]!!
            var i = 0
            while (true) {
                val result = when (instructions[i++ % instructions.length]) {
                    'L' -> node.first
                    'R' -> node.second
                    else -> error("Unknown character")
                }
                node = nodes[result] ?: error("Unknown node $result")
                if (result == "ZZZ") break
            }
            i
        }
        part2 {
            val starts = nodes.filterKeys { it.endsWith("A") }.keys.toList()
            val results = IntArray(starts.size)

            for (i in starts.indices) {
                var node = nodes[starts[i]]!!
                while (true) {
                    val insn = instructions[results[i]++ % instructions.length]
                    val result = when (insn) {
                        'L' -> node.first
                        'R' -> node.second
                        else -> error("Unknown character")
                    }
                    node = nodes[result] ?: error("Unknown node $result")
                    if (result.endsWith("Z")) break
                }
            }

            results.map { it.toLong() }.reduce { acc, i -> lcm(acc, i) }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
