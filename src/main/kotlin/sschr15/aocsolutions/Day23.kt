package sschr15.aocsolutions

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

        val graph = Graph<String>()
        part1 {
            val cpusToNodes = mutableMapOf<String, Graph<String>.Node>()

            for (line in inputLines) {
                val (a, b) = line.split("-")
                if (a !in cpusToNodes) {
                    cpusToNodes[a] = graph.addNode(a)
                }
                if (b !in cpusToNodes) {
                    cpusToNodes[b] = graph.addNode(b)
                }
            }

            for (line in inputLines) {
                val (a, b) = line.split("-")
                val aNode = cpusToNodes[a]!!
                val bNode = cpusToNodes[b]!!
                aNode.connectTo(bNode)
            }

            val tNodes = mutableListOf<Graph<String>.Node>()

            for (node in graph.nodes) {
                if (node.value.startsWith("t")) tNodes.add(node)
            }

            val sets = mutableSetOf<Set<Graph<String>.Node>>()
            for (node in tNodes) {
                val neighbors = node.neighbors()
                val neighborPairs = neighbors.pairSequence()
                    .filter { (a, b) -> b in a.neighbors() }

                sets += neighborPairs.map { (a, b) -> setOf(a, b, node) }
            }

            sets.size
        }
        part2 {
            val newGraph: JGraph<String, DefaultEdge> = DefaultUndirectedGraph(DefaultEdge::class.java)

            for (line in inputLines) {
                val (a, b) = line.split("-")
                if (!newGraph.containsVertex(a)) {
                    newGraph.addVertex(a)
                }
                if (!newGraph.containsVertex(b)) {
                    newGraph.addVertex(b)
                }
            }

            for (line in inputLines) {
                val (a, b) = line.split("-")
                newGraph.addEdge(a, b)
            }

            val finder = BronKerboschCliqueFinder(newGraph)
            val cliques = finder.maxBy { it.size }
            cliques.sorted().joinToString(",")
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
