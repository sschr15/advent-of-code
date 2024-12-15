package sschr15.aocsolutions.util

@OverloadResolutionByLambdaReturnType
@JvmName("sumOfIndexedInts")
inline fun <T> Iterable<T>.sumOfIndexed(selector: (index: Int, T) -> Int): Int {
    var sum = 0
    for ((index, element) in this.withIndex()) {
        sum += selector(index, element)
    }
    return sum
}

@OverloadResolutionByLambdaReturnType
@JvmName("sumOfIndexedLongs")
inline fun <T> Iterable<T>.sumOfIndexed(selector: (index: Int, T) -> Long): Long {
    var sum = 0L
    for ((index, element) in this.withIndex()) {
        sum += selector(index, element)
    }
    return sum
}

@OverloadResolutionByLambdaReturnType
@JvmName("sumOfIndexedFloats")
inline fun <T> Iterable<T>.sumOfIndexed(selector: (index: Int, T) -> Float): Float {
    var sum = 0f
    for ((index, element) in this.withIndex()) {
        sum += selector(index, element)
    }
    return sum
}

@OverloadResolutionByLambdaReturnType
@JvmName("sumOfIndexedDoubles")
inline fun <T> Iterable<T>.sumOfIndexed(selector: (index: Int, T) -> Double): Double {
    var sum = 0.0
    for ((index, element) in this.withIndex()) {
        sum += selector(index, element)
    }
    return sum
}
