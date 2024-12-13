package sschr15.aocsolutions

import com.sschr15.z3kt.get
import com.sschr15.z3kt.int
import com.sschr15.z3kt.solve
import com.sschr15.z3kt.z3
import sschr15.aocsolutions.util.*

/**
 * AOC 2024 [Day 13](https://adventofcode.com/2024/day/13)
 * Challenge: TODO
 */
object Day13 : Challenge {
    override fun solve() = challenge(2024, 13) {
//        test()

        splitBy("\n\n")

        val prizeList: List<IntArray>

        part1 {
            val regex =
                """Button A: X([+-]\d+), Y([+-]\d+)\nButton B: X([+-]\d+), Y([+-]\d+)\nPrize: X=(\d+), Y=(\d+)""".toRegex()

            prizeList = inputLines.map {
                val match = regex.matchEntire(it) ?: error("Unexpected")
                match.groupValues.drop(1).map(String::toInt).toIntArray()
            }

            prizeList.sumOf { (ax, ay, bx, by, px, py) ->
                z3 {
                    val aPresses by int
                    val bPresses by int

                    val result = solve {
                        add(aPresses gte 0)
                        add(bPresses gte 0)
                        add(ax * aPresses + bx * bPresses eq px)
                        add(ay * aPresses + by * bPresses eq py)
                    }
                    if (result == null) return@sumOf 0

                    result.evaluate((result[aPresses] * 3 + result[bPresses]), true).toInt()
                }
            }
        }
        part2 {
            prizeList.sumOf { (ax, ay, bx, by, px, py) ->
                z3 {
                    val aPresses by int
                    val bPresses by int

                    val result = solve {
                        add(aPresses gte 0)
                        add(bPresses gte 0)
                        add(ax * aPresses + bx * bPresses eq (px + 10000000000000))
                        add(ay * aPresses + by * bPresses eq (py + 10000000000000))
                    }
                    if (result == null) return@sumOf 0

                    result.evaluate((result[aPresses] * 3 + result[bPresses]), true).toLong()
                }
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
