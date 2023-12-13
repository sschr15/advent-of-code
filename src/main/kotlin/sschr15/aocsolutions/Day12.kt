package sschr15.aocsolutions

import sschr15.aocsolutions.util.*

/**
 * AOC 2023 [Day 12](https://adventofcode.com/2023/day/12)
 * Challenge: figure out just how messed up all these spring layouts are
 */
object Day12 : Challenge {
    @ReflectivelyUsed
    override fun solve() = challenge(2023, 12) {
//        test()

        val recursiveCheck = memoized<String, List<Long>, Boolean, Long> { remaining, requirements, inRequirement ->
            if (remaining.isEmpty()) {
                return@memoized if (requirements.isEmpty() || requirements.singleOrNull() == 0L) 1 else 0
            }

            if (requirements.isEmpty()) {
                // no # characters allowed, but otherwise it's a single valid solution
                return@memoized if ('#' !in remaining) 1 else 0
            }

            if (requirements.first() < 0L) return@memoized 0 // already took too many hashes

            val (first, rest) = remaining.first() to remaining.drop(1)
            when {
                first == '?' -> {
                    val withHash = recurse("#$rest", requirements, false)
                    val withoutHash = if (!inRequirement || requirements.first() == 0L) {
                        // only do this if it doesn't break a requirement
                        val newReqs = if (inRequirement) requirements.drop(1) else requirements
                        recurse(".${rest.dropWhile { c -> c == '.' }}", newReqs, false)
                    } else 0
                    withHash + withoutHash
                }
                first == '#' -> {
                    if (requirements.isEmpty()) return@memoized 0
                    val newRest = rest.dropWhile { c -> c == '#' }
                    val removed = 1 + (rest.length - newRest.length)
                    val newRequirements = listOf(requirements.first() - removed) + requirements.drop(1)
                    recurse(newRest, newRequirements, true)
                }
                inRequirement -> {
                    val (firstReq, restReq) = requirements.first() to requirements.drop(1)
                    if (firstReq == 0L) recurse(rest, restReq, false) else 0 // invalid if there's still a partially unsatisfied requirement
                }
                else -> {
                    val noStartDot = rest.dropWhile { c -> c == '.' }
                    recurse(noStartDot, requirements, false)
                }
            }
        }

        part1 {
            inputLines.sumOf { s ->
                val (map, requirements) = s.split(" ")
                val groups = requirements.findLongs()

                recursiveCheck(map, groups, false)
            }
        }
        part2 {
            inputLines.sumOf { 
                val (fakeMap, fakeReqs) = it.split(" ")
                val realMap = listOf(fakeMap, fakeMap, fakeMap, fakeMap, fakeMap).joinToString("?")
                val realReqs = listOf(fakeReqs, fakeReqs, fakeReqs, fakeReqs, fakeReqs).joinToString(",")
                val requirementNums = realReqs.findLongs()

                recursiveCheck(realMap, requirementNums, false)
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
