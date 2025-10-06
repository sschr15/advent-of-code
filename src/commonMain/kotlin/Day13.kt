package sschr15.aocsolutions

import com.sschr15.z3kt.*
import sschr15.aocsolutions.util.*
import kotlin.jvm.JvmStatic

/**
 * AOC 2024 [Day 13](https://adventofcode.com/2024/day/13)
 * Solves the challenge involving calculating tokens required for given prize targets.
 */
object Day13 : Challenge {
    override fun solve() = challenge(2024, 13) {
//        test()

        splitBy("\n\n")

        val prizeList: List<IntArray>

        part1 {
            val regex =
                """Button A: X([+-]\d+), Y([+-]\d+)\nButton B: X([+-]\d+), Y([+-]\d+)\nPrize: X=(\d+), Y=(\d+)""".toRegex()

            prizeList = inputLines.filter { it.isNotBlank() }.map {
                val match = regex.matchEntire(it)
                    ?: error("Input format does not match expected pattern")
                match.groupValues.drop(1).map(String::toInt).toIntArray()
            }

            z3 {
                prizeList.sumOf { (ax, ay, bx, by, px, py) ->
                    val aPresses by int
                    val bPresses by int

                    val result = optimize {
                        add(aPresses gte 0)
                        add(bPresses gte 0)
                        add(ax * aPresses + bx * bPresses eq px)
                        add(ay * aPresses + by * bPresses eq py)
                        minimize(aPresses + bPresses)
                    }
                    if (result == null) return@sumOf 0

                    result.eval(aPresses * 3 + bPresses, true).toLong()
                }
            }
        }
        part2 {
            z3 {
                prizeList.sumOf { (ax, ay, bx, by, px, py) ->
                    val aPresses by int
                    val bPresses by int

                    val result = optimize {
                        add(aPresses gte 0)
                        add(bPresses gte 0)
                        add(ax * aPresses + bx * bPresses eq (px + 10000000000000))
                        add(ay * aPresses + by * bPresses eq (py + 10000000000000))
                        minimize(aPresses + bPresses)
                    }
                    if (result == null) return@sumOf 0

                    result.eval(aPresses * 3 + bPresses, true).toLong()
                }
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
