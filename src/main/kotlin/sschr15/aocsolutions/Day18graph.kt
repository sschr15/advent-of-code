package sschr15.aocsolutions

import org.jgrapht.Graph
import org.jgrapht.alg.shortestpath.ALTAdmissibleHeuristic
import org.jgrapht.alg.shortestpath.AStarShortestPath
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.DefaultUndirectedGraph
import sschr15.aocsolutions.util.*

/**
 * AOC 2024 [Day 18](https://adventofcode.com/2024/day/18)
 * Challenge: 
 */
object Day18graph : Challenge {
    override fun solve() = challenge(2024, 18) {
//        test()

        val size = if (_test) 7 else 71

        val graph: Graph<Point, DefaultEdge>
        val corruptions: List<Point>
        val start: Point
        val end: Point
        val pathSearch: AStarShortestPath<Point, DefaultEdge>
        part1 {
            graph = DefaultUndirectedGraph(DefaultEdge::class.java)

            for (x in 0..<size) for (y in 0..<size) {
                val point = Point(x, y)
                graph.addVertex(point)
            }

            for (x in 0..<size) for (y in 0..<size) {
                val point = Point(x, y)
                for (neighbor in point.neighbors()) {
                    if (!graph.containsVertex(neighbor)) continue
                    graph.addEdge(point, neighbor)
                }
            }

            start = Point.origin
            end = Point(size - 1, size - 1)

            corruptions = inputLines
                .map { it.split(",").ints() }
                .map { (a, b) -> Point(a, b) }

            repeat(if (_test) 12 else 1024) {
                graph.removeVertex(corruptions[it])
            }

            pathSearch = AStarShortestPath<Point, DefaultEdge>(graph, ALTAdmissibleHeuristic(graph, setOf(end)))

            pathSearch.getPath(start, end).length
        }
        part2 {
            var next = if (_test) 12 else 1024
            while (pathSearch.getPath(start, end) != null) {
                graph.removeVertex(corruptions[next++])
            }

            val (x, y) = corruptions[next - 1]
            "$x,$y"
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
