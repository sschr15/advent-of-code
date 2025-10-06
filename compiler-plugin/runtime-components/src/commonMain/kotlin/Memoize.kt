package com.sschr15.aoc.annotations

/**
 * Marks a function to be memoized by the compiler plugin.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Memoize
