package sschr15.aocsolutions

import sschr15.aocsolutions.util.Challenge
import sschr15.aocsolutions.util.ReflectivelyUsed
import sschr15.aocsolutions.util.challenge
import sschr15.aocsolutions.util.findLongs

/**
 * AOC 2023 [Day 15](https://adventofcode.com/2023/day/15)
 * Challenge: Baby's first hashmap
 */
object Day15 : Challenge {
    @ReflectivelyUsed
    override fun solve() = challenge(2023, 15) {
        splitBy(",")
//        test()
        part1 {
            inputLines.sumOf {
                it.trim('\n').fold(0.toLong()) { acc, c -> (acc + c.code) * 17 and 0xff }
            }
        }
        part2 {
            val boxes = Array<MutableList<Pair<String, Int>>>(256) { mutableListOf() }

            inputLines.forEach { s ->
                val label = s
                    .takeWhile { it.isLetter() }
                val hash = label
                    .fold(0.toLong()) { acc, c -> (acc + c.code) * 17 and 0xff }

                val result = s.findLongs().singleOrNull()

                if (result == null) boxes[hash.toInt()].removeIf { (toRemove, _) -> toRemove == label }
                else {
                    val list = boxes[hash.toInt()]
                    val index = list.indexOfFirst { (toRemove, _) -> toRemove == label }
                    if (index == -1) list.add(label to result.toInt())
                    else list[index] = label to result.toInt()
                }
            }

            boxes.mapIndexed { i, lenses ->
                lenses.mapIndexed { index, (_, strength) ->
                    strength * (i + 1) * (index + 1)
                }.sum()
            }.sum()
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
