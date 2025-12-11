package sschr15.aocsolutions

import com.sschr15.chekt.Memoize
import org.jgrapht.GraphPath
import org.jgrapht.Graphs
import org.jgrapht.alg.shortestpath.AllDirectedPaths
import org.jgrapht.graph.DefaultEdge
import sschr15.aocsolutions.util.*
import sschr15.aocsolutions.util.graphwrap.addAndConnectVertices
import sschr15.aocsolutions.util.graphwrap.allDirectedPaths
import sschr15.aocsolutions.util.graphwrap.directedGraphOf
import sschr15.aocsolutions.util.graphwrap.getOutgoingNeighborsOf
import sschr15.aocsolutions.util.graphwrap.graphOf
import sschr15.aocsolutions.util.graphwrap.toDot

/**
 * AOC 2025 [Day 11](https://adventofcode.com/2025/day/11)
 * Challenge: 
 */
object Day11 : Challenge {
    override fun solve() = challenge(2025, 11) {
//        test()

        val graph = directedGraphOf<String>()

        @Memoize
        fun pathCount(currentNode: String, targetNode: String): Int {
            if (currentNode == targetNode) return 1
            return graph.getOutgoingNeighborsOf(currentNode).sumOf { pathCount(it, targetNode) }
        }

        part1 {
            for (line in inputLines) {
                val (input, outputs) = line.split(": ")
                outputs.split(" ").forEach { graph.addAndConnectVertices(input, it) }
            }

//            graph.allDirectedPaths().getAllPaths("you", "out", true, null).size

            pathCount("you", "out")
        }
        part2 {
//            run {
//                val pathfinder = graph.allDirectedPaths()
//
//                val (a, b) = if (pathfinder.getAllPaths("fft", "dac", true, null).isNotEmpty()) {
//                    "fft" to "dac"
//                } else {
//                    "dac" to "fft"
//                }
//
//                val toFirstCheckpoint = pathfinder.getAllPaths("svr", a, true, null).size
//                val betweenCheckpoints = pathfinder.getAllPaths(a, b, true, null).size
//                val toOutput = pathfinder.getAllPaths(b, "out", true, null).size
//
//                toFirstCheckpoint.toLong() * betweenCheckpoints * toOutput
//            }

            val (a, b) = if (pathCount("fft", "dac") > 0) {
                "fft" to "dac"
            } else {
                "dac" to "fft"
            }

            val toFirstCheckpoint = pathCount("svr", a)
            val betweenCheckpoints = pathCount(a, b)
            val toOutput = pathCount(b, "out")

            toFirstCheckpoint.toLong() * betweenCheckpoints * toOutput
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
