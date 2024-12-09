@file:OptIn(ExperimentalTypeInference::class)

package sschr15.aocsolutions.util.watched

import kotlin.experimental.ExperimentalTypeInference

inline fun Iterable<WatchedInt>.sum() = fold(0.watched()) { acc, i -> acc + i }
inline fun Iterable<WatchedInt>.product() = fold(1.watched()) { acc, i -> acc * i }
inline fun Iterable<WatchedInt>.average() = sum() / count()
inline fun Iterable<WatchedInt>.min() = reduce { acc, i -> if (i < acc) i else acc }
inline fun Iterable<WatchedInt>.max() = reduce { acc, i -> if (i > acc) i else acc }
inline fun Iterable<WatchedInt>.minOrNull() = reduceOrNull { acc, i -> if (i < acc) i else acc }
inline fun Iterable<WatchedInt>.maxOrNull() = reduceOrNull { acc, i -> if (i > acc) i else acc }

inline fun Sequence<WatchedInt>.sum() = fold(0.watched()) { acc, i -> acc + i }
inline fun Sequence<WatchedInt>.product() = fold(1.watched()) { acc, i -> acc * i }
inline fun Sequence<WatchedInt>.average() = sum() / count()
inline fun Sequence<WatchedInt>.min() = reduce { acc, i -> if (i < acc) i else acc }
inline fun Sequence<WatchedInt>.max() = reduce { acc, i -> if (i > acc) i else acc }
inline fun Sequence<WatchedInt>.minOrNull() = reduceOrNull { acc, i -> if (i < acc) i else acc }
inline fun Sequence<WatchedInt>.maxOrNull() = reduceOrNull { acc, i -> if (i > acc) i else acc }

@OverloadResolutionByLambdaReturnType // quite possibly the longest annotation name i've ever needed to use
inline fun <T> Iterable<T>.sumOf(selector: (T) -> WatchedInt): WatchedInt {
    var sum = 0.watched()
    for (element in this) {
        sum += selector(element)
    }
    return sum
}

@OverloadResolutionByLambdaReturnType
inline fun <T> Sequence<T>.sumOf(selector: (T) -> WatchedInt): WatchedInt {
    var sum = 0.watched()
    for (element in this) {
        sum += selector(element)
    }
    return sum
}

inline fun Iterable<WatchedLong>.sum() = fold(0L.watched()) { acc, i -> acc + i }
inline fun Iterable<WatchedLong>.product() = fold(1L.watched()) { acc, i -> acc * i }
inline fun Iterable<WatchedLong>.average() = sum() / count()
inline fun Iterable<WatchedLong>.min() = reduce { acc, i -> if (i < acc) i else acc }
inline fun Iterable<WatchedLong>.max() = reduce { acc, i -> if (i > acc) i else acc }
inline fun Iterable<WatchedLong>.minOrNull() = reduceOrNull { acc, i -> if (i < acc) i else acc }
inline fun Iterable<WatchedLong>.maxOrNull() = reduceOrNull { acc, i -> if (i > acc) i else acc }

inline fun Sequence<WatchedLong>.sum() = fold(0L.watched()) { acc, i -> acc + i }
inline fun Sequence<WatchedLong>.product() = fold(1L.watched()) { acc, i -> acc * i }
inline fun Sequence<WatchedLong>.average() = sum() / count()
inline fun Sequence<WatchedLong>.min() = reduce { acc, i -> if (i < acc) i else acc }
inline fun Sequence<WatchedLong>.max() = reduce { acc, i -> if (i > acc) i else acc }
inline fun Sequence<WatchedLong>.minOrNull() = reduceOrNull { acc, i -> if (i < acc) i else acc }
inline fun Sequence<WatchedLong>.maxOrNull() = reduceOrNull { acc, i -> if (i > acc) i else acc }

@OverloadResolutionByLambdaReturnType
inline fun <T> Iterable<T>.sumOf(selector: (T) -> WatchedLong): WatchedLong {
    var sum = 0L.watched()
    for (element in this) {
        sum += selector(element)
    }
    return sum
}

@OverloadResolutionByLambdaReturnType
inline fun <T> Sequence<T>.sumOf(selector: (T) -> WatchedLong): WatchedLong {
    var sum = 0L.watched()
    for (element in this) {
        sum += selector(element)
    }
    return sum
}

inline fun Iterable<Int>.watched() = map(Int::watched)
@JvmName("watchedLong") fun Iterable<Long>.watched() = map(Long::watched)
