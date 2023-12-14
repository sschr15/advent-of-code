package sschr15.aocsolutions

import sschr15.aocsolutions.util.*
import sschr15.aocsolutions.util.watched.sumOf

/**
 * AOC 2023 [Day 14](https://adventofcode.com/2023/day/14)
 * Challenge: TODO (based on the day's description)
 */
object Day14 : Challenge {
    @ReflectivelyUsed
    override fun solve() = challenge(2023, 14) {
//        test()

        fun move(direction: Direction, grid: Grid<Char>): Grid<Char> {
            val newGrid = grid.toGrid() // new grid
            when (direction) {
                Direction.North -> {
                    for (y in 1..<newGrid.height) for (x in 0..<newGrid.width) {
                        var point = Point(x, y)
                        val char = newGrid[point]
                        if (char == 'O') {
                            while (newGrid[point.up()] == '.') {
                                newGrid[point] = '.'
                                point = point.up()
                                if (point.y == 0) break
                            }
                            newGrid[point] = 'O'
                        }
                    }
                }
                Direction.South -> {
                    for (y in newGrid.height - 2 downTo 0) for (x in 0..<newGrid.width) {
                        var point = Point(x, y)
                        val char = newGrid[point]
                        if (char == 'O') {
                            while (newGrid[point.down()] == '.') {
                                newGrid[point] = '.'
                                point = point.down()
                                if (point.y == newGrid.height - 1) break
                            }
                            newGrid[point] = 'O'
                        }
                    }
                }
                Direction.East -> {
                    for (y in 0..<newGrid.height) for (x in newGrid.width - 2 downTo 0) {
                        var point = Point(x, y)
                        val char = newGrid[point]
                        if (char == 'O') {
                            while (newGrid[point.right()] == '.') {
                                newGrid[point] = '.'
                                point = point.right()
                                if (point.x == newGrid.width - 1) break
                            }
                            newGrid[point] = 'O'
                        }
                    }
                }
                Direction.West -> {
                    for (y in 0..<newGrid.height) for (x in 1..<newGrid.width) {
                        var point = Point(x, y)
                        val char = newGrid[point]
                        if (char == 'O') {
                            while (newGrid[point.left()] == '.') {
                                newGrid[point] = '.'
                                point = point.left()
                                if (point.x == 0) break
                            }
                            newGrid[point] = 'O'
                        }
                    }
                }
            }
            return newGrid
        }

        fun Grid<Char>.getPoints() = toPointMap().filterValues { it == 'O' }.keys

        part1 {
            val input = inputLines.chars().toGrid()
            val grid = move(Direction.North, input)

            grid.rows().mapIndexed { i, row -> grid.height - i to row.count { it == 'O' } }.sumOf { it.first.w * it.second }
        }
        part2 {
            var grid = inputLines.chars().toGrid()

            val gridSet = mutableSetOf<Set<Point>>()
            val previousGrids = mutableListOf(grid)
            var previousSet = grid.getPoints()
            while (gridSet.add(previousSet)) {
                grid = move(Direction.North, grid)
                grid = move(Direction.West, grid)
                grid = move(Direction.South, grid)
                grid = move(Direction.East, grid)
                previousGrids.add(grid)
                previousSet = grid.getPoints()
            }

            val lookup = previousGrids.dropWhile { it.getPoints() != previousSet }

            val billionth = lookup[(1_000_000_000 - gridSet.size) % (lookup.size - 1)]
            billionth.rows().mapIndexed { i, row -> billionth.height - i to row.count { it == 'O' } }.sumOf { it.first.w * it.second }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
