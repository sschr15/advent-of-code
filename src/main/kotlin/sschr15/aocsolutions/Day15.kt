package sschr15.aocsolutions

import com.microsoft.z3.Expr
import com.microsoft.z3.IntSort
import com.sschr15.z3kt.*
import sschr15.aocsolutions.util.*

context(Z3Context)
private fun Expr<IntSort>.abs() = mkITE(this lt 0, -this, this)

object Day15 : Challenge {
    @ReflectivelyUsed
    override fun solve() = challenge(2022) {
//        test()
        data class Sensor(val position: Point, val closestBeacon: Point) {
            val distance get() = position.manhattanDistance(closestBeacon)
        }

        part1 {
            "Skipped" // not trying p1 again
        }
        part2 {
            val sensors = inputLines.map { line ->
                // Sensor at x=..., y=...: closest beacon is at x=..., y=...
                val sensor = line.substringAfter("Sensor at ").substringBefore(":").split(", ").map { it.substringAfter("=").toInt() }
                val beacon = line.substringAfter("closest beacon is at ").split(", ").map { it.substringAfter("=").toInt() }
                Sensor(Point(sensor[0], sensor[1]), Point(beacon[0], beacon[1]))
            }

            z3 { 
                val x = int("x")
                val y = int("y")

                val model = solve {
                    add(x gte 0)
                    add(y gte 0)
                    add(x lte 4_000_000)
                    add(y lte 4_000_000)

                    sensors.forEachIndexed { i, sensor ->
                        val distance = int("d_$i")
                        add(distance eq (x - sensor.position.x).abs() + (y - sensor.position.y).abs())
                        add(distance gt sensor.distance) // each sensor must be further away than the beacon
                    }
                } ?: error("model was deemed unsatisfiable")

                val xVal = model.eval(x, true).toLong()
                val yVal = model.eval(y, true).toLong()

                xVal * 4_000_000 + yVal
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
