package sschr15.aocsolutions

import sschr15.aocsolutions.util.*
import sschr15.aocsolutions.util.graphs.AStar
import sschr15.aocsolutions.util.graphs.Graph
import sschr15.aocsolutions.util.graphs.aStar
import sschr15.aocsolutions.util.graphs.undirectedGraphOf
import kotlin.jvm.JvmStatic

/**
 * AOC 2024 [Day 18](https://adventofcode.com/2024/day/18)
 * Challenge: 
 */
object Day18graph : Challenge {
    override fun solve() = challenge(2024, 18) {
//        test()

        val size = if (testing) 7 else 71

        val graph: Graph<Point>
        val corruptions: List<Point>
        val start: Point
        val end: Point
        val pathSearch: AStar<Point>
        part1 {
            graph = undirectedGraphOf()

            for (x in 0..<size) for (y in 0..<size) {
                val point = Point(x, y)
                graph.addVertex(point)
            }

            for (x in 0..<size) for (y in 0..<size) {
                val point = Point(x, y)
                for (neighbor in point.neighbors()) {
                    if (neighbor !in graph) continue
                    graph.connect(point, neighbor)
                }
            }

            start = Point.origin
            end = Point(size - 1, size - 1)

            corruptions = inputLines
                .map { it.split(",").ints() }
                .map { (a, b) -> Point(a, b) }

            repeat(if (testing) 12 else 1024) {
                graph.removeVertex(corruptions[it])
            }

            pathSearch = graph.aStar { a, b -> a.manhattanDistance(b).toDouble() }

            pathSearch.findPath(start, end)!!.length
        }
        part2 {
            var next = if (testing) 12 else 1024
            while (pathSearch.findPath(start, end) != null) {
                graph.removeVertex(corruptions[next++])
            }

            val (x, y) = corruptions[next - 1]
            "$x,$y"
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
