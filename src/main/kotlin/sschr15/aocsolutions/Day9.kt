package sschr15.aocsolutions

import sschr15.aocsolutions.util.*
import java.awt.Polygon
import java.awt.Rectangle
import java.awt.geom.Area
import kotlin.math.max
import kotlin.math.min

/**
 * AOC 2025 [Day 9](https://adventofcode.com/2025/day/9)
 * Challenge: what kind of movie theater has tiles that need replacing
 */
object Day9 : Challenge {
    override fun solve() = challenge(2025, 9) {
//        test()

        val points: List<Point>
        fun findAreaOfRectangle(a: Point, b: Point): Long {
            val (x1, y1) = a
            val (x2, y2) = b
            val top = min(y1, y2)
            val bottom = max(y1, y2)
            val left = min(x1, x2)
            val right = max(x1, x2)
            return (bottom - top + 1).toLong() * (right - left + 1).toLong()
        }

        part1 {
            points = inputLines.csv().map { it.ints() }.map { (a, b) -> Point(a, b) }
            points.pairSequence().maxOf {  (a, b) ->
                findAreaOfRectangle(a, b)
            }
        }
        part2 {
            val polygon = Polygon()
            for (point in points) {
                polygon.addPoint(point.x, point.y)
            }
            val polygonArea = Area(polygon)

            points.pairSequence().maxOf { (a, b) ->
                val (x1, y1) = a
                val (x2, y2) = b
                val x = min(x1, x2)
                val y = min(y1, y2)
                val w = max(x1, x2) - x
                val h = max(y1, y2) - y
                val rectangle = Rectangle(x, y, w, h)
                if (rectangle in polygonArea) findAreaOfRectangle(a, b) else 0
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
