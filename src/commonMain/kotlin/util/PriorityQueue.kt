package sschr15.aocsolutions.util

expect class PriorityQueue<E : Any>(comparator: Comparator<in E>) {
    fun poll(): E
    fun add(element: E): Boolean
    val size: Int
    fun isEmpty(): Boolean
    fun peek(): E?
}

fun <E : Any> PriorityQueue<E>.isNotEmpty() = !isEmpty()
