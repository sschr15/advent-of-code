package sschr15.aocsolutions

import sschr15.aocsolutions.util.*

/**
 * AOC 2024 [Day 4](https://adventofcode.com/2024/day/4)
 * Challenge: help with a word search, but we misunderstood an X-MAS search for an XMAS search
 */
object Day4 : Challenge {
    override fun solve() = challenge(2024, 4) {
//        test()

        part1 {
            fun Grid<Char>.findXmas(point: Point) =
                listOf<(Point) -> Point>(
                    Point::up, Point::down, Point::left, Point::right,
                    { p: Point -> p.up().left() }, { p: Point -> p.down().left() }, { p: Point -> p.up().right() }, { p: Point -> p.down().right() }
                ).count { next ->
                    next(next(next(point))) in this && get(point) == 'X' &&
                    get(next(point)) == 'M' && get(next(next(point))) == 'A' &&
                    get(next(next(next(point)))) == 'S'
                }

            inputLines.toGrid().let { it.toPointMap().keys.sumOf { p -> it.findXmas(p) } }
        }
        part2 {
            fun Grid<Char>.findXmas(point: Point): Boolean {
                if (point !in this || get(point) != 'A') return false
                if (point.up() !in this || point.left() !in this || point.down() !in this || point.right() !in this) return false

                val topLeft = get(point.up().left())
                val topRight = get(point.up().right())
                val bottomLeft = get(point.down().left())
                val bottomRight = get(point.down().right())

                when (topLeft) {
                    'M' -> if (bottomRight != 'S') return false
                    'S' -> if (bottomRight != 'M') return false
                    else -> return false
                }

                when (topRight) {
                    'M' -> if (bottomLeft != 'S') return false
                    'S' -> if (bottomLeft != 'M') return false
                    else -> return false
                }

                return true
            }

            inputLines.toGrid().let { it.toPointMap().keys.count { p -> it.findXmas(p) } }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
