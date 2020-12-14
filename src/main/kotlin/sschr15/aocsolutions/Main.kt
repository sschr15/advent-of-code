package sschr15.aocsolutions

@ExperimentalUnsignedTypes
fun main(args: Array<String>) {
    val shouldExecuteSlowFunctions = args.contains("--include-slow")

    println("Day 1:")
    day1()
    println("\nDay 2:")
    day2()
    println("\nDay 3:")
    day3()
    println("\nDay 4:")
    day4()
    println("\nDay 5:")
    day5()
    println("\nDay 6:")
    day6()
    println("\nDay 7:")
    day7()
    println("\nDay 8:")
    day8()
    println("\nDay 9:")
    day9()
    println("\nDay 10:")
    day10()
    if (!shouldExecuteSlowFunctions) {
        println("\nSkipping day 11 because it is slow." +
                "Run using Day11Kt.main() or run this with --include-slow to run part 2.")
    } else {
        println("\nDay 11 (slow):")
        day11()
    }
    println("\nDay 12:")
    day12()
    println("\nDay 13:")
    day13(shouldExecuteSlowFunctions)
}