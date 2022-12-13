package sschr15.aocsolutions

import sschr15.aocsolutions.util.*

/**
 * AOC 2022 [Day 12](https://adventofcode.com/2022/day/12)
 * Challenge: Pathfinding! Climb a mountain and find the shortest path to the top.
 */
object Day12 : Challenge {
    @ReflectivelyUsed
    override fun solve() = challenge(2022, 12) {
//        test()
        part1 {
            val map = inputLines.chars().toGrid()
            val startPoint = map.toPointMap().filterValues { it == 'S' }.keys.first()
            val endPoint = map.toPointMap().filterValues { it == 'E' }.keys.first()
            map[startPoint] = 'a'
            map[endPoint] = 'z'

            val tree = node(startPoint)
            var points = mutableListOf(startPoint to tree)
            while (endPoint !in tree.flatten().map { it.value }) {
                val newPoints = mutableListOf<Pair<Point, Node<Point>>>()
                points.forEach { (point, node) ->
                    val neighbors = map.getNeighbors(point, false)
                        .filterValues { it <= map[point] || it - 1 == map[point] }
                        .keys
                        .map(AbstractPoint::toPoint)
                        .filter { p -> p !in tree.flatten().map { it.value } } // don't go back

                    neighbors.forEach { neighbor ->
                        newPoints.add(neighbor to node.addChild(neighbor))
                    }
                }
                points = newPoints
            }
            val endNode = tree.flatten().first { it.value == endPoint }
            submit(endNode.line().size - 1)

            addInfo("map", map)
            addInfo("p1", endNode.line())
            addInfo("endPoint", endPoint)
        }
//        part2 {
//            TODO("I failed miserably at this one")
//        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
