@file:OptIn(ExperimentalTypeInference::class)

package sschr15.aocsolutions.util.watched

import kotlin.experimental.ExperimentalTypeInference

fun Iterable<WatchedInt>.sum() = fold(0.watched()) { acc, i -> acc + i }
fun Iterable<WatchedInt>.product() = fold(1.watched()) { acc, i -> acc * i }
fun Iterable<WatchedInt>.average() = sum() / count()
fun Iterable<WatchedInt>.min() = reduce { acc, i -> if (i < acc) i else acc }
fun Iterable<WatchedInt>.max() = reduce { acc, i -> if (i > acc) i else acc }
fun Iterable<WatchedInt>.minOrNull() = reduceOrNull { acc, i -> if (i < acc) i else acc }
fun Iterable<WatchedInt>.maxOrNull() = reduceOrNull { acc, i -> if (i > acc) i else acc }

@OverloadResolutionByLambdaReturnType // quite possibly the longest annotation name i've ever needed to use
fun <T> Iterable<T>.sumOf(selector: (T) -> WatchedInt) = map(selector).sum()

fun Iterable<WatchedLong>.sum() = fold(0L.watched()) { acc, i -> acc + i }
fun Iterable<WatchedLong>.product() = fold(1L.watched()) { acc, i -> acc * i }
fun Iterable<WatchedLong>.average() = sum() / count()
fun Iterable<WatchedLong>.min() = reduce { acc, i -> if (i < acc) i else acc }
fun Iterable<WatchedLong>.max() = reduce { acc, i -> if (i > acc) i else acc }
fun Iterable<WatchedLong>.minOrNull() = reduceOrNull { acc, i -> if (i < acc) i else acc }
fun Iterable<WatchedLong>.maxOrNull() = reduceOrNull { acc, i -> if (i > acc) i else acc }

@OverloadResolutionByLambdaReturnType
fun <T> Iterable<T>.sumOf(selector: (T) -> WatchedLong) = map(selector).sum()

fun Iterable<Int>.watched() = map(Int::watched)
@JvmName("watchedLong") fun Iterable<Long>.watched() = map(Long::watched)
