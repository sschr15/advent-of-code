@file:OptIn(ExperimentalTypeInference::class)
@file:Suppress("NOTHING_TO_INLINE", "unused")

package com.sschr15.aoc.annotations

import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName

@JvmName("sumOfBytes")
inline fun sum(items: Iterable<Byte>): Int {
    var sum = 0
    for (item in items) {
        val prev = sum
        val num = item.toInt()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}

@JvmName("sumOfBytes")
inline fun sum(items: Sequence<Byte>): Int {
    var sum = 0
    for (item in items) {
        val prev = sum
        val num = item.toInt()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}

@JvmName("sumOfBytes")
inline fun sum(items: Array<Byte>): Int {
    var sum = 0
    for (item in items) {
        val prev = sum
        val num = item.toInt()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}

inline fun sum(items: ByteArray): Int {
    var sum = 0
    for (item in items) {
        val prev = sum
        val num = item.toInt()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}

@OverloadResolutionByLambdaReturnType
@JvmName("sumOfBytesToBytes")
inline fun sumOf(items: ByteArray, transform: (Byte) -> Byte): Int {
    var sum = 0
    for (item in items) {
        val prev = sum
        val num = transform(item).toInt()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}

@OverloadResolutionByLambdaReturnType
@JvmName("sumOfBytesToShorts")
inline fun sumOf(items: ByteArray, transform: (Byte) -> Short): Int {
    var sum = 0
    for (item in items) {
        val prev = sum
        val num = transform(item).toInt()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}

@OverloadResolutionByLambdaReturnType
@JvmName("sumOfBytesToInts")
inline fun sumOf(items: ByteArray, transform: (Byte) -> Int): Int {
    var sum = 0
    for (item in items) {
        val prev = sum
        val num = transform(item).toInt()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}

@OverloadResolutionByLambdaReturnType
@JvmName("sumOfBytesToLongs")
inline fun sumOf(items: ByteArray, transform: (Byte) -> Long): Long {
    var sum = 0L
    for (item in items) {
        val prev = sum
        val num = transform(item).toLong()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}

@OverloadResolutionByLambdaReturnType
@JvmName("sumOfCharsToBytes")
inline fun sumOf(items: CharArray, transform: (Char) -> Byte): Int {
    var sum = 0
    for (item in items) {
        val prev = sum
        val num = transform(item).toInt()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}

@JvmName("sumOfShorts")
inline fun sum(items: Iterable<Short>): Int {
    var sum = 0
    for (item in items) {
        val prev = sum
        val num = item.toInt()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}

@JvmName("sumOfShorts")
inline fun sum(items: Sequence<Short>): Int {
    var sum = 0
    for (item in items) {
        val prev = sum
        val num = item.toInt()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}

@JvmName("sumOfShorts")
inline fun sum(items: Array<Short>): Int {
    var sum = 0
    for (item in items) {
        val prev = sum
        val num = item.toInt()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}

inline fun sum(items: ShortArray): Int {
    var sum = 0
    for (item in items) {
        val prev = sum
        val num = item.toInt()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}

@OverloadResolutionByLambdaReturnType
@JvmName("sumOfShortsToBytes")
inline fun sumOf(items: ShortArray, transform: (Short) -> Byte): Int {
    var sum = 0
    for (item in items) {
        val prev = sum
        val num = transform(item).toInt()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}

@OverloadResolutionByLambdaReturnType
@JvmName("sumOfShortsToShorts")
inline fun sumOf(items: ShortArray, transform: (Short) -> Short): Int {
    var sum = 0
    for (item in items) {
        val prev = sum
        val num = transform(item).toInt()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}

@OverloadResolutionByLambdaReturnType
@JvmName("sumOfShortsToInts")
inline fun sumOf(items: ShortArray, transform: (Short) -> Int): Int {
    var sum = 0
    for (item in items) {
        val prev = sum
        val num = transform(item).toInt()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}

@OverloadResolutionByLambdaReturnType
@JvmName("sumOfShortsToLongs")
inline fun sumOf(items: ShortArray, transform: (Short) -> Long): Long {
    var sum = 0L
    for (item in items) {
        val prev = sum
        val num = transform(item).toLong()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}

@OverloadResolutionByLambdaReturnType
@JvmName("sumOfCharsToShorts")
inline fun sumOf(items: CharArray, transform: (Char) -> Short): Int {
    var sum = 0
    for (item in items) {
        val prev = sum
        val num = transform(item).toInt()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}

@JvmName("sumOfInts")
inline fun sum(items: Iterable<Int>): Int {
    var sum = 0
    for (item in items) {
        val prev = sum
        val num = item.toInt()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}

@JvmName("sumOfInts")
inline fun sum(items: Sequence<Int>): Int {
    var sum = 0
    for (item in items) {
        val prev = sum
        val num = item.toInt()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}

@JvmName("sumOfInts")
inline fun sum(items: Array<Int>): Int {
    var sum = 0
    for (item in items) {
        val prev = sum
        val num = item.toInt()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}

inline fun sum(items: IntArray): Int {
    var sum = 0
    for (item in items) {
        val prev = sum
        val num = item.toInt()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}

@OverloadResolutionByLambdaReturnType
@JvmName("sumOfIntsToBytes")
inline fun sumOf(items: IntArray, transform: (Int) -> Byte): Int {
    var sum = 0
    for (item in items) {
        val prev = sum
        val num = transform(item).toInt()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}

@OverloadResolutionByLambdaReturnType
@JvmName("sumOfIntsToShorts")
inline fun sumOf(items: IntArray, transform: (Int) -> Short): Int {
    var sum = 0
    for (item in items) {
        val prev = sum
        val num = transform(item).toInt()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}

@OverloadResolutionByLambdaReturnType
@JvmName("sumOfIntsToInts")
inline fun sumOf(items: IntArray, transform: (Int) -> Int): Int {
    var sum = 0
    for (item in items) {
        val prev = sum
        val num = transform(item).toInt()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}

@OverloadResolutionByLambdaReturnType
@JvmName("sumOfIntsToLongs")
inline fun sumOf(items: IntArray, transform: (Int) -> Long): Long {
    var sum = 0L
    for (item in items) {
        val prev = sum
        val num = transform(item).toLong()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}

@OverloadResolutionByLambdaReturnType
@JvmName("sumOfCharsToInts")
inline fun sumOf(items: CharArray, transform: (Char) -> Int): Int {
    var sum = 0
    for (item in items) {
        val prev = sum
        val num = transform(item).toInt()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}

@JvmName("sumOfLongs")
inline fun sum(items: Iterable<Long>): Long {
    var sum = 0L
    for (item in items) {
        val prev = sum
        val num = item.toLong()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}

@JvmName("sumOfLongs")
inline fun sum(items: Sequence<Long>): Long {
    var sum = 0L
    for (item in items) {
        val prev = sum
        val num = item.toLong()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}

@JvmName("sumOfLongs")
inline fun sum(items: Array<Long>): Long {
    var sum = 0L
    for (item in items) {
        val prev = sum
        val num = item.toLong()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}

inline fun sum(items: LongArray): Long {
    var sum = 0L
    for (item in items) {
        val prev = sum
        val num = item.toLong()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}

@OverloadResolutionByLambdaReturnType
@JvmName("sumOfLongsToBytes")
inline fun sumOf(items: LongArray, transform: (Long) -> Byte): Int {
    var sum = 0
    for (item in items) {
        val prev = sum
        val num = transform(item).toInt()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}

@OverloadResolutionByLambdaReturnType
@JvmName("sumOfLongsToShorts")
inline fun sumOf(items: LongArray, transform: (Long) -> Short): Int {
    var sum = 0
    for (item in items) {
        val prev = sum
        val num = transform(item).toInt()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}

@OverloadResolutionByLambdaReturnType
@JvmName("sumOfLongsToInts")
inline fun sumOf(items: LongArray, transform: (Long) -> Int): Int {
    var sum = 0
    for (item in items) {
        val prev = sum
        val num = transform(item).toInt()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}

@OverloadResolutionByLambdaReturnType
@JvmName("sumOfLongsToLongs")
inline fun sumOf(items: LongArray, transform: (Long) -> Long): Long {
    var sum = 0L
    for (item in items) {
        val prev = sum
        val num = transform(item).toLong()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}

@OverloadResolutionByLambdaReturnType
@JvmName("sumOfCharsToLongs")
inline fun sumOf(items: CharArray, transform: (Char) -> Long): Long {
    var sum = 0L
    for (item in items) {
        val prev = sum
        val num = transform(item).toLong()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}

@OverloadResolutionByLambdaReturnType
@JvmName("sumToBytes")
inline fun <T> sumOf(items: Iterable<T>, transform: (T) -> Byte): Int {
    var sum = 0
    for (item in items) {
        val prev = sum
        val num = transform(item).toInt()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}

@OverloadResolutionByLambdaReturnType
@JvmName("sumToShorts")
inline fun <T> sumOf(items: Iterable<T>, transform: (T) -> Short): Int {
    var sum = 0
    for (item in items) {
        val prev = sum
        val num = transform(item).toInt()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}

@OverloadResolutionByLambdaReturnType
@JvmName("sumToInts")
inline fun <T> sumOf(items: Iterable<T>, transform: (T) -> Int): Int {
    var sum = 0
    for (item in items) {
        val prev = sum
        val num = transform(item).toInt()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}

@OverloadResolutionByLambdaReturnType
@JvmName("sumToLongs")
inline fun <T> sumOf(items: Iterable<T>, transform: (T) -> Long): Long {
    var sum = 0L
    for (item in items) {
        val prev = sum
        val num = transform(item).toLong()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}

@OverloadResolutionByLambdaReturnType
@JvmName("sumToBytes")
inline fun <T> sumOf(items: Sequence<T>, transform: (T) -> Byte): Int {
    var sum = 0
    for (item in items) {
        val prev = sum
        val num = transform(item).toInt()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}

@OverloadResolutionByLambdaReturnType
@JvmName("sumToShorts")
inline fun <T> sumOf(items: Sequence<T>, transform: (T) -> Short): Int {
    var sum = 0
    for (item in items) {
        val prev = sum
        val num = transform(item).toInt()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}

@OverloadResolutionByLambdaReturnType
@JvmName("sumToInts")
inline fun <T> sumOf(items: Sequence<T>, transform: (T) -> Int): Int {
    var sum = 0
    for (item in items) {
        val prev = sum
        val num = transform(item).toInt()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}

@OverloadResolutionByLambdaReturnType
@JvmName("sumToLongs")
inline fun <T> sumOf(items: Sequence<T>, transform: (T) -> Long): Long {
    var sum = 0L
    for (item in items) {
        val prev = sum
        val num = transform(item).toLong()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}

@OverloadResolutionByLambdaReturnType
@JvmName("sumToBytes")
inline fun <T> sumOf(items: Array<T>, transform: (T) -> Byte): Int {
    var sum = 0
    for (item in items) {
        val prev = sum
        val num = transform(item).toInt()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}

@OverloadResolutionByLambdaReturnType
@JvmName("sumToShorts")
inline fun <T> sumOf(items: Array<T>, transform: (T) -> Short): Int {
    var sum = 0
    for (item in items) {
        val prev = sum
        val num = transform(item).toInt()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}

@OverloadResolutionByLambdaReturnType
@JvmName("sumToInts")
inline fun <T> sumOf(items: Array<T>, transform: (T) -> Int): Int {
    var sum = 0
    for (item in items) {
        val prev = sum
        val num = transform(item).toInt()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}

@OverloadResolutionByLambdaReturnType
@JvmName("sumToLongs")
inline fun <T> sumOf(items: Array<T>, transform: (T) -> Long): Long {
    var sum = 0L
    for (item in items) {
        val prev = sum
        val num = transform(item).toLong()
        sum += num
        if ((sum xor prev) and (sum xor num) < 0) {
            throw ArithmeticException("Integer overflow")
        }
    }
    return sum
}