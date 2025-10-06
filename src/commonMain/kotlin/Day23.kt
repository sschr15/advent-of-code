package sschr15.aocsolutions

import sschr15.aocsolutions.util.*
import sschr15.aocsolutions.util.graphs.*
import kotlin.jvm.JvmStatic

/**
 * AOC 2024 [Day 23](https://adventofcode.com/2024/day/23)
 * Challenge: 
 */
object Day23 : Challenge {
    override fun solve() = challenge(2024, 23) {
//        test()

        val graph = undirectedGraphOf<String>()
        part1 {
            for (line in inputLines) {
                val (a, b) = line.split("-")
                graph.addEdgeAndVertices(a, b)
            }

            val tNodes = graph.vertices.filter { it.startsWith("t") }

            val sets = mutableSetOf<Set<String>>()
            for (node in tNodes) {
                val neighbors = graph.neighborsOf(node)
                val neighborPairs = neighbors.pairSequence()
                    .filter { (a, b) -> b in graph.neighborSetOf(a) }

                sets += neighborPairs.map { (a, b) -> setOf(a, b, node) }
            }

            sets.size
        }
        part2 {
            val finder = graph.maxCliques()
            val cliques = finder.maxBy { it.size }
            cliques.sorted().joinToString(",")
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
