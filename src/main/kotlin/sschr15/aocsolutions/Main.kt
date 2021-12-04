package sschr15.aocsolutions

fun main(args: Array<String>) {
    // in case there are slow solves, we can skip them if necessary
    val shouldExecuteSlowFunctions = args.contains("--include-slow")

    println("Day 1: Sonar Sweep")
    day1()

    println("Day 2: Dive!")
    day2()

    println("Day 3: Binary Diagnostic")
    day3()

    println("Day 4: Giant Squid")
    day4()
}