package sschr15.aocsolutions.util

class Graph {
    class Node internal constructor(val name: String? = null, val value: Int? = null, private val containingGraph: Graph) {
        val edges = mutableListOf<Edge>()

        fun connectTo(other: Node, weight: Int? = null) = Edge(this, other, weight).also {
            edges.add(it)
            other.edges.add(it)
            containingGraph.edges.add(it)
        }

        fun oneWayConnectTo(other: Node, weight: Int? = null) = Edge(this, other, weight).also {
            edges.add(it)
            containingGraph.edges.add(it)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Node) return false

            if (name != other.name) return false
            if (value != other.value) return false

            return true
        }

        override fun hashCode(): Int {
            var result = name?.hashCode() ?: 0
            result = 31 * result + (value ?: 0)
            return result
        }

        override fun toString(): String {
            return "Node(name=$name, value=$value, edges.size=${edges.size})"
        }
    }

    data class Edge internal constructor(val from: Node, val to: Node, val weight: Int? = null)

    val nodes = mutableSetOf<Node>()
    val edges = mutableSetOf<Edge>()

    fun addNode(name: String? = null, value: Int? = null) = Node(name, value, this).also(nodes::add)
}

fun Graph.Node.bfs(): Sequence<Graph.Node> {
    val visited = mutableSetOf<Graph.Node>()
    val queue = ArrayDeque<Graph.Node>()
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

fun Graph.Node.dfs(): Sequence<Graph.Node> {
    val visited = mutableSetOf<Graph.Node>()
    val stack = ArrayDeque<Graph.Node>()
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

fun Graph.Node.dijkstra(): Map<Graph.Node, Int> = dijkstra(
    this to 0,
    { (node, _) -> node.edges.map { it.to to it.weight }.filterSecondNotNull() },
    { (_, cost) -> cost }
).mapKeys { it.key.first }
