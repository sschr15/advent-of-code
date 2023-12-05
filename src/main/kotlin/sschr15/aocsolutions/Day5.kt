package sschr15.aocsolutions

import sschr15.aocsolutions.util.*
import kotlin.math.max
import kotlin.math.min

/**
 * AOC 2023 [Day 5](https://adventofcode.com/2023/day/5)
 * Challenge: Them farmers don't know how to decode their new fancy almanac
 */
object Day5 : Challenge {
    @ReflectivelyUsed
    override fun solve() = challenge(2023, 5) {
        splitBy("\n\n")
//        test()
        part1 {
            val seeds = inputLines[0].findLongs()
            val maps = mutableMapOf<String, Pair<String, MutableList<Pair<LongRange, LongRange>>>>()
            for (map in inputLines.drop(1)) {
                val (sourceType, _, destType) = map.substringBefore(" ").split("-")
                map.trim().split("\n").drop(1).forEach {
                    val (targetStart, sourceStart, length) = it.findLongs()
                    val sourceRange = sourceStart..<sourceStart + length
                    val targetRange = targetStart..<targetStart + length
                    maps.getOrPut(sourceType) { destType to mutableListOf() }.second.add(sourceRange to targetRange)
                }
            }

            seeds.minOf { 
                var currentType = "seed"
                var currentLocation = it
                while (currentType != "location") {
                    val (nextType, list) = maps[currentType]!!
                    currentType = nextType
                    val result = list.firstOrNull { (sourceRange, _) -> currentLocation in sourceRange }
                    if (result == null) continue
                    val (sourceRange, targetRange) = result
                    currentLocation = targetRange.first + (currentLocation - sourceRange.first)
                }
                currentLocation
            }
        }
        part2 {
            val seeds = inputLines[0].findLongs().chunked(2).map { (start, length) -> start..<start + length }
            val maps = mutableMapOf<String, Pair<String, MutableList<Pair<LongRange, LongRange>>>>()
            for (map in inputLines.drop(1)) {
                val (sourceType, _, destType) = map.substringBefore(" ").split("-")
                map.trim().split("\n").drop(1).forEach {
                    val (targetStart, sourceStart, length) = it.findLongs()
                    val sourceRange = sourceStart..<sourceStart + length
                    val targetRange = targetStart..<targetStart + length
                    maps.getOrPut(sourceType) { destType to mutableListOf() }.second.add(sourceRange to targetRange)
                }
            }

            seeds.minOf { seedRange ->
                var currentType = "seed"
                var currentRanges = listOf(seedRange)
                while (currentType != "location") {
                    val (nextType, list) = maps[currentType]!!
                    currentType = nextType
                    val newRanges = mutableListOf<LongRange>()
                    for (range in currentRanges) {
                        val start = range.first
                        val end = range.last

                        val rangesContainingAnything = list.filter { (sourceRange, _) ->
                            sourceRange.first < end && sourceRange.last > start
                        }
                        if (rangesContainingAnything.isEmpty()) {
                            newRanges.add(range) // no change
                            continue
                        }

                        val currentlyUnclaimedRanges = mutableListOf(range)
                        rangesContainingAnything.forEach { 
                            val (sourceRange, targetRange) = it
                            val offset = targetRange.first - sourceRange.first

                            val iter = currentlyUnclaimedRanges.listIterator()
                            while (iter.hasNext()) {
                                val currentRange = iter.next()
                                val currentStart = currentRange.first
                                val currentEnd = currentRange.last

                                if (currentStart > sourceRange.last || currentEnd < sourceRange.first)
                                    continue // this section is not affected

                                val laterStart = max(currentStart, sourceRange.first)
                                val earlierEnd = min(currentEnd, sourceRange.last)
                                iter.remove()

                                newRanges.add(laterStart + offset..earlierEnd + offset)
                                if (laterStart > currentStart) { // something before the range
                                    iter.add(currentStart..<laterStart)
                                }
                                if (earlierEnd < currentEnd) { // something after the range
                                    iter.add(earlierEnd + 1..currentEnd)
                                }
                            }
                        }

                        newRanges.addAll(currentlyUnclaimedRanges)
                    }
                    currentRanges = newRanges
                }
                currentRanges.minOf { it.first }
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = println("Time: ${solve()}")
}
