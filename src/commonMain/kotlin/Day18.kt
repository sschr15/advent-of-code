package sschr15.aocsolutions

import sschr15.aocsolutions.util.*
import kotlin.jvm.JvmStatic

/**
 * AOC 2024 [Day 18](https://adventofcode.com/2024/day/18)
 * Challenge: 
 */
object Day18 : Challenge {
    override fun solve() = challenge(2024, 18) {
//        test()

        fun findPath(grid: Grid<Int>): Int? = dijkstra(
            Point.origin,
            { grid.getNeighbors(it).filterValues { it == 0 }.keys.toList() },
            { 1 }
        )[Point(70, 70)]
        val grid: Grid<Int>
        val corruptions: List<Point>
        part1 {
            corruptions = inputLines.map { it.split(",").ints() }.map { (a, b) -> Point(a, b) }
            grid = Grid(71, 71, 0)
            corruptions.subList(0, 1024).forEach {
                grid[it] = Int.MAX_VALUE
            }

            findPath(grid)
        }
        part2 {
            var count = 1024
            while (findPath(grid) != null) {
                grid[corruptions[count++]] = Int.MAX_VALUE
            }

            val (x, y) = corruptions[count - 1]
            "$x,$y"
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
