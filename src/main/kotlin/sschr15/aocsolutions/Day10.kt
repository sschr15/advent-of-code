package sschr15.aocsolutions

import sschr15.aocsolutions.util.*

/**
 * AOC 2024 [Day 10](https://adventofcode.com/2024/day/10)
 * Challenge: 
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
            val currentPaths = ArrayDeque(starts.keys.map { listOf(it) to it })
            val trails = mutableMapOf<Pair<Point, Point>, MutableSet<List<Point>>>()

            while (currentPaths.isNotEmpty()) {
                val (path, start) = currentPaths.removeFirst()
                val pt = path.last()
                val height = grid[pt]
                if (height == 9) {
                    trails.getOrPut(start to pt) { mutableSetOf() }.add(path)
                    continue
                }

                val adjacent = grid.getNeighbors(pt, includeDiagonals = false)
                currentPaths.addAll(adjacent.mapNotNull { (pt, h) -> if (h != height + 1) return@mapNotNull null else path + pt to start })
            }

            trails.values.sumOf { it.count() }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
