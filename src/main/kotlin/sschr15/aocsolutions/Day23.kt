package sschr15.aocsolutions

import sschr15.aocsolutions.util.*
import java.util.ArrayDeque
import kotlin.time.measureTime

/**
 * AOC 2023 [Day 23](https://adventofcode.com/2023/day/23)
 * Challenge: How does one take a leisurely walk through a forest, pretending there *isn't*
 * a global snow shortage mere days before christmas?
 */
object Day23 : Challenge {
    @ReflectivelyUsed
    override fun solve() = challenge(2023, 23) {
//        test()

        val grid: Grid<Char>
        val start: Point
        val end: Point

        part1 {
            fun findSlowestPath(start: Point, end: Point, grid: Grid<Char>, previous: Point = start.up()): Int {
                var previous = previous
                var current = start
                var score = 0
                while (true) {
                    when (grid[current]) {
                        '^' -> {
                            previous = current
                            current = current.up()
                            score++
                            continue
                        }
                        'v' -> {
                            previous = current
                            current = current.down()
                            score++
                            continue
                        }
                        '<' -> {
                            previous = current
                            current = current.left()
                            score++
                            continue
                        }
                        '>' -> {
                            previous = current
                            current = current.right()
                            score++
                            continue
                        }
                    }

                    val capableDirections = listOfNotNull(
                        current.up().takeIf { it in grid && grid[it] !in arrayOf('#', 'v') },
                        current.down().takeIf { it in grid && grid[it] !in arrayOf('#', '^') },
                        current.left().takeIf { it in grid && grid[it] !in arrayOf('#', '>') },
                        current.right().takeIf { it in grid && grid[it] !in arrayOf('#', '<') }
                    ).filter { it != previous }

                    if (capableDirections.isEmpty()) {
                        return score // only one spot on the grid has no valid moves, and that's the end
                    }

                    val only = capableDirections.singleOrNull()
                        ?: return capableDirections.maxOf { findSlowestPath(it, end, grid, previous) } + score + 1

                    previous = current
                    current = only
                    score++
                }
            }
            grid = inputLines.toGrid()

            start = Point((0..<grid.width).single { grid[it, 0] != '#' }, 0)
            end = Point((0..<grid.width).single { grid[it, grid.height - 1] != '#' }, grid.height - 1)

            findSlowestPath(start, end, grid)
        }
        part2 {
            val graph = Graph<Point>()

            val startNode = graph.addNode(start)
            val endNode = graph.addNode(end)

            val junctions = grid.toPointMap()
                .filterValues { it != '#' }
                .filterKeys { grid.getNeighbors(it, false).count { (_, c) -> c != '#' } > 2 }
                .mapValues { (pt) -> graph.addNode(pt) }

            measureTime {

                data class QueueEntry(
                    val point: Point,
                    val count: Int,
                    val from: Graph<Point>.Node,
                    val previous: Point
                )

                val queue = ArrayDeque<QueueEntry>()
                queue.addAll(junctions.flatMap { (pt, node) ->
                    grid.getNeighbors(pt, false)
                        .filterValues { it != '#' }
                        .keys
                        .map(AbstractPoint::toPoint)
                        .map { QueueEntry(it, 1, node, pt) }
                })

                while (queue.isNotEmpty()) {
                    val (point, count, from, previous) = queue.removeFirst()
                    if (point in junctions) {
                        // every junction will meet each other on its own, one-way connections prevent duplicate edges
                        from.oneWayConnectTo(junctions.getValue(point), count)
                        continue
                    }

                    val possibleDirections = grid.getNeighbors(point, false)
                        .filterValues { it != '#' }
                        .keys
                        .map { it.toPoint() }
                        .filter { it != previous }

                    when (possibleDirections.size) {
                        0 -> when (point) {
                            start -> startNode.connectTo(from, count)
                            end -> endNode.connectTo(from, count)
                            else -> error("Unexpected immovability from $previous to $point")
                        }

                        1 -> {
                            val toGo = possibleDirections.single()
                            queue.add(QueueEntry(toGo, count + 1, from, point))
                        }

                        else -> error("Unexpected junction at $point (point ${if (point in junctions) "" else "!"}in junctions)")
                    }
                }
            }.also { println("Constructing graph took $it") }

            fun slowestPath(graph: Graph<Point>, start: Graph<Point>.Node, end: Graph<Point>.Node, alreadyVisited: MaxStatesSet<Graph<Point>.Node>.Immutable): Int {
                if (start == end) return 0
                val possiblePaths = start.edges
                    .filter { it.to !in alreadyVisited }
                    .map { it.weight!! + slowestPath(graph, it.to, end, alreadyVisited + start) }
                return possiblePaths.maxOrNull() ?: Int.MIN_VALUE
            }

            val stateSet = junctions.values.toMutableSet()
            stateSet.add(startNode)
            stateSet.add(endNode)

            slowestPath(graph, startNode, endNode, MaxStatesSet(stateSet).immutable())
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
