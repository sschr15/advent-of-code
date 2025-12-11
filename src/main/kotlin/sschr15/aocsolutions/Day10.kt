package sschr15.aocsolutions

import com.microsoft.z3.ArithExpr
import com.sschr15.chekt.Memoize
import com.sschr15.z3kt.*
import sschr15.aocsolutions.util.*
import java.util.PriorityQueue

/**
 * AOC 2025 [Day 10](https://adventofcode.com/2025/day/10)
 * Challenge: These elves have time to decorate (until we decide to fix their machinery, that is)
 */
object Day10 : Challenge {
    override fun solve() = challenge(2025, 10) {
//        test()

        val regex = Regex("""^\[([.#]+)] ((?:\(\d+(?:,\d+)*\) )+)\{(\d+(?:,\d+)*)}$""")

        val buttonsAndJoltages = mutableListOf<Pair<List<Set<Int>>, List<Int>>>()
        part1 {
            inputLines.sumOf { line ->
                if (!line.matches(regex)) error(line)
                val (initialStateText, buttonsText, joltagesText) = regex.matchEntire(line)!!.destructured
                val initialState = initialStateText.map { it == '#' }
                val buttons = buttonsText.split(" ").dropLast(1).map {
                    it.substring(1, it.length - 1).split(",").ints().toSet()
                }
                val joltages = joltagesText.split(",").ints()
                buttonsAndJoltages += buttons to joltages
                val visited = mutableSetOf<List<Boolean>>()
                val queue = PriorityQueue<Pair<List<Boolean>, Int>>(compareBy { it.second })
                queue.add(initialState to 0)
                while (queue.isNotEmpty()) {
                    val (state, cost) = queue.poll()
                    if (!visited.add(state)) continue
                    if (state.none { it }) {
                        return@sumOf cost
                    }
                    for (button in buttons) {
                        val newState = state.toMutableList()
                        for (index in button) {
                            newState[index] = !newState[index]
                        }
                        queue.add(newState to cost + 1)
                    }
                }
                error("No solution found")
            }
        }
        part2 {
            Class.forName("com.microsoft.z3.Native")
            z3 {
                buttonsAndJoltages.sumOf { (buttons, targetJoltages) ->
                    val joltages = targetJoltages.map { it.toZ3Int() }
                    val buttonPressCounts = buttons.associateWith { button -> int("button_${button.joinToString(",")}") }
                    val totalPresses = buttonPressCounts.values.fold(0.toZ3Int() as ArithExpr<IntSort>) { total, next -> total + next }
                    val model = optimize {
                        for (count in buttonPressCounts.values) {
                            add(count gte 0)
                        }

                        for (i in targetJoltages.indices) {
                            val contributions = buttonPressCounts.filterKeys { button -> i in button }.values
                            add(joltages[i] eq contributions.fold(0.toZ3Int() as ArithExpr<IntSort>) { total, next -> total + next })
                        }
                        MkMinimize(totalPresses)
                    } ?: error("UNSAT")
                    model[totalPresses].toLong()
                }
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
