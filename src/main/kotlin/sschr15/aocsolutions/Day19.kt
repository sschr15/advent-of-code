package sschr15.aocsolutions

import sschr15.aocsolutions.util.Challenge
import sschr15.aocsolutions.util.ReflectivelyUsed
import sschr15.aocsolutions.util.challenge
import sschr15.aocsolutions.util.w
import sschr15.aocsolutions.util.watched.sumOf
import sschr15.aocsolutions.util.watched.times
import java.util.ArrayDeque
import kotlin.collections.sumOf

/**
 * AOC 2023 [Day 19](https://adventofcode.com/2023/day/19)
 * Challenge: Help those poor Desert Island elves sort all of those new metal parts from Gear Island
 */
object Day19 : Challenge {
    @ReflectivelyUsed
    override fun solve() = challenge(2023, 19) {
//        test()

        data class State(val x: Int, val m: Int, val a: Int, val s: Int)
        data class RawRule(val requiredOption: Char, val isGreater: Boolean, val requirement: Int, val workflow: String)
        data class RawWorkflow(val name: String, val rules: List<RawRule>, val ifNoneMatch: String)

        val accepted = RawWorkflow("A", listOf(), "R")
        val rejected = RawWorkflow("R", listOf(), "R")

        val forP2 = mutableMapOf(
            "A" to accepted,
            "R" to rejected,
        )

        part1 {
            val workflows = inputLines.takeWhile { it.isNotBlank() }.map { line ->
                val name = line.substringBefore('{')
                val rawRules = mutableListOf<RawRule>()
                val rules = line.substringAfter('{').substringBefore('}').split(",").map {
                    if (':' in it) {
                        val (condition, workflow) = it.split(":")
                        val operation = condition[1]
                        val requirement = condition.substring(2).toInt()

                        val check: (State) -> String? = when (condition.first()) {
                            'x' -> when (operation) {
                                '>' -> { state -> if (state.x > requirement) workflow else null }
                                '<' -> { state -> if (state.x < requirement) workflow else null }
                                else -> error("Unexpected operation $operation")
                            }
                            'm' -> when (operation) {
                                '>' -> { state -> if (state.m > requirement) workflow else null }
                                '<' -> { state -> if (state.m < requirement) workflow else null }
                                else -> error("Unexpected operation $operation")
                            }
                            'a' -> when (operation) {
                                '>' -> { state -> if (state.a > requirement) workflow else null }
                                '<' -> { state -> if (state.a < requirement) workflow else null }
                                else -> error("Unexpected operation $operation")
                            }
                            's' -> when (operation) {
                                '>' -> { state -> if (state.s > requirement) workflow else null }
                                '<' -> { state -> if (state.s < requirement) workflow else null }
                                else -> error("Unexpected operation $operation")
                            }
                            else -> throw IllegalArgumentException("Invalid condition: $condition")
                        }

                        val rule = RawRule(condition.first(), operation == '>', requirement, workflow)
                        rawRules.add(rule)

                        check
                    } else {
                        val result = it
                        val check: (State) -> String? = { _ -> result }

                        forP2[name] = RawWorkflow(name, rawRules, result)

                        check
                    }
                }

                name to { state: State ->
                    rules.firstNotNullOf { it(state) }
                }
            }.toMap()

            val challenges = inputLines.takeLastWhile { it.isNotBlank() }.map { line ->
                val (x, m, a, s) = line.substring(1, line.length - 1).split(",").map { it.substring(2).toInt() }
                State(x, m, a, s)
            }

            challenges.mapNotNull { 
                var current = workflows["in"]!!(it)
                while (current in workflows.keys) {
                    current = workflows[current]!!(it)
                }
                if (current == "A") it else null // "R" is rejected
            }.sumOf { (x, m, a, s) -> x.toLong() + m + a + s }
        }
        part2 {
            data class States(val x: IntRange = 1..4000, val m: IntRange = 1..4000, val a: IntRange = 1..4000, val s: IntRange = 1..4000)

            val queue = ArrayDeque<Pair<States, RawWorkflow>>()
            queue.add(States() to forP2["in"]!!)

            val discoveredStates = mutableSetOf<States>()
            val seenStates = mutableSetOf<States>()

            while (queue.isNotEmpty()) {
                val (state, workflow) = queue.removeFirst()
                if (workflow == rejected) continue // don't bother with rejected states
                if (workflow == accepted) {
                    discoveredStates.add(state)
                    continue
                }

                if (!seenStates.add(state)) continue

                val newStates = mutableSetOf<Pair<States, RawWorkflow>>()

                var eligibleForNext = state
                for ((requiredOption, isGreater, requirement, resultingWorkflow) in workflow.rules) {
                    val range = when (requiredOption) {
                        'x' -> eligibleForNext.x
                        'm' -> eligibleForNext.m
                        'a' -> eligibleForNext.a
                        's' -> eligibleForNext.s
                        else -> throw IllegalArgumentException("Invalid required option: $requiredOption")
                    }

                    val newRange = if (isGreater) {
                        (requirement + 1)..range.last
                    } else {
                        range.first..<requirement
                    }

                    val nextEligibleRange = if (isGreater) {
                        range.first..requirement
                    } else {
                        requirement..range.last
                    }

                    if (!newRange.isEmpty()) {
                        newStates.add(when (requiredOption) {
                            'x' -> eligibleForNext.copy(x = newRange)
                            'm' -> eligibleForNext.copy(m = newRange)
                            'a' -> eligibleForNext.copy(a = newRange)
                            's' -> eligibleForNext.copy(s = newRange)
                            else -> throw IllegalArgumentException("Invalid required option: $requiredOption")
                        } to forP2[resultingWorkflow]!!)

                        eligibleForNext = when (requiredOption) {
                            'x' -> eligibleForNext.copy(x = nextEligibleRange)
                            'm' -> eligibleForNext.copy(m = nextEligibleRange)
                            'a' -> eligibleForNext.copy(a = nextEligibleRange)
                            's' -> eligibleForNext.copy(s = nextEligibleRange)
                            else -> throw IllegalArgumentException("Invalid required option: $requiredOption")
                        }
                    } // else, this rule is impossible to satisfy and we can skip it
                }

                if (!eligibleForNext.x.isEmpty() || !eligibleForNext.m.isEmpty() || !eligibleForNext.a.isEmpty() || !eligibleForNext.s.isEmpty()) {
                    newStates.add(eligibleForNext to forP2[workflow.ifNoneMatch]!!)
                }

                queue.addAll(newStates)
            }

            discoveredStates.sumOf { (x, m, a, s) -> x.count().toLong().w * m.count() * a.count() * s.count() }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
