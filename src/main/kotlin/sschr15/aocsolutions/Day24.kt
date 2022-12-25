package sschr15.aocsolutions

import sschr15.aocsolutions.util.*

data class Blizzard(
    val direction: Direction,
    val position: Point,
)

data class BlizzardStates(
    val blizzards: Set<Blizzard>,
    val areaWidth: Int,
    val areaHeight: Int,
) {
    val next by lazy {
        copy(blizzards = blizzards.map {
            val (x, y) = it.direction.mod(it.position)
            val newX = x mod areaWidth
            val newY = y mod areaHeight
            it.copy(position = Point(newX, newY))
        }.toSet())
    }
}

data class D24State(
    val blizzards: BlizzardStates,
    val location: Point,
    val attemptedEndLocation: Point,
    val stepsTaken: Int,
)

/**
 * AOC 2022 [Day 24](https://adventofcode.com/2022/day/24)
 * Challenge: TODO (based on the day's description)
 */
object Day24 : Challenge {
    @ReflectivelyUsed
    override fun solve() = challenge(2022, 24) {
//        test()
        part1 {
            /*
             * #S#### (start)
             * #    #
             * #    #
             * #    #
             * ####E# (end)
             */

            val areaWidth = inputLines[0].length - 2 // edges
            val areaHeight = inputLines.size - 2 // edges

            val blizzards = inputLines.mapIndexed { y, line ->
                line.mapIndexedNotNull { x, c ->
                    when (c) {
                        '^' -> Direction.North
                        '>' -> Direction.East
                        'v' -> Direction.South
                        '<' -> Direction.West
                        else -> null
                    }?.let { Blizzard(it, Point(x - 1, y - 1)) }
                }
            }.flatten().toSet()

            val startLocation = Point(0, -1) // "y" on the border

            val initialState = D24State(
                blizzards = BlizzardStates(blizzards, areaWidth, areaHeight),
                location = startLocation,
                attemptedEndLocation = Point(areaWidth - 1, areaHeight),
                stepsTaken = 0,
            )

            val states = ArrayDeque(listOf(initialState))

            fun complete(state: D24State) = state.location == state.attemptedEndLocation.up()

            while (states.isNotEmpty()) {
                val state = states.removeFirst()
                if (complete(state)) {
                    // before returning, add some info for p2
                    addInfo("state", state)
                    addInfo("complete", ::complete)

                    return@part1 state.stepsTaken + 1
                }

                val newBlizzards = state.blizzards.next

                states.addAll(with(state.location) { listOf(up(), right(), down(), left(), this) }
                    .filter { (it.x in 0 ..< areaWidth && it.y in 0 ..< areaHeight) || it == startLocation }
                    .filter { it !in newBlizzards.blizzards.map(Blizzard::position) }
                    .map {
                        state.copy(blizzards = newBlizzards, location = it, stepsTaken = state.stepsTaken + 1)
                    }
                )

                if (state.stepsTaken > 1000) {
                    println("uh oh - more than 1000 steps")
                }

                // prune duplicate states
                val newStates = states.groupBy { it.blizzards to it.location }.values.map { it.minBy { s -> s.stepsTaken } }
                states.clear()
                states.addAll(newStates)
            }

            error("no solution found (for part 1)")
        }
        part2 {
            // return to beginning and again to end
            val (initialState, start) = getInfo<D24State>("state").let {
                it.copy(
                    location = it.attemptedEndLocation,
                    attemptedEndLocation = Point(0, -1),
                    stepsTaken = it.stepsTaken + 1,
                    blizzards = it.blizzards.next, // blizzards *have* moved
                ) to it.attemptedEndLocation
            }

            val complete = getInfo<(D24State) -> Boolean>("complete")
            fun atStart(state: D24State) = state.location.up() == initialState.attemptedEndLocation

            val nowAtBeginning: D24State = run {
                val states = ArrayDeque(listOf(initialState))
                while (states.isNotEmpty()) {
                    val state = states.removeFirst()
                    if (atStart(state)) {
                        return@run state.copy(
                            location = initialState.attemptedEndLocation,
                            attemptedEndLocation = initialState.location, // back to end
                            stepsTaken = state.stepsTaken + 1,
                            blizzards = state.blizzards.next,
                        )
                    }

                    val newBlizzards = state.blizzards.next

                    states.addAll(with(state.location) { listOf(up(), right(), down(), left(), this) }
                        .filter { (it.x in 0 ..< initialState.blizzards.areaWidth && it.y in 0 ..< initialState.blizzards.areaHeight) || it == start }
                        .filter { it !in newBlizzards.blizzards.map(Blizzard::position) }
                        .map {
                            state.copy(blizzards = newBlizzards, location = it, stepsTaken = state.stepsTaken + 1)
                        }
                    )

                    // prune duplicate states
                    val newStates = states.groupBy { it.blizzards to it.location }.values.map { it.minBy { s -> s.stepsTaken } }
                    states.clear()
                    states.addAll(newStates)
                }

                error("no solution found (for part 2, going back to beginning)")
            }

            val states = ArrayDeque(listOf(nowAtBeginning))
            while (states.isNotEmpty()) {
                val state = states.removeFirst()
                if (complete(state)) {
                    return@part2 state.stepsTaken + 1
                }

                val newBlizzards = state.blizzards.next

                states.addAll(with(state.location) { listOf(up(), right(), down(), left(), this) }
                    .filter { (it.x in 0 ..< initialState.blizzards.areaWidth && it.y in 0 ..< initialState.blizzards.areaHeight) || it == initialState.attemptedEndLocation }
                    .filter { it !in newBlizzards.blizzards.map(Blizzard::position) }
                    .map {
                        state.copy(blizzards = newBlizzards, location = it, stepsTaken = state.stepsTaken + 1)
                    }
                )

                // prune duplicate states
                val newStates = states.groupBy { it.blizzards to it.location }.values.map { it.minBy { s -> s.stepsTaken } }
                states.clear()
                states.addAll(newStates)
            }

            error("no solution found (for part 2, going back to end)")
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
