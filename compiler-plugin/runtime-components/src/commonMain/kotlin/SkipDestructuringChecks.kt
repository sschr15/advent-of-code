package com.sschr15.aoc.annotations

/**
 * Marks a function, class, or file to skip destructuring checks.
 * 
 * When using this compiler plugin, the plugin will check that all elements of a destructuring declaration are used
 * if the destructed item is a [Collection].
 * If this behavior is not desired, adding `@SkipDestructuringChecks` will disable it.
 */
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.EXPRESSION,
)
@Retention(AnnotationRetention.SOURCE)
annotation class SkipDestructuringChecks
