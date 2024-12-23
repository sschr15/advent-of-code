package sschr15.aocsolutions

import org.jgrapht.Graphs
import org.jgrapht.alg.clique.BronKerboschCliqueFinder
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.DefaultUndirectedGraph
import sschr15.aocsolutions.util.*
import org.jgrapht.Graph as JGraph

/**
 * AOC 2024 [Day 23](https://adventofcode.com/2024/day/23)
 * Challenge: 
 */
object Day23 : Challenge {
    override fun solve() = challenge(2024, 23) {
//        test()

        val graph: JGraph<String, DefaultEdge> = DefaultUndirectedGraph(DefaultEdge::class.java)
        part1 {
            for (line in inputLines) {
                val (a, b) = line.split("-")
                Graphs.addEdgeWithVertices(graph, a, b)
            }

            val tNodes = graph.vertexSet().filter { it.startsWith("t") }

            val sets = mutableSetOf<Set<String>>()
            for (node in tNodes) {
                val neighbors = Graphs.neighborListOf(graph, node)
                val neighborPairs = neighbors.pairSequence()
                    .filter { (a, b) -> b in Graphs.neighborSetOf(graph, a) }

                sets += neighborPairs.map { (a, b) -> setOf(a, b, node) }
            }

            sets.size
        }
        part2 {
            val finder = BronKerboschCliqueFinder(graph)
            val cliques = finder.maxBy { it.size }
            cliques.sorted().joinToString(",")
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
