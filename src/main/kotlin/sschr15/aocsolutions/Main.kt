package sschr15.aocsolutions

import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    // in case there are slow solves, we can skip them if necessary
    val shouldExecuteSlowFunctions = args.contains("--include-slow")

    println("Day 1: Sonar Sweep")
    println("Completed in ${measureTimeMillis { day1() }}ms")

    println("Day 2: Dive!")
    println("Completed in ${measureTimeMillis { day2() }}ms")

    println("Day 3: Binary Diagnostic")
    println("Completed in ${measureTimeMillis { day3() }}ms")

    println("Day 4: Giant Squid")
    println("Completed in ${measureTimeMillis { day4() }}ms")

    println("Day 5: Hydrothermal Venture")
    println("Completed in ${measureTimeMillis { day5() }}ms")
}