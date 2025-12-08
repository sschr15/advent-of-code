package sschr15.aocsolutions

import sschr15.aocsolutions.util.*

/**
 * AOC 2025 [Day 7](https://adventofcode.com/2025/day/7)
 * Challenge: hehe particle physics go brr
 */
object Day7 : Challenge {
    override fun solve() = challenge(2025, 7) {
//        test()

        val grid: Grid<Char>
        val start: Point
        part1 {
            grid = inputLines.chars().toGrid()
            start = grid.toPointMap().entries.single { (_, c) -> c == 'S' }.key

            val visited = mutableSetOf<Point>()
            val queue = ArrayDeque<Point>()
            queue.add(start)
            var i = 0

            while (queue.isNotEmpty()) {
                val current = queue.removeFirst()
//                grid[current] = '|'
                if (!visited.add(current)) continue
                val down = current.down()
                if (down !in grid) {
                    continue
                }
                when (grid[down]) {
                    '^' -> {
                        i++
                        queue.add(down.left())
                        queue.add(down.right())
                    }
                    else -> queue.add(down)
                }
            }
//            println(grid)
            i
        }
        part2 {
            val states = mutableMapOf(start.x to 1L)
            for (line in grid.drop(1)) {
                val line = line.toList()
                val prevState = states.toMap()
                states.clear()
                for ((state, count) in prevState) {
                    if (line[state] == '^') {
                        states[state - 1] = (states[state - 1] ?: 0) + count
                        states[state + 1] = (states[state + 1] ?: 0) + count
                    } else {
                        states[state] = (states[state] ?: 0) + count
                    }
                }
            }
            states.values.sum()
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println(solve())
}
