package sschr15.aocsolutions.util

class Node<T>(val parent: Node<T>?, val value: T) {
    val children = mutableListOf<Node<T>>()

    override fun toString() = "Node(value=$value, children=$children)" // tracking parent would be recursive
}

fun node(parent: Node<Nothing?>?): Node<Nothing?> = Node(parent, null)

fun <T> node(parent: Node<T>?, value: T): Node<T> = Node(parent, value)

fun <T> node(value: T): Node<T> = Node(null, value)

fun <T> Node<T>.addChild(value: T): Node<T> {
    val child = node(this, value)
    children.add(child)
    return child
}

/**
 * Get all nodes as a simple list
 */
fun <T> Node<T>.flatten(): List<Node<T>> = listOf(this) + children.flatMap { it.flatten() }

/**
 * Get lineage from the root node to this node (including this node)
 */
fun <T> Node<T>.line(): List<Node<T>> = parent?.line()?.plus(this) ?: listOf(this)
