package sschr15.aocsolutions

import sschr15.aocsolutions.util.*
import sschr15.aocsolutions.util.graphwrap.UnweightedGraph
import sschr15.aocsolutions.util.graphwrap.addAllVertices
import sschr15.aocsolutions.util.graphwrap.connectivity
import sschr15.aocsolutions.util.graphwrap.graphOf
import java.util.*
import kotlin.properties.Delegates

private typealias P3d = Triple<Long, Long, Long>

/**
 * AOC 2025 [Day 8](https://adventofcode.com/2025/day/8)
 * Challenge: That's a... worrying number of extension cords...
 */
object Day8 : Challenge {
    override fun solve() = challenge(2025, 8) {
//        test()
        
        data class Link(val a: P3d, val b: P3d) : Comparable<Link> {
            val distanceSquared by lazy {
                val (x1, y1, z1) = a
                val (x2, y2, z2) = b
                (x1 - x2).toBigInteger().pow(2) + (y1 - y2).toBigInteger().pow(2) + (z1 - z2).toBigInteger().pow(2)
            }

            override fun compareTo(other: Link) = distanceSquared.compareTo(other.distanceSquared)
        }

        val graph: UnweightedGraph<P3d>
        val allLinks: SortedSet<Link>
        part1 {
            val locations = inputLines.csv().map { it.longs().let { (a, b, c) -> a to b and c } }
            graph = graphOf()
            graph.addAllVertices(locations)

            allLinks = locations.pairSequence()
                .map { (a, b) -> Link(a, b) }
                .toSortedSet()

            repeat(if (_test) 10 else 1000) {
                val (a, b) = allLinks.removeFirst()
                graph.addEdge(a, b)
            }

            graph.connectivity().connectedSets().sortedBy { it.size }.takeLast(3).map { it.size }.mul()
        }
        part2 {
            var link by Delegates.notNull<Link>()
            while (!graph.connectivity().isConnected) {
                link = allLinks.removeFirst()
                graph.addEdge(link.a, link.b)
            }
            link.a.first * link.b.first
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
