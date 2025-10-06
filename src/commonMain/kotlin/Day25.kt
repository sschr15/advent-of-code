package sschr15.aocsolutions

import sschr15.aocsolutions.util.*
import kotlin.jvm.JvmStatic

/**
 * AOC 2024 [Day 25](https://adventofcode.com/2024/day/25)
 * Challenge: Figure out which keys and locks might be for the Chief Historian's office
 */
object Day25 : Challenge {
    override fun solve() = challenge(2024, 25) {
//        test()

        data class Item(val a: Int, val b: Int, val c: Int, val d: Int, val e: Int) {
            fun lockFitsKey(key: Item): Boolean =
                a + key.a < 6 && b + key.b < 6 && c + key.c < 6 && d + key.d < 6 && e + key.e < 6
        }

        splitBy("\n\n")

        part1 {
            val locks = mutableListOf<Item>()
            val keys = mutableListOf<Item>()

            for (possibility in inputLines) {
                // Initial solution
                val grid = possibility.lines().toGrid()
                val list = IntArray(5)
                val isLock = grid.first().all { it == '#' }
                val toParse = if (!isLock) {
                    grid.reversed()
                } else grid

                val transposed = toParse.transpose()

                for (i in list.indices) {
                    list[i] = transposed[i].count { it == '#' } - 1
                }

                val item = Item(list[0], list[1], list[2], list[3], list[4])

                if (isLock) locks.add(item)
                else keys.add(item)

                // Modified solution designed primarily for speed
//                val isLock = possibility.startsWith("#####")
//                var a = 0
//                var b = 0
//                var c = 0
//                var d = 0
//                var e = 0
//
//                for (line in possibility.lines()) {
//                    if (line == "#####") continue
//                    if (line[0] == '#') a++
//                    if (line[1] == '#') b++
//                    if (line[2] == '#') c++
//                    if (line[3] == '#') d++
//                    if (line[4] == '#') e++
//                }
//
//                val item = Item(a, b, c, d, e)
//                if (isLock) locks.add(item)
//                else keys.add(item)
            }

            var sum = 0

            for (lock in locks) for (key in keys) {
                if (lock.lockFitsKey(key)) sum++
            }

            sum
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
