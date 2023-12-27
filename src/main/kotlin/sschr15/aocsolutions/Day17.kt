package sschr15.aocsolutions

import sschr15.aocsolutions.util.*
import java.util.*

object Day17 : Challenge {
    @ReflectivelyUsed
    override fun solve() = challenge(2022) {
//        test()
        part1 {
            val jetSequence = inputLines.single().toList().looping { if (it == '<') Direction.West else Direction.East }.iterator()
            val pieceSequence = listOf(
                "####",
                """
                    .#.
                    ###
                    .#.
                """.trimIndent(),
                """
                    ..#
                    ..#
                    ###
                """.trimIndent(),
                """
                    #
                    #
                    #
                    #
                """.trimIndent(),
                """
                    ##
                    ##
                """.trimIndent()
            ).looping()
                .map { it.lines().asReversed() }
                .map { it.flatMapIndexed { y, s -> s.mapIndexedNotNull { x, c -> 
                    if (c == '#') Point(x, y) else null
                } } to it.first().length and it.size }
                .iterator()

            val resultingStack = Stack<List<Char>>()

            repeat(2022) {
                val (piece, width, height) = pieceSequence.next()
                var currentPosition = Point(2, resultingStack.size + 3)
                val maxX = 7 - width
                val currentGrid = resultingStack.toGrid()

                while (true) {
                    var new = jetSequence.next().mod(currentPosition)
                    // coerce into legal bounds
                    new = new.copy(x = new.x.coerceIn(0..maxX))

                    val hitSomethingTryingToMove = piece.any { point ->
                        val realPoint = point + new
                        realPoint in currentGrid && currentGrid[realPoint] == '#'
                    }

                    if (!hitSomethingTryingToMove) {
                        currentPosition = new
                    }

                    val down = currentPosition.up() // "up" refers to y decreasing (but our y increases)
                    val hitSomething = piece.any { point ->
                        val realPoint = point + down
                        realPoint in currentGrid && currentGrid[realPoint] == '#'
                    } || down.y < 0

                    if (hitSomething) {
                        piece.map { it + currentPosition }.forEach { (x, y) ->
                            while (resultingStack.size <= y) resultingStack.push(".......".toList())
                            resultingStack[y] = resultingStack[y].toMutableList().apply { this[x] = '#' }
                        }
                        break
                    }

                    currentPosition = down
                }
            }

            resultingStack.size
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
