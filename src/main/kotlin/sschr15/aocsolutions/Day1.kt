package sschr15.aocsolutions

@ExperimentalUnsignedTypes
fun main() {
    val funStart = System.currentTimeMillis()

    var partStart = funStart
    val nums = getChallenge(2020, 1).readLines().map { it.toInt() }

    for (i in nums.indices) {
        for (j in i until nums.size) {
            if (nums[i] + nums[j] == 2020) {
                println("${nums[i]} * ${nums[j]} == ${nums[i] * nums[j]}")
            }
        }
    }

    println("Part 1 completed in ${System.currentTimeMillis() - partStart}ms")

    partStart = System.currentTimeMillis()

    for (i in nums.indices) {
        for (j in i until nums.size) {
            if (j == nums[nums.size - 1]) continue
            for (k in j until nums.size) {
                if (nums[i] + nums[j] + nums[k] == 2020) {
                    println("${nums[i]} * ${nums[j]} * ${nums[k]} == ${nums[i] * nums[j] * nums[k]}")
                }
            }
        }
    }

    println("Part 2 completed in ${System.currentTimeMillis() - partStart}ms")

    partStart = System.currentTimeMillis()

    val challenge = getChallenge(0, 1).readLines().map { it.toInt() }.filter { it < 3232322 }
    for (i in challenge.indices) {
        for (j in i until challenge.size) {
            for (k in j until challenge.size) {
                if (challenge[i] + challenge[j] + challenge[k] == 3232322) {
                    println("${challenge[i]} * ${challenge[j]} * ${challenge[k]} == " +
                            "${challenge[i].toLong() * challenge[j].toLong() * challenge[k].toLong()}")
                }
            }
        }
    }

    println("Part 3 completed in ${System.currentTimeMillis() - partStart}ms")
    println("Task completed in ${System.currentTimeMillis() - funStart}ms")
}