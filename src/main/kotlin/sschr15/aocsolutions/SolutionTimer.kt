package sschr15.aocsolutions

import com.sschr15.templates.invoke
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import sschr15.aocsolutions.util.*
import java.io.OutputStream
import java.io.PrintStream
import java.util.FormatProcessor.FMT
import kotlin.io.path.Path
import kotlin.io.path.appendText
import kotlin.io.path.createFile
import kotlin.io.path.exists
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

object SolutionTimer {
    @JvmStatic
    fun main(args: Array<String>) {
        System.setProperty("aoc.clipboard.skip", "true") // Copying to clipboard is slow and unnecessary

        if (args.isNotEmpty()) {
            runDay(args[0])
            return
        }

        // Run all the challenges
        try {
            System.setProperty("aoc.timeout", "5s") // Running tests synchronously, decrease wait time
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
        val start = Clock.System.now()

        // Load the challenge class
        val day = if (dayString.matches("Day\\d{1,2}".toRegex())) dayString else error("Invalid day: $dayString")
        val dayClass = Class.forName("sschr15.aocsolutions.$day")
        val challenge = dayClass.kotlin.objectInstance as Challenge

        // Disable output (don't go logging the same thing 20 times)
        val existingOut = System.out
        val nullOutput = PrintStream(OutputStream.nullOutputStream())
        System.setOut(nullOutput)

        // Prep the challenge by running it a handful of times
        // (because JVM startup is slow and class loading is slow)
        val prepTimes = mutableListOf<Duration>()
        repeat(20) { prepTimes.add(challenge.solve()) }

        // Run the challenge many times
        val actualTestStart = Clock.System.now()
        val times = mutableListOf<Duration>()

        val runCount = System.getProperty("aoc.runs")?.toInt()
        if (runCount == null) {
            val timeout = System.getProperty("aoc.timeout")?.let(Duration::parse) ?: 30.seconds

            while (Clock.System.now() - actualTestStart < timeout || (times.size < 250 && Clock.System.now() - actualTestStart < 5.minutes)) {
                times.add(challenge.solve())
            }
        } else {
            repeat(runCount) {
                times.add(challenge.solve())
            }
        }

        // Re-enable output
        System.setOut(existingOut)

        // Calculate and print the times + fun stats
        val nsTimes = times.map { it.toDouble(DurationUnit.NANOSECONDS) }
        val average = nsTimes.average().nanoseconds
        val stdDev = times.map { it.toDouble(DurationUnit.MILLISECONDS) }.stdDev()
        val min = times.min()
        val max = times.max()

        print("Times for day ${day.substring(3)} ")

        if (times.size > 500) {
            println("(${times.size} runs):")
        } else if (times.size > 100) {
            // Yellow: starting to get slow
            println("\u001B[33m(${times.size} runs)\u001B[0m:")
        } else if (times.size > 10) {
            // Red: quite slow
            println("\u001B[31m(${times.size} runs)\u001B[0m:")
        } else {
            // Highlighted red: extraordinarily, outrageously, preposterously slow
            println("\u001B[41m(${times.size} runs, very slow)\u001B[0m:")
        }

        println("Average: $average")
        println("Min: $min")
        println("Max: $max")

        // The string here uses Java's FMT String template, available in Java 21 and later.
        // In java, the function's argument would be written as:
        //     FMT."Standard Deviation: %.2f\{stdDev}ms"
        // The exclamation point in this version is for my templates-kt library, in order
        // to skirt around Kotlin's direct injection of variables into strings.
        println(FMT { "Standard Deviation: %.2f${!stdDev}ms" })

        // Store prep times to a file
        val prepFile = Path("prep_times.txt")
        if (!prepFile.exists()) prepFile.createFile()

        val textToAppend = listOf(
            "Day ${day.substring(3)} Preparation Times (20 runs):",
            "Ran at ${start.toLocalDateTime(TimeZone.UTC)} (UTC)",
            "Average: ${(prepTimes.sumOf { it.toDouble(DurationUnit.NANOSECONDS) } / prepTimes.size).nanoseconds}",
            "Min: ${prepTimes.min()}",
            "Max: ${prepTimes.max()}",
            "Standard Deviation (ms): ${prepTimes.map { it.toDouble(DurationUnit.MILLISECONDS) }.stdDev()}",
        ).joinToString("\n") + "\n\n"

        prepFile.appendText(textToAppend)
    }
}
