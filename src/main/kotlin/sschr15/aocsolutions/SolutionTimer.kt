package sschr15.aocsolutions

import sschr15.aocsolutions.util.Challenge
import java.io.OutputStream
import java.io.PrintStream
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.DurationUnit

object SolutionTimer {
    @JvmStatic
    fun main(args: Array<String>) {
        System.setProperty("aoc.skip.clipboard.copy", "true") // Copying to clipboard is slow and unnecessary

        // Load the challenge class
        val day = if (args[0].matches("Day\\d{1,2}".toRegex())) args[0] else error("Invalid day: ${args[0]}")
        val dayClass = Class.forName("sschr15.aocsolutions.$day")
        val challenge = dayClass.kotlin.objectInstance as Challenge

        // Disable output (don't go logging the same thing 20 times)
        val existingOut = System.out
        val nullOutput = PrintStream(OutputStream.nullOutputStream())
        System.setOut(nullOutput)

        // Run the challenge 20 times
        val times = mutableListOf<Duration>()
        repeat(100) { times.add(challenge.solve()) }

        // Re-enable output
        System.setOut(existingOut)

        // Calculate and print the times + fun stats
        val nsTimes = times.map { it.toDouble(DurationUnit.NANOSECONDS) }
        val average = nsTimes.average().nanoseconds
        val varianceMs = times.map { (it - average).toDouble(DurationUnit.MILLISECONDS).pow(2) }.average()
        val stdDev = sqrt(varianceMs)
        val min = times.min()
        val max = times.max()

        println("Average: $average")
        println("Min: $min")
        println("Max: $max")
        println("Standard deviation: ${"%.2f".format(stdDev)}")
    }
}
