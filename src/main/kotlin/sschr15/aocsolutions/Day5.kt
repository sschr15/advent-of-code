package sschr15.aocsolutions

import sschr15.aocsolutions.util.*

/**
 * AOC 2024 [Day 5](https://adventofcode.com/2024/day/5)
 * Challenge: Whoops, printing doesn't go in order! must fix
 */
object Day5 : Challenge {
    override fun solve() = challenge(2024, 5) {
//        test()
        splitBy("\n\n")

        val requirements: Map<Int, Set<Int>>
        val invalid: List<List<Int>>
        part1 {
            requirements = inputLines.first().split("\n").map { it.split("|").ints() }
                .groupBy({ (_, a) -> a }) { (i) -> i }
                .mapValues { (_, list) -> list.toSet() }

            val lists: List<List<Int>> = inputLines[1].split("\n").map { it.split(",").ints() }
            val updates = lists.partition { list ->
                list.asSequence().allIndexed { i, pg ->
                    val reqs = requirements[pg] ?: return@allIndexed true
                    list.subList(i, list.size).none { it in reqs }
                }
            }
            invalid = updates.second
            updates.first.sumOf { it[it.size / 2] }
        }
        part2 {
            invalid.asSequence().sumOf {
                val asSet = it.toSet()
                asSet.groupBy { i ->
                    requirements[i]
                        ?.count { req -> req in asSet }
                        ?: 0
                }.entries
                    .sortedBy { (k) -> k }
                    .flatMap { (_, v) -> v }
                    .let { it[it.size / 2] }
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
