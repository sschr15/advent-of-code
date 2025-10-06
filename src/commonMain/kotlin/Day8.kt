package sschr15.aocsolutions

import sschr15.aocsolutions.util.*
import kotlin.jvm.JvmStatic

/**
 * AOC 2024 [Day 8](https://adventofcode.com/2024/day/8)
 * Challenge: figure out where antennas don't work the same
 */
object Day8 : Challenge {
    override fun solve() = challenge(2024, 8) {
//        test()

        val signals: Map<Char, List<Point>>
        val grid: Grid<Char>
        part1 {
            grid = inputLines.toGrid()
            signals = grid.toPointMap()
                .filterValues { it != '.' }
                .entries
                .groupBy({ (_, c) -> c }) { (p) -> p }

            for (pts in signals.values) {
                pts.combinations(2).forEach { (p1, p2) ->
                    val diff = p2 - p1
                    val anti1 = p1 - diff
                    val anti2 = p2 + diff

                    if (anti1 in grid) grid[anti1] = '#'
                    if (anti2 in grid) grid[anti2] = '#'
                }
            }

            grid.sumOf { row -> row.count { it == '#' } }
        }
        part2 {
            for (pts in signals.values) {
                pts.combinations(2).forEach { (p1, p2) ->
                    val diff = p2 - p1
                    var anti1 = p1
                    var anti2 = p2

                    while (anti1 in grid) {
                        grid[anti1] = '#'
                        anti1 -= diff
                    }

                    while (anti2 in grid) {
                        grid[anti2] = '#'
                        anti2 += diff
                    }
                }
            }

            grid.sumOf { row -> row.count { it == '#' } }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
