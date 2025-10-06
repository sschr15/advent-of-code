package sschr15.aocsolutions

import sschr15.aocsolutions.util.*
import kotlin.jvm.JvmStatic

/**
 * AOC 2024 [Day 16](https://adventofcode.com/2024/day/16)
 * Challenge: find a good seat for the Second Non-Annual Reindeer Olympics
 */
object Day16 : Challenge {
    override fun solve() = challenge(2024, 16) {
//        test()

        val grid: Grid<Char>
        val start: Point
        val end: Point
        val costsToEachPoint: Map<Triple<Point, Direction, Direction>, Int>
        val minCost: Int
        part1 {
            grid = inputLines.toGrid()
            start = grid.toPointMap().filterValues { it == 'S' }.keys.first()
            end = grid.toPointMap().filterValues { it == 'E' }.keys.first()
            // dijkstra calculates the cost to each point from the start
            costsToEachPoint = dijkstra<Triple<Point, Direction, Direction>>(
                Triple(start, Direction.East, Direction.East),
                { (pt, dir) ->
                    listOf(dir, dir.turnLeft(), dir.turnRight()).mapNotNull {
                        if (grid[it.mod(pt)] != '#') Triple(it.mod(pt), it, dir) else null
                    }
                },
                { (_, dir, prev) -> if (dir == prev) 1 else 1001 }
            )
            minCost = costsToEachPoint.filterKeys { it.first == end }.values.min()
            minCost
        }
        part2 {
            val allShortestPaths = mutableSetOf<List<Point>>()
            val queue = PriorityQueue<Triple<Int, List<Point>, Direction>>(compareBy { it.first })
            queue.add(Triple(0, listOf(start), Direction.East))
            while (queue.isNotEmpty()) {
                val (cost, path, prev) = queue.poll()
                val last = path.last()
                if (last == end) {
                    if (cost == minCost) allShortestPaths.add(path)
                    continue
                }
                for (dir in listOf(prev, prev.turnLeft(), prev.turnRight())) {
                    val next = dir.mod(last)
                    if (grid[next] == '#') continue
                    val nextCost = cost + if (dir == prev) 1 else 1001
                    if (nextCost > costsToEachPoint[Triple(next, dir, prev)]!!) continue
                    queue.add(Triple(nextCost, path + next, dir))
                }
            }
            allShortestPaths.flatten().toSet().size
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
