package sschr15.aocsolutions

import com.sschr15.z3kt.*
import sschr15.aocsolutions.util.*

/**
 * AOC 2023 [Day 24](https://adventofcode.com/2023/day/24)
 * Challenge: Killing hundreds of hailstones with one stone
 * ~~that's how the saying goes, right?~~
 */
object Day24 : Challenge {
    @ReflectivelyUsed
    override fun solve() = challenge(2023, 24) {
//        test()
        part1 {
            val min = 200000000000000.0
            val max = 400000000000000.0
            val range = if (!_test) min..max else 7.0..27.0
            val stones = inputLines.map { line ->
                val (pos, vel) = line.split(" @ ")
                val (x, y, _) = pos.split(", ").map { it.trim().toLong().toDouble() }
                val (dx, dy, _) = vel.split(", ").map { it.trim().toLong().toDouble() }

                Ray(x, y, x + dx, y + dy)
            }

            stones.combinations(2).asSequence()
                .map { (a, b) -> a.intersection(b) }
                .filter { (x) -> x.isFinite() } // x will be infinite or nan if there is no valid intersection
                .filter { (x, y) -> x in range && y in range }
                .count()
        }
        part2 {
            z3 { 
                val x = int("x")
                val y = int("y")
                val z = int("z")
                val dx = int("dx")
                val dy = int("dy")
                val dz = int("dz")

                val model = solve {
                    // three lines is technically all it takes because of how aoc gave the input
                    for ((i, line) in inputLines.withIndex().take(3)) {
                        val (pos, vel) = line.split(" @ ")
                        val (xv, yv, zv) = pos.split(", ").map { it.trim().toLong() }
                        val (dxv, dyv, dzv) = vel.split(", ").map { it.trim().toLong() }

                        val t = int("t_$i")
                        add(t gte 0)

                        add(x + t * dx eq xv + t * dxv)
                        add(y + t * dy eq yv + t * dyv)
                        add(z + t * dz eq zv + t * dzv)
                    }
                } ?: error("Failed to solve model")

                val xResult = model.eval(x, true).toLong()
                val yResult = model.eval(y, true).toLong()
                val zResult = model.eval(z, true).toLong()

                // for fun data
                val dxResult = model.eval(dx, true).toLong()
                val dyResult = model.eval(dy, true).toLong()
                val dzResult = model.eval(dz, true).toLong()

                println("$xResult, $yResult, $zResult @ $dxResult, $dyResult, $dzResult")

                xResult + yResult + zResult
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
