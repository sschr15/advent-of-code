package sschr15.aocsolutions

import sschr15.aocsolutions.util.*

/**
 * AOC 2024 [Day 18](https://adventofcode.com/2024/day/18)
 * Challenge: 
 */
object Day18graph : Challenge {
    override fun solve() = challenge(2024, 18) {
//        test()

        val size = if (_test) 7 else 71

        val graph: Graph<Point>
        val corruptions: List<Graph<Point>.Node>
        val start: Graph<Point>.Node
        val end: Graph<Point>.Node
        part1 {
            graph = Graph()
            val pointsToNodes = mutableMapOf<Point, Graph<Point>.Node>()
            for (x in 0..<size) for (y in 0..<size) {
                val point = Point(x, y)
                val node = graph.addNode(point)
                pointsToNodes[point] = node
            }

            for (x in 0..<size) for (y in 0..<size) {
                val point = Point(x, y)
                val node = pointsToNodes[point]!!
                for (neighbor in point.neighbors()) {
                    val neighborNode = pointsToNodes[neighbor] ?: continue // out of bounds
                    node.connectTo(neighborNode, 1)
                }
            }

            start = pointsToNodes[Point.origin]!!
            end = pointsToNodes[Point(size - 1, size - 1)]!!

            corruptions = inputLines
                .map { it.split(",").ints() }
                .map { (a, b) -> Point(a, b) }
                .map { pointsToNodes[it]!! }

            repeat(if (_test) 12 else 1024) {
                graph.removeNode(corruptions[it])
            }

            start.findPathTo(end)!!.size
        }
        part2 {
            var next = if (_test) 12 else 1024
            while (start.findPathTo(end) != null) {
                graph.removeNode(corruptions[next++])
            }

            val (x, y) = corruptions[next - 1].value
            "$x,$y"
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
