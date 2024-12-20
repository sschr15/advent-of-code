package com.sschr15.aoc.annotations

/**
 * Marks an expression to skip overflow checks.
 * Any called functions will still have checks applied unless also marked
 * or built without the compiler plugin (such as the standard libraries).
 */
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.EXPRESSION,
)
@Retention(AnnotationRetention.SOURCE)
annotation class SkipOverflowChecks
