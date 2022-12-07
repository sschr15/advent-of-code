@file:Suppress("NOTHING_TO_INLINE")

package sschr15.aocsolutions.util

inline fun Int.toString(chars: Int) = toString().padStart(chars, '0')

inline operator fun String.times(amount: Int) = repeat(amount)

inline operator fun String.get(start: Int, end: Int = length) = substring(start, end)
inline operator fun String.get(range: IntRange) = substring(range)

inline fun String.charList() = map { it }
@JvmName("charsI1d")
inline fun I1d<String>.chars() = map(String::charList)
@JvmName("charsI2d")
inline fun I2d<String>.chars() = map(I1d<String>::chars)
@JvmName("charsI3d")
inline fun I3d<String>.chars() = map(I2d<String>::chars)
@JvmName("charsI4d")
inline fun I4d<String>.chars() = map(I3d<String>::chars)
@JvmName("charsI5d")
inline fun I5d<String>.chars() = map(I4d<String>::chars)
