package sschr15.aocsolutions

import sschr15.aocsolutions.util.*

/**
 * AOC 2024 [Day 6](https://adventofcode.com/2024/day/6)
 * Challenge: cause a temporary time loop in the past!
 */
object Day6 : Challenge {
    override fun solve() = challenge(2024, 6) {
        fun checkForEscape(start: Point, grid: Grid<Char>, write: Boolean): Boolean {
            var pt = start.toMutablePoint()
            var dir: Direction = Direction.North
            var i = 0

            var ptInGrid = pt in grid
            while (ptInGrid && i++ < 10_000) {
                pt.move(dir)
                ptInGrid = pt in grid
                if (ptInGrid && grid[pt] == '#') {
                    pt.moveBack(dir)
                    dir = dir.turnRight()
                } else {
                    if (write && ptInGrid) {
                        grid[pt] = 'X'
                    }
                }
            }

            return !ptInGrid
        }

        val grid: Grid<Char>
        val start: Point
        part1 {
            grid = inputLines.toGrid()
            start = grid.toPointMap().keys.single { grid[it] == '^' }

            require(checkForEscape(start, grid, true))

            grid.sumOf { it.count { c -> c == 'X' } } + 1 // add 1 for the start point
        }
        part2 {
            val possibleCollisionPoints = grid.toPointMap().filterValues { it == 'X' }

//            val newGrid = inputLines.toGrid()
//
//            var possibilities = mutableListOf<Point>()
//            for ((point) in possibleCollisionPoints) {
//                newGrid[point] = '#'
//                if (!checkForEscape(start, newGrid, false)) {
//                    possibilities.add(point)
//                }
//                newGrid[point] = '.'
//            }
//
//            possibilities.size

            val result = object {
                var count = 0
            }

            val divvied = possibleCollisionPoints.keys.chunked(possibleCollisionPoints.size / 16)
            val threads = divvied.map { chunk ->
                Thread {
                    val newGrid = grid.toGrid() // copy
                    var count = 0
                    for (point in chunk) {
                        newGrid[point] = '#'
                        if (!checkForEscape(start, newGrid, false)) count++
                        newGrid[point] = '.'
                    }
                    synchronized(result) {
                        result.count += count
                    }
                }
            }

            threads.forEach { it.start() }
            threads.forEach { it.join() }

            result.count
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
