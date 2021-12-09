package sschr15.aocsolutions

import sschr15.aocsolutions.util.addAllToAll
import sschr15.aocsolutions.util.getChallenge
import sschr15.aocsolutions.util.only
import sschr15.aocsolutions.util.solid
import kotlin.time.measureTime

/**
 * Day 8: Seven Segment Search
 *
 * - Part 1: Find how many times `1`, `4`, `7`, and `8` appear in the input (as 7 segment displays)
 * - Part 2: Find what each number of each output is, and sum all four-digit outputs
 */
fun day8() {
    val input = getChallenge(2021, 8)
        .map { it.removeSuffix("\r") }
        .map { it.split(" | ") }
        .map { it[0].split(' ') to it[1].split(' ') }

    // Part 1
    val segmentCounts = mapOf(
        6 to listOf(0, 6, 9),
        5 to listOf(2, 3, 5),
        2 to listOf(1),
        4 to listOf(4),
        3 to listOf(7),
        7 to listOf(8)
    ).solid(emptyList())

    val result = input.map { (_, output) ->
        output.map {
            segmentCounts[it.length]
        }.filter { it.size == 1 }
    }.flatten().flatten()

    println("Part 1: ${result.count()}")

    val totalTime = measureTime {
        // Part 2
        input.sumOf { (input, output) ->
            fun first(n: Int) =
                output.firstOrNull { n in segmentCounts[it.length] }
                    ?: input.first { n in segmentCounts[it.length] }

            fun allPossible(n: Int) = (
                    output.filter { n in segmentCounts[it.length] }
                            + input.filter { n in segmentCounts[it.length] }
                    )

            val segmentMap = buildMap<Int, MutableList<Char>> {
                repeat(7) { put(it, mutableListOf()) }
            }.solid { throw NoSuchElementException("No value for $it exists!") }

            val mappedSegments = CharArray(7) { '?' }

            val firstOne = first(1)
            val firstFour = first(4)
            val firstSeven = first(7)

            // eight has all segments lit, so ignore it

            segmentMap.addAllToAll(firstOne, 2, 5)
            segmentMap.addAllToAll(firstFour, 1, 2, 3, 5)
            segmentMap.addAllToAll(firstSeven, 0, 2, 5)

            // segment 0 only has `7` so far, we therefore know which one it is
            mappedSegments[0] = segmentMap[0].first { it !in firstOne }

            // `3` has both segments of `1` lit, so we can find which one it is
            val firstThree = allPossible(3).first {
                firstOne.all { c -> c in it }
            }

            // segment 6 is the only unknown segment in `3`
            mappedSegments[6] = firstThree.first {
                it !in firstFour && it != mappedSegments[0]
            }

            // segment 3 is the is in `3` and `4`, but not `7`
            mappedSegments[3] = segmentMap[3].first {
                it in firstThree && it in firstFour && it !in firstSeven
            }

            // segment 1 is in `4` but not `3`
            mappedSegments[1] = segmentMap[1].first {
                it in firstFour && it !in firstThree
            }

            // we can find a `6` which will identify segments 2 and 4
            val firstSix = allPossible(6).first {
                // it contains segments 0, 1, 3, and 6 for sure
                it.toList().containsAll(mappedSegments.only(0, 1, 3, 6))
                        // it only contains one of the segments in `1`
                        && it.toList().count { i -> i in firstOne } == 1
            }

            // segment 5 is in `1` and `6`
            mappedSegments[5] = segmentMap[5].first {
                it in firstOne && it in firstSix
            }

            // segment 2 is the other segment in `1`
            mappedSegments[2] = segmentMap[2].first {
                it in firstOne && it != mappedSegments[5]
            }

            // segment 4 is the last one
            mappedSegments[4] = "abcdefg".filterNot {
                it in mappedSegments.only(0, 1, 2, 3, 5, 6)
            }.first()

            // now we know all the segments, so we can do an inverse mapping to get numbers
            val segmentToNumberMap = listOf(
                mappedSegments.only(0, 1, 2, 4, 5, 6) to "0",
                mappedSegments.only(2, 5) to "1",
                mappedSegments.only(0, 2, 3, 4, 6) to "2",
                mappedSegments.only(0, 2, 3, 5, 6) to "3",
                mappedSegments.only(1, 2, 3, 5) to "4",
                mappedSegments.only(0, 1, 3, 5, 6) to "5",
                mappedSegments.only(0, 1, 3, 4, 5, 6) to "6",
                mappedSegments.only(0, 2, 5) to "7",
                mappedSegments.only(0, 1, 2, 3, 4, 5, 6) to "8",
                mappedSegments.only(0, 1, 2, 3, 5, 6) to "9",

                // A - F because someone looking at my code might want to have hexadecimal capabilities
                mappedSegments.only(0, 1, 2, 3, 4, 5) to "A",
                mappedSegments.only(1, 3, 4, 5, 6) to "B",
                mappedSegments.only(0, 1, 4, 6) to "C",
                mappedSegments.only(2, 3, 4, 5, 6) to "D",
                mappedSegments.only(0, 1, 3, 4, 6) to "E",
                mappedSegments.only(0, 1, 3, 4) to "F"
            ).associate { (segments, number) ->
                segments.sorted().joinToString("") to number
            }.solid { throw IllegalStateException("Couldn't find a number for segment set '$it'") }

            // return four-digit number
            output.joinToString("") {
                segmentToNumberMap[it.toList().sorted().joinToString("")]
            }.toInt()
        }.also {
            println("Part 2: $it")
        }
    }

    if (System.getProperty("debug") != null) {
        println("Time for part 2: $totalTime")
    }
}

fun main() {
    day8()
}
