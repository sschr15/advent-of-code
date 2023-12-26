package sschr15.aocsolutions

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import sschr15.aocsolutions.util.*

/**
 * AOC 2023 [Day 25](https://adventofcode.com/2023/day/25)
 * Challenge: We did it, we've brought snow back across the globe!
 */
object Day25 : Challenge {
    @ReflectivelyUsed
    override fun solve() = challenge(2023, 25) {
//        test()
        part1 {
            val components = inputLines.associate { it.substringBefore(":") to it.substringAfter(": ").split(" ") }.toMutableMap()

            // step 1: find the "choke points" in the graph
            // step 2: solve the thing

            val states = components.entries.flatMap { it.value + it.key }.toSet()

            var answer = 0
            while (true) {
                val graph = Graph<String>()
                val nodes = states.associateWith { graph.addNode(it, it) }

                components.forEach { (key, value) ->
                    value.forEach { nodes.getValue(key).connectTo(nodes.getValue(it)) }
                }

                val edgeUses = Object2IntOpenHashMap<Graph<String>.Edge>()

                // there are 3 edges that need to be removed
                repeat(3) {
                    edgeUses.clear() // reset to remove any edges that were removed in the last iteration (a forward and reverse edge)
                    graph.edges.forEach { edgeUses[it] = 0 }

                    // using the simple method of "if you do it enough, you probably will get the right answer"
                    // more tests = fewer retries, but longer runtime per trial, giving a max value before it's counterproductive
                    // but fewer tests also leads to longer runtime in total, so it's a balancing act
                    // my computer averages 75ms (with ~70ms standard deviation) when running 25 repeats
                    repeat(25) inner@{
                        val start = nodes.values.random()
                        val end = nodes.values.random()

                        val path = start.findPathTo(end) ?: return@inner
                        path.forEach { edgeUses.put(it, edgeUses.getOrDefault(it as Any, 0) + 1) }
                    }

                    val edge = edgeUses.object2IntEntrySet().maxBy { it.intValue }.key
                    graph.removeEdge(edge) // bye (gone) (forever) (without a trace) (no one will miss you) (cya!)
                }

                val groupA = nodes.values.first().bfs().toSet()
                val groupB = nodes.values.toSet() - groupA

                answer = groupA.size * groupB.size

                if (answer != 0) break

                // if we made it here, we did not, in fact, remove all three edges that split the graph into two and therefore should start over
                println("Trial and error made an error, must try again")
            }

            answer
        }
        // No part 2 on Christmas, happy chrysler
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
