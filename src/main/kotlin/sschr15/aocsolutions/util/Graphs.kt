package sschr15.aocsolutions.util

class Graph<T> {
    inner class Node internal constructor(val name: String? = null, val value: T) {
        val edges = mutableListOf<Edge>()

        fun connectTo(other: Node, weight: Int? = null) = Edge(this, other, weight).also {
            edges.add(it)
            other.edges.add(it.reversed())
            this@Graph.edges.add(it)
        }

        fun oneWayConnectTo(other: Node, weight: Int? = null) = Edge(this, other, weight).also {
            edges.add(it)
            this@Graph.edges.add(it)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Graph<*>.Node) return false

            if (name != other.name) return false
            if (value != other.value) return false

            return true
        }

        override fun hashCode(): Int {
            var result = name?.hashCode() ?: 0
            result = 31 * result + value.hashCode()
            return result
        }

        override fun toString(): String {
            return "Node(name=$name, value=$value, edges.size=${edges.size})"
        }
    }

    inner class Edge internal constructor(val from: Node, val to: Node, val weight: Int? = null) {
        internal fun reversed() = Edge(to, from, weight)

        override fun toString() = "Edge(from=$from, to=$to, weight=$weight)"
    }

    val nodes = mutableSetOf<Node>()
    val edges = mutableSetOf<Edge>()

    fun addNode(value: T, name: String? = null, ) = Node(name, value).also(nodes::add)

    override fun toString() = "Graph(nodes=$nodes, edges=$edges)"

    fun toGraphviz() = buildString { 
        append("digraph {\n")
        nodes.forEach { node ->
            append("    n_${node.name ?: node.value.hashCode()} [label=\"${node.name ?: node.value}\"];\n")
        }

        edges.forEach { edge ->
            append("    n_${edge.from.name ?: edge.from.value.hashCode()} -> n_${edge.to.name ?: edge.to.value.hashCode()};\n")
        }

        append("}")
    }
}

fun <T> Graph<T>.Node.bfs(): Sequence<Graph<T>.Node> {
    val visited = mutableSetOf<Graph<T>.Node>()
    val queue = ArrayDeque<Graph<T>.Node>()
    queue.add(this)
    visited.add(this)

    return generateSequence(this) {
        val next = queue.removeFirstOrNull() ?: return@generateSequence null
        next.edges.map { it.to }.filter { it !in visited }.forEach {
            queue.add(it)
            visited.add(it)
        }
        next
    }
}

fun <T> Graph<T>.Node.dfs(): Sequence<Graph<T>.Node> {
    val visited = mutableSetOf<Graph<T>.Node>()
    val stack = ArrayDeque<Graph<T>.Node>()
    stack.add(this)
    visited.add(this)

    return generateSequence(this) {
        val next = stack.removeLastOrNull() ?: return@generateSequence null
        next.edges.map { it.to }.filter { it !in visited }.forEach {
            stack.add(it)
            visited.add(it)
        }
        next
    }
}

fun <T> Graph<T>.Node.dijkstra(): Map<Graph<T>.Node, Int> = dijkstra(
    this to 0,
    { (node, _) -> node.edges.map { it.to to it.weight }.filterSecondNotNull() },
    { (_, cost) -> cost }
).mapKeys { it.key.first }
