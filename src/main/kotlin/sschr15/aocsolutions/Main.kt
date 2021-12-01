package sschr15.aocsolutions

fun main(args: Array<String>) {
    val shouldExecuteSlowFunctions = args.contains("--include-slow")

    println("Day 1: Sonar Sweep")
    day1()
}