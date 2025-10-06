package sschr15.aocsolutions

import sschr15.aocsolutions.util.*
import kotlin.jvm.JvmStatic
import kotlin.math.absoluteValue

/**
 * AOC 2024 [Day 20](https://adventofcode.com/2024/day/20)
 * Challenge: Make sure two historic location lists are "close enough"
 */
object Day20 : Challenge {
    override fun solve() = challenge(2024, 20) {
//        test()

        val grid: Grid<Char>
        val pointMap: Map<Point, Char>
        val start: Point
        val end: Point
        val travelGrid: Grid<Int>

        part1 {
            grid = inputLines.toGrid()
            pointMap = grid.toPointMap()
            start = pointMap.filterValues { it == 'S' }.keys.single()
            end = pointMap.filterValues { it == 'E' }.keys.single()

            val newGrid = Grid(grid.width, grid.height, -1)

            var prev = Point(-1, -1)
            var current = start
            var travelled = 0
            while (current != end) {
                val next = current.neighborsIn(grid).single { it != prev && grid[it] != '#' }
                prev = current
                newGrid[current] = travelled++
                current = next
            }

            newGrid[current] = travelled
            travelGrid = newGrid.toGrid() // make copy for p2

            val skips = mutableMapOf<Point, Int>()

            for ((pt, i) in newGrid.toPointMap()) {
                if (i != -1) continue
                val neighborsInPath = newGrid.getNeighbors(pt).filterValues { it != -1 }
                val a = neighborsInPath.keys.firstOrNull { it.right().right() in neighborsInPath || it.down().down() in neighborsInPath } ?: continue
                val b = if (a.right().right() in neighborsInPath) a.right().right() else a.down().down()

                val diff = newGrid[a] - newGrid[b]
                skips[pt] = diff.absoluteValue - 2
            }

            skips.values.count { it >= 100 }
        }
        part2 {
            val allTraversalPoints = pointMap.filterValues { it != '#' }.keys

            allTraversalPoints.toList().pairSequence().count {
                val (s, e) = it
                val psCost = s.manhattanDistance(e)
                if (psCost > 20) return@count false

                val diff = travelGrid[s] - travelGrid[e]
                val saving = diff.absoluteValue - psCost
                saving >= 100
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
