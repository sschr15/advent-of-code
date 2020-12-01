package sschr15.aocsolutions

import java.nio.file.Files
import java.nio.file.Path

fun main() {
    val nums = Files.newBufferedReader(Path.of("inputs", "day1", "input1")).readLines().map { it.toInt() }
    println("Part 1")
    for (i in nums.indices) {
        for (j in i until nums.size) {
            if (nums[i] + nums[j] == 2020) {
                println("${nums[i]} * ${nums[j]} == ${nums[i] * nums[j]}")
            }
        }
    }

    println("Part 2")
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
}