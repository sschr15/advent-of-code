package sschr15.aocsolutions

import sschr15.aocsolutions.util.*

private typealias Point3d = Triple<Int, Int, Int>
private typealias Cube = Set<Point3d>

/**
 * AOC 2023 [Day 22](https://adventofcode.com/2023/day/22)
 * Challenge: how can we have our sand bricks and disintegrate them too?
 */
object Day22 : Challenge {
    @ReflectivelyUsed
    override fun solve() = challenge(2023, 22) {
//        test()

        fun calculateFalling(bricks: List<Cube>): Pair<List<Cube>, Int> {
            val newState = mutableListOf<Cube>()
            val fallenEntries = mutableSetOf<Point3d>()
            var fallen = 0

            for (brick in bricks.sortedBy { it.minOf(Point3d::third) }) {
                var currentBrickState = brick
                while (true) {
                    val below: Cube = currentBrickState.map { (x, y, z) -> Triple(x, y, z - 1) }.toSet()
                    if (below.any { it in fallenEntries || it.third < 0 }) {
                        newState += currentBrickState
                        fallenEntries += currentBrickState

                        if (currentBrickState != brick) {
                            fallen++
                        }

                        break
                    }
                    currentBrickState = below
                }
            }

            return newState to fallen
        }

        val fallenBricksByRemovedBrick: List<Int>
        part1 {
            val bricks: List<Cube> = inputLines.map { s ->
                s.split("~")
                    .map { it.split(",") }
                    .map { (x, y, z) -> Triple(x.toInt(), y.toInt(), z.toInt()) }
                    .let { (start, end) ->
                        val minX = minOf(start.first, end.first)
                        val maxX = maxOf(start.first, end.first)
                        val minY = minOf(start.second, end.second)
                        val maxY = maxOf(start.second, end.second)
                        val minZ = minOf(start.third, end.third)
                        val maxZ = maxOf(start.third, end.third)

                        (minX..maxX).flatMap { x ->
                            (minY..maxY).flatMap { y ->
                                (minZ..maxZ).map { z ->
                                    Triple(x, y, z)
                                }
                            }
                        }.toSet()
                    }
            }

            val initialState = calculateFalling(bricks).first

            fallenBricksByRemovedBrick = initialState.map { calculateFalling(initialState.minusElement(it)).second }

            fallenBricksByRemovedBrick.count { it == 0 } // number of bricks that don't cause any other bricks to fall if removed
        }
        part2 {
            fallenBricksByRemovedBrick.sum()
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
