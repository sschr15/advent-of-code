package sschr15.aocsolutions

import com.sschr15.templates.invoke
import sschr15.aocsolutions.util.Challenge
import java.io.OutputStream
import java.io.PrintStream
import java.util.FormatProcessor.FMT
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.DurationUnit

object SolutionTimer {
    @JvmStatic
    fun main(args: Array<String>) {
        System.setProperty("aoc.skip.clipboard.copy", "true") // Copying to clipboard is slow and unnecessary

        if (args.isNotEmpty()) {
            runDay(args[0])
            return
        }

        // Run all the challenges
        try {
            repeat(25) {
                runDay("Day${it + 1}")
                println()
            }
        } catch (e: ClassNotFoundException) {
            // If we get here, we've run out of days to run
            println("Done!")
        }
    }

    private fun runDay(dayString: String) {
        // Load the challenge class
        val day = if (dayString.matches("Day\\d{1,2}".toRegex())) dayString else error("Invalid day: $dayString")
        val dayClass = Class.forName("sschr15.aocsolutions.$day")
        val challenge = dayClass.kotlin.objectInstance as Challenge

        // Disable output (don't go logging the same thing 20 times)
        val existingOut = System.out
        val nullOutput = PrintStream(OutputStream.nullOutputStream())
        System.setOut(nullOutput)

        // Run the challenge 20 times
        val times = mutableListOf<Duration>()
        repeat(1000) { times.add(challenge.solve()) }

        // Re-enable output
        System.setOut(existingOut)

        // Calculate and print the times + fun stats
        val nsTimes = times.map { it.toDouble(DurationUnit.NANOSECONDS) }
        val average = nsTimes.average().nanoseconds
        val varianceMs = times.map { (it - average).toDouble(DurationUnit.MILLISECONDS).pow(2) }.average()
        val stdDev = sqrt(varianceMs)
        val min = times.min()
        val max = times.max()

        println("Times for day ${day.substring(3)}:")
        println("Average: $average")
        println("Min: $min")
        println("Max: $max")

        // The string here uses Java's FMT String template, available in Java 21 and later.
        // In java, the function's argument would be written as:
        //     FMT."Standard Deviation: %.2f\{stdDev}ms"
        // The exclamation point in this version is for my templates-kt library, in order
        // to skirt around Kotlin's direct injection of variables into strings.
        println(FMT { "Standard Deviation: %.2f${!stdDev}ms" })
    }
}
