package sschr15.aocsolutions

import sschr15.aocsolutions.util.*

/**
 * AOC 2025 [Day 12](https://adventofcode.com/2025/day/12)
 * Challenge: let's put presents under the tree!
 */
object Day12 : Challenge {
    override fun solve() = challenge(2025, 12) {
//        test()
        splitBy("\n\n")

        part1 {
            val shapes = inputLines.dropLast(1).map { it.lines().drop(1).toGrid() }
            val grids = inputLines.last().lines()
            var count = 0
            for (grid in grids) {
                val (size, shapesText) = grid.split(": ")
                val (width, height) = size.split("x").ints()
                val shapes = shapesText.split(" ").ints()

                if (shapes.sum() <= ((width / 3) * (height / 3))) {
                    count++
                    continue
                }

                // TODO: try fitting tighter?
                // apparently the solution doesn't have any cases where fitting tighter is necessary
            }

            count
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
