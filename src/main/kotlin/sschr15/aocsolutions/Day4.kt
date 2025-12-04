package sschr15.aocsolutions

import sschr15.aocsolutions.util.*

/**
 * AOC 2025 [Day 4](https://adventofcode.com/2025/day/1)
 * Challenge: that's a lot of paper, how'd you get it in rolls?
 */
object Day4 : Challenge {
    override fun solve() = challenge(2025, 4) {
//        test()

        val grid: Grid<Char>
        part1 {
            grid = inputLines.toGrid()
            grid.toPointMap().count { (pt, c) ->
                if (c != '@') return@count false
                grid.getNeighbors(pt, includeDiagonals = true).count { (_, c) -> c == '@' } < 4
            }
        }
        part2 {
            var grid = grid
            var removed = 0
            val papers = grid.toPointMap().filter { (_, c) -> c == '@' }.keys.toMutableSet()
            while (true) {
                var modified = false
                val newGrid = grid.toGrid() // copy
                for (point in papers.toSet()) {
                    val canRemove = grid.getNeighbors(point, includeDiagonals = true).count { (_, c) -> c == '@' } < 4
                    if (canRemove) {
                        newGrid[point] = '.'
                        removed++
                        papers.remove(point)
                        modified = true
                    }
                }
                if (!modified) break
                grid = newGrid
            }

            removed
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
