package sschr15.aocsolutions

import sschr15.aocsolutions.util.Challenge
import sschr15.aocsolutions.util.ReflectivelyUsed
import sschr15.aocsolutions.util.component6
import sschr15.aocsolutions.util.getChallenge
import kotlin.time.measureTime

/**
 * AOC 2022 [Day 5](https://adventofcode.com/2022/day/5)
 * Challenge: move crates, towers of hanoi style
 */
object Day5 : Challenge {
    @ReflectivelyUsed
    override fun solve() = measureTime {
        val (rawState, rawMoves) = getChallenge(2022, 5, "\n\n")

        val stacks = Array(9) { mutableListOf<Char>() }
        for (line in rawState.lines().takeWhile { '[' in it }) {
            var i = 0
            var stack = 0
            while (i < line.length) {
                val c = line[i + 1]
                if (c != ' ') {
                    stacks[stack].add(0, c)
                }
                stack++
                i += 4
            }
        }

        val part2Stacks = stacks.map { it.toMutableList() } // copy the stacks for part 2

        for (insn in rawMoves.lines().filter { it.isNotBlank() }) {
            // move N from A to B
            val (_, n, _, a, _, b) = insn.split(" ")
            val from = a.toInt() - 1
            val to = b.toInt() - 1
            val count = n.toInt()
            repeat(count) {
                stacks[to].add(stacks[from].removeLast())
            }
        }

        val tops = stacks.map { it.lastOrNull() ?: ' ' }
        println(tops.joinToString(""))

        // ok now stacked boxes moved at the same time
        for (insn in rawMoves.lines().filter { it.isNotBlank() }) {
            // move N from A to B
            val (_, n, _, a, _, b) = insn.split(" ")
            val from = a.toInt() - 1
            val to = b.toInt() - 1
            val count = n.toInt()
            val moved = mutableListOf<Char>()
            repeat(count) {
                moved.add(part2Stacks[from].removeLast())
            }
            repeat(count) {
                part2Stacks[to].add(moved.removeLast())
            }
        }

        val part2Tops = part2Stacks.map { it.lastOrNull() ?: ' ' }
        println(part2Tops.joinToString(""))
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
