package sschr15.aocsolutions

import sschr15.aocsolutions.util.*

data class Elf(val position: Point, val directions: List<Direction> = listOf(Direction.North, Direction.South, Direction.West, Direction.East)) {
    sealed class Direction(val mod: (Point) -> Point) {
        object North : Direction(Point::up)
        object South : Direction(Point::down)
        object West : Direction(Point::left)
        object East : Direction(Point::right)

        override fun toString() = this::class.simpleName!![0].toString()
    }
}

/**
 * AOC 2022 [Day 23](https://adventofcode.com/2022/day/23)
 * Challenge: Elves are wandering around, trying to social distance. They just happen to be two years late to the party.
 */
object Day23 : Challenge {
    @ReflectivelyUsed
    override fun solve() = challenge(2022, 23) {
        fun attemptMove(elves: List<Elf>): List<Elf> {
            val attemptsToMove = elves.mapNotNull { elf ->
                // step 1: don't move if no elves are directly adjacent
                val noAdjacentElves = Grid.getNeighboringPoints(elf.position, includeDiagonals = true)
                    .none { p -> elves.any { it.position == p } }
                if (noAdjacentElves) return@mapNotNull null

                // step 2: choose a direction
                val next = elf.directions.firstOrNull { direction ->
                    when (direction) {
                        Elf.Direction.North -> {
                            val up = elf.position.up()
                            val uL = up.left()
                            val uR = up.right()
                            elves.none { it.position == up || it.position == uL || it.position == uR }
                        }
                        Elf.Direction.South -> {
                            val down = elf.position.down()
                            val dL = down.left()
                            val dR = down.right()
                            elves.none { it.position == down || it.position == dL || it.position == dR }
                        }
                        Elf.Direction.West -> {
                            val left = elf.position.left()
                            val lU = left.up()
                            val lD = left.down()
                            elves.none { it.position == left || it.position == lU || it.position == lD }
                        }
                        Elf.Direction.East -> {
                            val right = elf.position.right()
                            val rU = right.up()
                            val rD = right.down()
                            elves.none { it.position == right || it.position == rU || it.position == rD }
                        }
                    }
                } ?: return@mapNotNull null // will not move
                next to elf
            }

            val probablePositions = buildMap<Point, MutableList<Elf>> {
                attemptsToMove.forEach { (direction, elf) ->
                    val next = direction.mod(elf.position)
                    getOrPut(next) { mutableListOf() }.add(elf)
                }
            }

            val elvesToMove = probablePositions.filter { it.value.size == 1 }.map { it.value.first() to it.key }.toMap()

            return elves.map {
                it.copy(
                    position = elvesToMove[it] ?: it.position,
                    directions = it.directions.drop(1) + it.directions.first()
                )
            }
        }

//        test()
        part1 {
            var elves = inputLines.mapIndexed { y, s -> s.mapIndexed { x, c ->
                if (c == '#') Elf(Point(x, y)) else null
            } }.flatten().filterNotNull()

            addInfo("elves", elves) // copy initial state for part 2

            repeat(10) {
                elves = attemptMove(elves)
            }

            val minX = elves.minOf { it.position.x }
            val maxX = elves.maxOf { it.position.x }
            val minY = elves.minOf { it.position.y }
            val maxY = elves.maxOf { it.position.y }

            val rectangleWidth = maxX - minX + 1
            val rectangleHeight = maxY - minY + 1

            (rectangleHeight * rectangleWidth) - elves.size // count empty spaces in smallest rectangle
        }
        part2 {
            var elves = getInfo<List<Elf>>("elves")

            // find number of times any elf will move
            fun elvesByPosition() = elves.associateBy { it.position }

            var i = 0
            while (true) {
                i++
                elves = attemptMove(elves)
                val elvesByPosition = elvesByPosition()
                val any = elves.any { elf ->
                    Grid.getNeighboringPoints(elf.position, includeDiagonals = true)
                        .any { p -> elvesByPosition[p] != null }
                }
                if (!any) break
            }

            i + 1 // nothing quite like a good ol' off-by-one error
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
