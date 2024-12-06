package sschr15.aocsolutions

import sschr15.aocsolutions.util.*

/**
 * AOC 2024 [Day 1](https://adventofcode.com/2024/day/1)
 * Challenge: Make sure two historic location lists are "close enough"
 */
object Day6 : Challenge {
    override fun solve() = challenge(2024, 6) {
        fun checkForEscape(start: Point, grid: Grid<Char>, write: Boolean): Boolean {
            var pt = start
            var dir: Direction = Direction.North
            var i = 0

            while (pt in grid && i++ < grid.width * grid.height * 10) {
                if (dir.mod(pt) in grid && grid[dir.mod(pt)] == '#') {
                    dir = dir.turnRight()
                } else {
                    if (write) {
                        grid[pt] = 'X'
                    }

                    pt = dir.mod(pt)
                }
            }

            return pt !in grid
        }

        val grid: Grid<Char>
        val start: Point
        part1 {
            grid = inputLines.toGrid()
            start = grid.toPointMap().keys.single { grid[it] == '^' }

            require(checkForEscape(start, grid, true))

            grid.sumOf { it.count { c -> c == 'X' } }
        }
        part2 {
            val possibleCollisionPoints = grid.toPointMap().filterValues { it == 'X' }
            val freshGrid = { inputLines.toGrid() }

            var possibilities = mutableListOf<Point>()
            for ((point) in possibleCollisionPoints) {
                val newGrid = freshGrid()
                newGrid[point] = '#'
                if (!checkForEscape(start, newGrid, false)) {
                    possibilities.add(point)
                }
            }

            possibilities.size
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
