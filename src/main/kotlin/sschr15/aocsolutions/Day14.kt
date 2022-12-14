@file:OptIn(ExperimentalStdlibApi::class)

package sschr15.aocsolutions

import sschr15.aocsolutions.util.*

enum class Value {
    EMPTY,
    SAND,
    ROCK,
    BOTTOM,
}

/**
 * AOC 2022 [Day 14](https://adventofcode.com/2022/day/14)
 * Challenge: TODO (based on the day's description)
 */
object Day14 : Challenge {
    @ReflectivelyUsed
    override fun solve() = challenge(2022, 14) {
        fun calcGrain(grid: Grid<Value>): Boolean {
            var sandGrain = Point(500, 0)
            inner@ while (true) {
                val below = sandGrain.down()
                if (grid[below] == Value.EMPTY) {
                    sandGrain = below
                    continue@inner
                }
                val left = below.left()
                if (grid[left] == Value.EMPTY) {
                    sandGrain = left
                    continue@inner
                }
                val right = below.right()
                if (grid[right] == Value.EMPTY) {
                    sandGrain = right
                    continue@inner
                }
                // can't move, sand sits at its current location (unless it's at the bottom)
                if (grid[below] == Value.BOTTOM) return false
                grid[sandGrain] = Value.SAND
                return true
            }
        }

//        test()
        part1 {
            // sandbox, quite literally
            val walls = inputLines.map { it.split(" -> ").map { s -> s.split(",").let { l ->  Point(l[0].toInt(), l[1].toInt()) } } }
            val grid = Grid(1000, walls.flatten().maxOf { it.y } + 2, Value.EMPTY)
            walls.forEach { l ->
                l.windowed(2, partialWindows = false).forEach {
                    val (start, end) = it
                    val minX = minOf(start.x, end.x)
                    val maxX = maxOf(start.x, end.x)
                    val minY = minOf(start.y, end.y)
                    val maxY = maxOf(start.y, end.y)
                    for (x in minX .. maxX) {
                        for (y in minY .. maxY) {
                            grid[x, y] = Value.ROCK
                        }
                    }
                }
            }
            for (x in 0 ..< 1000) {
                grid[x, grid.height - 1] = Value.BOTTOM
            }
            var sandGrains = 0
            outer@ while (true) {
                val grain = calcGrain(grid)
                if (grain) {
                    sandGrains++
                } else {
                    break@outer
                }
            }
            submit(sandGrains)
            addInfo("grid", grid)
        }
        part2 {
            val grid = getInfo<Grid<Value>>("grid")!!
            val newGrid = Grid(grid.width, grid.height + 1, Value.EMPTY)
            for ((point, value) in grid.toPointMap()) {
                newGrid[point] = if (value != Value.BOTTOM) value else Value.EMPTY
            }
            for (x in 0 ..< 1000) {
                newGrid[x, newGrid.height - 1] = Value.ROCK // no more infinite void
            }

            var sandGrains = newGrid.flatten().count { it == Value.SAND }
            val spawnPoint = Point(500, 0)
            while (newGrid[spawnPoint] != Value.SAND) {
                calcGrain(newGrid)
                sandGrains++
                // once the next grain can't fall, we're done
            }
            submit(sandGrains)
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
