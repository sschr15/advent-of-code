@file:OptIn(ExperimentalForeignApi::class)

package com.sschr15.aoc.annotations

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import platform.posix.getenv
import platform.posix.setenv

@PublishedApi
internal actual inline fun warnIfNegativeRem(a: Int, b: Int) {
    if (a < 0 && b != a && getenv("AOC_REM_WARNINGS")?.toKString() != "false") {
        println("Warning: Remainder of $a (a negative number)")
        setenv("AOC_REM_WARNINGS", "false", 1)
    }
}

@PublishedApi
internal actual inline fun warnIfNegativeRem(a: Long, b: Long) {
    if (a < 0 && b != a && getenv("AOC_REM_WARNINGS")?.toKString() != "false") {
        println("Warning: Remainder of $a (a negative number)")
        setenv("AOC_REM_WARNINGS", "false", 1)
    }
}
