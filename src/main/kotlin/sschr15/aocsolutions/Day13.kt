package sschr15.aocsolutions

fun main() {
    day13(true)
}

fun day13(doPartTwo: Boolean) {
    val time = getChallenge(2020, 13).readLines()[0].toInt()
    val busses = getChallenge(2020, 13).readLines()[1].split(",").mapNotNull { it.toIntOrNull() }
    println("Part 1")
    var newTime = time.toLong()
    while (busses.none { newTime % it.toLong() == 0L }) {
        newTime++
    }
    println("Bus ${busses.firstOrNull { newTime % it.toLong() == 0L }!!} arrives ${newTime - time} minutes later. " +
            "Your result is ${busses.firstOrNull { newTime % it.toLong() == 0L }!! * (newTime - time)}")

    if (!doPartTwo) {
        println("Skipping part 2 as it is slow.\n" +
                "Run using Day13Kt.main() or the main method with --include-slow to run part 2.")
        return
    }

    println("Part 2 (slow)")
    // Phil is being really helpful and I'm learning things
    val busTimestamps = getChallenge(2020, 13).readLines()[1].split(",").mapIndexedNotNull { i, bus ->
        try {
            // Why does using the negative offset work? *WHO KNOWS*
            bus.toLong() to -i.toLong()
        } catch (e: NumberFormatException) {
            null
        }
    }.sortedBy { it[0] }.toMutableList()

    while (busTimestamps.size > 1) {
        val bus1 = busTimestamps.removeAt(0)
        val bus2 = busTimestamps.removeAt(0)

        var workOffset1 = bus1[1]
        var workOffset2 = bus2[1]

        var newFrequency = 0L
        var newOffset = 0L

        while (workOffset1 < 0) workOffset1 += bus1[0]
        while (workOffset2 < 0) workOffset2 += bus2[0]

        while (newFrequency == 0L || newOffset == 0L) {
            when {
                workOffset1  > workOffset2 -> workOffset2 += bus2[0]
                workOffset1  < workOffset2 -> workOffset1 += bus1[0]
                workOffset1 == workOffset2 -> {
                    if (newOffset == 0L) newOffset = workOffset1
                    else newFrequency = workOffset1 - newOffset
                    workOffset2 += bus2[0]
                }
            }
        }
        busTimestamps.also { it.add(newFrequency to newOffset) }.sortBy { it[0] }
    }

    println(busTimestamps[0][1])
}