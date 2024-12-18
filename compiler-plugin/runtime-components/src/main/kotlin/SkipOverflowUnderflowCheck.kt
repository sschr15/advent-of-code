package com.sschr15.aoc.annotations

/**
 * Marks an expression to skip overflow and underflow checks.
 * Any called functions will still have checks applied unless also marked.
 */
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.EXPRESSION,
)
@Retention(AnnotationRetention.SOURCE)
annotation class SkipOverflowUnderflowCheck
