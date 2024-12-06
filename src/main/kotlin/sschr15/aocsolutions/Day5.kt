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

        val requirements: Map<WatchedInt, List<WatchedInt>>
        val invalid: List<List<WatchedInt>>
        part1 {
            requirements = inputLines.first().split("\n").map { it.split("|").ints() }
                .groupBy { (_, a) -> a }
                .mapValues { (_, v) -> v.map { ints -> ints[0] } }

            val lists: List<List<WatchedInt>> = inputLines[1].split("\n").map { it.split(",").ints() }
            val updates = lists.partition { list ->
                list.mapIndexed { i, pg ->
                    requirements[pg]?.all { prio -> prio !in list.subList(i, list.size) }
                }.all { it != false }
            }
            invalid = updates.second
            updates.first.sumOf { it[it.size / 2] }
        }
        part2 {
            invalid.map {
                val asSet = it.toSet()
                val dependencies = asSet.groupBy { i ->
                    requirements[i]
                        ?.filter { req -> req in asSet }
                        ?.size
                        ?: 0
                }
                dependencies.entries
                    .sortedBy { (k) -> k }
                    .flatMap { (_, v) -> v }
            }.sumOf { it[it.size / 2] }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
