@file:OptIn(ExperimentalStdlibApi::class)

package sschr15.aocsolutions

import sschr15.aocsolutions.util.*

/**
 * AOC 2022 [Day 8](https://adventofcode.com/2022/day/8)
 * Challenge: finding trees in a forest
 */
object Day8 : Challenge {
    @ReflectivelyUsed
    override fun solve() = challenge(2022, 8) {
//        test()
        part1 {
            // IT'S GRID DAY
            val grid = inputLines.chars().map { it.map(Char::digitToInt) }.toGrid()
            val knownVisible = mutableSetOf<Point>()
            // find how many points are the largest from the edges
            for ((point, value) in grid.toPointMap()) {
                // top
                var working = point
                while (working.y > 0) {
                    working = working.up()
                    if (grid[working] >= value) break
                }
                if (working.y == 0 && grid[working] < value) {
                    knownVisible += point
                    continue
                }
                // bottom
                working = point
                while (working.y < grid.height - 1) {
                    working = working.down()
                    if (grid[working] >= value) break
                }
                if (working.y == grid.height - 1 && grid[working] < value) {
                    knownVisible += point
                    continue
                }
                // left
                working = point
                while (working.x > 0) {
                    working = working.left()
                    if (grid[working] >= value) break
                }
                if (working.x == 0 && grid[working] < value) {
                    knownVisible += point
                    continue
                }
                // right
                working = point
                while (working.x < grid.width - 1) {
                    working = working.right()
                    if (grid[working] >= value) break
                }
                if (working.x == grid.width - 1 && grid[working] < value) {
                    knownVisible += point
                    continue
                }
            }

            // ensure edges are visible
            for (x in 0 ..< grid.width) {
                knownVisible += Point(x, 0)
                knownVisible += Point(x, grid.height - 1)
            }
            for (y in 0 ..< grid.height) {
                knownVisible += Point(0, y)
                knownVisible += Point(grid.width - 1, y)
            }

            submit(knownVisible.size)

            addInfo("grid", grid)
        }
        part2 {
            // find the highest "scenic score" (the point with the highest number of visible points)
            val grid = getInfo<Grid<Int>>("grid")!!
            val amounts = grid.toPointMap().map { (point, value) ->
                var working = point
                var hitTree = false
                var above = 1
                while (working.y > 0) {
                    working = working.up()
                    if (grid[working] >= value) {
                        hitTree = true
                        break
                    }
                    above++
                }
                if (working.y == point.y) above = 0 // if we didn't move, we didn't see anything
                if (!hitTree) above-- // we went to the edge
                working = point
                hitTree = false
                var below = 1
                while (working.y < grid.height - 1) {
                    working = working.down()
                    if (grid[working] >= value) {
                        hitTree = true
                        break
                    }
                    below++
                }
                if (working.y == point.y) below = 0
                if (!hitTree) below--
                working = point
                hitTree = false
                var left = 1
                while (working.x > 0) {
                    working = working.left()
                    if (grid[working] >= value) {
                        hitTree = true
                        break
                    }
                    left++
                }
                if (working.x == point.x) left = 0
                if (!hitTree) left--
                working = point
                hitTree = false
                var right = 1
                while (working.x < grid.width - 1) {
                    working = working.right()
                    if (grid[working] >= value) {
                        hitTree = true
                        break
                    }
                    right++
                }
                if (working.x == point.x) right = 0
                if (!hitTree) right--

                above * below * left * right
            }
            submit(amounts.max())
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
