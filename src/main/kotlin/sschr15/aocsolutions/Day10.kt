package sschr15.aocsolutions

import sschr15.aocsolutions.util.*

/**
 * AOC 2024 [Day 10](https://adventofcode.com/2024/day/10)
 * Challenge: let's go hiking! (not gambling)
 */
object Day10 : Challenge {
    override fun solve() = challenge(2024, 10) {
//        test()

        val grid: Grid<Int>
        val starts: Map<Point, Int>
        part1 {
            grid = inputLines.map { it.map { c -> c.digitToInt() } }.toGrid()
            starts = grid.toPointMap().filterValues { it == 0 }

            val currentLocations = ArrayDeque(starts.keys.map { it to it })
            val nines = mutableMapOf<Point, MutableSet<Point>>()

            while (currentLocations.isNotEmpty()) {
                val (pt, start) = currentLocations.removeFirst()
                val height = grid[pt]
                if (height == 9) {
                    nines.getOrPut(start) { mutableSetOf() }.add(pt)
                    continue
                }

                val adjacent = grid.getNeighbors(pt, includeDiagonals = false)
                currentLocations.addAll(adjacent.mapNotNull { (pt, h) -> if (h == height + 1) pt to start else null })
            }

            nines.values.sumOf { it.count() }
        }
        part2 {
            val currentPoints = ArrayDeque(starts.keys)
            var nines = 0

            while (currentPoints.isNotEmpty()) {
                val pt = currentPoints.removeFirst()
                val height = grid[pt]
                if (height == 9) {
                    nines++
                    continue
                }

                val adjacent = grid.getNeighbors(pt, includeDiagonals = false)
                currentPoints.addAll(adjacent.mapNotNull { (pt, h) -> if (h == height + 1) pt else null })
            }

            nines
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
