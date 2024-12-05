package sschr15.aocsolutions

import sschr15.aocsolutions.util.*
import sschr15.aocsolutions.util.watched.*

/**
 * AOC 2024 [Day 5](https://adventofcode.com/2024/day/5)
 * Challenge: Whoops, printing doesn't go in order! must fix
 */
object Day5 : Challenge {
    override fun solve() = challenge(2024, 5) {
//        test()
        splitBy("\n\n")
        val dependents: Map<WatchedInt, List<WatchedInt>>
        val invalid: List<List<WatchedInt>>
        part1 {
            dependents = inputLines.first().split("\n").map { it.split("|").ints() }
                .groupBy { (a) -> a }
                .mapValues { (_, v) -> v.map { ints -> ints[1] } }

            val lists: List<List<WatchedInt>> = inputLines[1].split("\n").map { it.split(",").ints() }
            val updates = lists.groupBy { 
                it.mapIndexed { i, pg -> dependents[pg]?.all { prio -> prio !in it.subList(0, i) } }
                    .all { it != false }
            }
            val valid = updates[true]!!
            invalid = updates[false]!!
            valid.sumOf { it[it.size / 2] }
        }
        part2 {
            val requirements = inputLines.first().split("\n").map { it.split("|").ints() }
                .groupBy { (_, a) -> a }
                .mapValues { (_, v) -> v.map { ints -> ints[0] } }

            invalid.map {
                val dependencies = it.groupBy { i ->
                    requirements[i]?.filter { req -> req in it }?.size
                        ?: 0
                }
                dependencies.entries.sortedBy { (k, v) -> k }
                    .flatMap { (_, v) -> v }
            }.sumOf { it[it.size / 2] }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
