package sschr15.aocsolutions

import a
import sschr15.aocsolutions.util.*
import kotlin.math.absoluteValue

/**
 * AOC 2023 [Day 11](https://adventofcode.com/2023/day/11)
 * Challenge: whoops our spacetime is destroying itself right in front of our eyes
 */
object Day11 : Challenge {
    @ReflectivelyUsed
    override fun solve() = challenge(2023, 11) {
//        test()

        val initialGrid: Grid<Char>
        val emptyColumns = mutableListOf<Int>()
        val emptyRows = mutableListOf<Int>()
        part1 {
            initialGrid = inputLines.chars().toGrid()

            for (column in 0..<initialGrid.width) {
                if (initialGrid.getColumn(column).all { it == '.' }) {
                    emptyColumns += column
                }
            }

            for (row in 0..<initialGrid.height) {
                if (initialGrid.getRow(row).all { it == '.' }) {
                    emptyRows += row
                }
            }

            val grid = Grid(initialGrid.width + emptyColumns.size, initialGrid.height + emptyRows.size, '.')

            var newX = 0
            var newY = 0

            for (y in 0..<initialGrid.height) {
                if (y in emptyRows) {
                    newY += 2
                    continue
                }
                for (x in 0..<initialGrid.width) {
                    if (x in emptyColumns) {
                        newX += 2
                        continue
                    }
                    grid[newX, newY] = initialGrid[x, y]
                    newX++
                }
                newX = 0
                newY++
            }

            val galaxies = grid.toPointMap().filterValues { it == '#' }.keys.toList().combinations(2)

            galaxies.sumOf { (a, b) -> a.manhattanDistance(b) }
        }
        part2 {
            val expansion = 999_999
            val galaxies = initialGrid.toPointMap().filterValues { it == '#' }.keys.toList()
                .map { (x, y) ->
                    val emptyRowCount = emptyRows.filter { it < y }.size
                    val emptyColumnCount = emptyColumns.filter { it < x }.size
                    val newX = x.toLong() + emptyColumnCount * expansion
                    val newY = y.toLong() + emptyRowCount * expansion
                    newX to newY // no points, too large
                }
                .combinations(2)

            galaxies.sumOf { (a, b) -> (a.first - b.first).absoluteValue + (a.second - b.second).absoluteValue }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
