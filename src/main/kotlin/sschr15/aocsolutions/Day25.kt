package sschr15.aocsolutions

import sschr15.aocsolutions.util.Challenge
import sschr15.aocsolutions.util.ReflectivelyUsed
import sschr15.aocsolutions.util.challenge

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

//            val states = components.entries.flatMap { it.value + it.key }.toSet()
//
//            val graph = Graph<String>()
//            val nodes = states.associateWith { graph.addNode(it, it) }
//
//            components.forEach { (key, value) ->
//                value.forEach { nodes.getValue(key).connectTo(nodes.getValue(it)) }
//            }
//
//            graph

            val groupB = components.flatMap { it.value + it.key }.toMutableSet()

            val groupA = mutableSetOf<String>()
            val first = components.keys.first()
            groupA.add(first)
            groupA.addAll(components.remove(first)!!)

            while (components.any { (_, v) -> v.any { it in groupA } } || components.keys.any { it in groupA }) {
                components.filter { (_, v) -> v.any { it in groupA } }.forEach {
                    (k, _) ->
                    groupA.addAll(components.remove(k)!!)
                    groupA.add(k)
                }

                components.keys.filter { it in groupA }.forEach {
                    groupA.addAll(components.remove(it)!!)
                }
            }

            groupB.removeAll(groupA)

            groupA.size * groupB.size
        }
        // No part 2 on Christmas, happy chrysler
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
