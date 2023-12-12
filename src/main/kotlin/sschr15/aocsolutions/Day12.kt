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

            val (first, rest) = remaining.first() to remaining.drop(1)
            if (first == '?') {
                return@memoized recurse(".$rest", requirements, inRequirement) +
                        recurse("#$rest", requirements, inRequirement)
            } else if (first == '#') {
                if (requirements.isEmpty()) return@memoized 0
                val newRequirements = listOf(requirements.first() - 1) + requirements.drop(1)
                return@memoized recurse(rest, newRequirements, true)
            } else if (inRequirement) {
                val (firstReq, restReq) = requirements.first() to requirements.drop(1)
                return@memoized if (firstReq == 0L) recurse(rest, restReq, false) else 0 // invalid if there's still a partially unsatisfied requirement
            } else {
                val noStartDot = rest.dropWhile { c -> c == '.' }
                return@memoized recurse(noStartDot, requirements, false)
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
