package sschr15.aocsolutions.util.watched

fun Iterable<WatchedInt>.sum() = fold(0.watched()) { acc, i -> acc + i }
fun Iterable<WatchedInt>.product() = fold(1.watched()) { acc, i -> acc * i }
fun Iterable<WatchedInt>.average() = sum() / count()
fun Iterable<WatchedInt>.min() = reduce { acc, i -> if (i < acc) i else acc }
fun Iterable<WatchedInt>.max() = reduce { acc, i -> if (i > acc) i else acc }
fun Iterable<WatchedInt>.minOrNull() = reduceOrNull { acc, i -> if (i < acc) i else acc }
fun Iterable<WatchedInt>.maxOrNull() = reduceOrNull { acc, i -> if (i > acc) i else acc }

fun Iterable<WatchedLong>.sum() = fold(0L.watched()) { acc, i -> acc + i }
fun Iterable<WatchedLong>.product() = fold(1L.watched()) { acc, i -> acc * i }
fun Iterable<WatchedLong>.average() = sum() / count()
fun Iterable<WatchedLong>.min() = reduce { acc, i -> if (i < acc) i else acc }
fun Iterable<WatchedLong>.max() = reduce { acc, i -> if (i > acc) i else acc }
fun Iterable<WatchedLong>.minOrNull() = reduceOrNull { acc, i -> if (i < acc) i else acc }
fun Iterable<WatchedLong>.maxOrNull() = reduceOrNull { acc, i -> if (i > acc) i else acc }

fun Iterable<Int>.watched() = map(Int::watched)
@JvmName("watchedLong") fun Iterable<Long>.watched() = map(Long::watched)
