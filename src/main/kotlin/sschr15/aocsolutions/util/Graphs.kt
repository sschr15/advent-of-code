package sschr15.aocsolutions.util

import java.util.*
import kotlin.collections.ArrayDeque

/**
 * A generic graph structure supporting weighted edges and basic graph operations.
 *
 * This class represents a graph composed of nodes and edges.
 * Nodes store a value and optionally a name, while edges connect nodes with optional weights.
 * The graph supports directed and undirected edges, adding, removing nodes and edges,
 * and generating a DOT representation for visualization.
 *
 * This graph implementation treats all graphs as directed, meaning that edges are one-way connections
 * even if they are created with [Node.connectTo].
 */
class Graph<T> {
    inner class Node internal constructor(val name: String? = null, val value: T) {
        private val _edges = mutableListOf<Edge>()
        private val _incoming = mutableListOf<Edge>()
        private val _outgoing = mutableListOf<Edge>()

        val edges: List<Edge> get() = _edges
        val incomingEdges: List<Edge> get() = _incoming
        val outgoingEdges: List<Edge> get() = _outgoing

        /**
         * Connects the current node to another node, creating a bidirectional edge between them.
         *
         * Adds an edge from this node to the specified node, and another edge in the reverse direction
         * to represent a bidirectional connection. Both nodes' edge lists and the parent Graph's
         * edge list are updated to include this new connection.
         */
        fun connectTo(other: Node, weight: Int? = null) = Edge(this, other, weight).also {
            _edges.add(it)
            _incoming.add(it)
            other._outgoing.add(it)

            val rev = it.reversed()
            _outgoing.add(rev)
            other._edges.add(rev)
            other._incoming.add(rev)

            this@Graph.edges.add(it)
        }

        /**
         * Creates a directed edge between the current node and the specified node.
         *
         * This method establishes a one-way connection from the current node to the other node
         * with an optional weight. The edge is added to both the current node's edge list
         * and the parent graph's edge list.
         */
        fun oneWayConnectTo(other: Node, weight: Int? = null) = Edge(this, other, weight).also {
            _edges.add(it)
            _outgoing.add(it)
            other._incoming.add(it)
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

        internal val parent get() = this@Graph
    }

    inner class Edge internal constructor(val from: Node, val to: Node, val weight: Int? = null) {
        internal fun reversed() = Edge(to, from, weight)

        override fun toString() = "Edge(from=$from, to=$to, weight=$weight)"
    }

    val nodes = mutableSetOf<Node>()
    val edges = mutableSetOf<Edge>()

    fun addNode(value: T, name: String? = null, ) = Node(name, value).also(nodes::add)

    override fun toString() = "Graph(nodes=$nodes, edges=$edges)"

    /**
     * Converts the graph represented by the current instance into a Graphviz DOT format string.
     *
     * This method generates a directed graph representation of the graph's nodes and edges
     * in DOT format, which can be used with Graphviz tools to visualize the structure.
     * Each node is assigned a label based on its name or value.
     * Each edge is represented as a directed connection between nodes.
     *
     * @return A string containing the graph in Graphviz DOT format.
     */
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

/**
 * Convert a `Grid` into a graph representation.
 *
 * @param startPoint the starting point in the grid where the graph generation begins.
 * @param startValue the initial value for the starting node in the graph.
 * @param transform a function that takes a grid point and its value
 *                  and produces the value for the corresponding graph node.
 * @param outgoingNeighbors a function that determines the neighbors of a grid point.
 *                          It takes a point and its value and returns a list of pairs,
 *                          each containing a neighboring point and a cost associated with the connection.
 * @return a `Graph` where nodes represent grid points, their values are determined by the [transform] function,
 *         and edges are created based on the [outgoingNeighbors] function.
 */
fun <T, R> Grid<T>.toGraph(
    startPoint: Point,
    startValue: R,
    transform: (Point, T) -> R,
    outgoingNeighbors: (Point, T) -> List<Pair<Point, Int>>
): Graph<R> {
    val newGraph = Graph<R>()
    val visited = mutableMapOf<Point, Graph<R>.Node>()
    val nonexistentNeighbors = mutableMapOf<Point, MutableList<Pair<Point, Int>>>()
    val queue = ArrayDeque<Pair<Point, T>>()
    queue.add(startPoint to get(startPoint))
    visited[startPoint] = newGraph.addNode(startValue, startPoint.toString())

    for ((point, cost) in outgoingNeighbors(startPoint, get(startPoint))) {
        if (point !in visited) {
            nonexistentNeighbors[point] = mutableListOf(startPoint to cost)
        }
    }

    while (queue.isNotEmpty()) {
        val (point, value) = queue.removeFirst()
        val node = newGraph.addNode(transform(point, value), point.toString())
        visited[point] = node
        for ((neighbor, cost) in outgoingNeighbors(point, value)) {
            val neighborNode = visited[neighbor] ?: run {
                val neighbors = nonexistentNeighbors.getOrPut(neighbor) { mutableListOf() }
                neighbors += point to cost
                continue
            }

            node.oneWayConnectTo(neighborNode, cost)
        }

        nonexistentNeighbors.remove(point)?.forEach { (neighbor, cost) ->
            node.oneWayConnectTo(visited[neighbor]!!, cost)
        }
    }

    return newGraph
}

/**
 * Run a breadth-first search on this node. This function returns a sequence of nodes in the order they were visited.
 */
fun <T> Graph<T>.Node.bfs(): Sequence<Graph<T>.Node> {
    val visited = mutableSetOf<Graph<T>.Node>()
    val queue = ArrayDeque<Graph<T>.Node>()
    queue.add(this)
    visited.add(this)

    return sequence {
        while (queue.isNotEmpty()) {
            val next = queue.removeFirst()
            yield(next)
            next.edges.map { it.to }.filter { it !in visited }.forEach {
                queue.add(it)
                visited.add(it)
            }
        }
    }
}

/**
 * Run a depth-first search on this node. This function returns a sequence of nodes in the order they were visited.
 */
fun <T> Graph<T>.Node.dfs(): Sequence<Graph<T>.Node> {
    val visited = mutableSetOf<Graph<T>.Node>()
    val stack = Stack<Graph<T>.Node>()
    stack.push(this)
    visited.add(this)

    return sequence {
        while (stack.isNotEmpty()) {
            val next = stack.pop()
            yield(next)
            next.edges.map { it.to }.filter { it !in visited }.forEach {
                stack.push(it)
                visited.add(it)
            }
        }
    }
}

/**
 * Run a version of Dijkstra's algorithm on this node.
 * This function returns a map of nodes to their distances from this node.
 */
fun <T> Graph<T>.Node.dijkstra(): Map<Graph<T>.Node, Int> {
    val distances = mutableMapOf<Graph<T>.Node, Int>()
    val queue = PriorityQueue<Pair<Graph<T>.Node, Int>>(compareBy { it.second })
    queue.add(this to 0)

    while (queue.isNotEmpty()) {
        val (node, cost) = queue.poll()
        if (node in distances) continue
        distances[node] = cost
        node.edges.forEach { edge ->
            if (edge.to !in distances) {
                queue.add(edge.to to cost + (edge.weight ?: 1))
            }
        }
    }

    return distances
}

/**
 * Find a path from this node to another node. This function uses breadth-first search.
 */
fun <T> Graph<T>.Node.findPathTo(other: Graph<T>.Node): List<Graph<T>.Edge>? {
    require(parent == other.parent) { "Nodes must be in the same graph" }

    val visited = mutableSetOf<Graph<T>.Node>()
    val queue = ArrayDeque<Graph<T>.Node>()
    val path = mutableMapOf<Graph<T>.Node, Pair<Graph<T>.Node, Graph<T>.Edge>>()

    queue.add(this)
    visited.add(this)

    while (queue.isNotEmpty()) {
        val next = queue.removeFirst()
        next.edges.filter { it.to !in visited }.forEach {
            queue.add(it.to)
            visited.add(it.to)
            path[it.to] = next to it
        }

        if (next == other) {
            return generateSequence(next to null as Graph<T>.Edge?) { path[it.first] }
                .mapNotNull { it.second }
                .toList()
                .asReversed()
        }
    }

    return null // no path found
}

/**
 * Find the shortest path from this node to another node. This function uses a version of Dijkstra's algorithm.
 */
fun <T> Graph<T>.Node.findShortestPathTo(other: Graph<T>.Node): List<Graph<T>.Edge>? {
    require(parent == other.parent) { "Nodes must be in the same graph" }

    val visited = mutableSetOf<Graph<T>.Node>()
    val queue = PriorityQueue<Pair<Graph<T>.Node, Int>>(compareBy { it.second })
    val path = mutableMapOf<Graph<T>.Node, Pair<Graph<T>.Node, Graph<T>.Edge>>()

    queue.add(this to 0)

    while (queue.isNotEmpty()) {
        val (next, cost) = queue.poll()
        if (next in visited) continue
        visited.add(next)

        next.edges.filter { it.to !in visited }.forEach {
            queue.add(it.to to cost + (it.weight ?: 1))
            path[it.to] = next to it
        }

        if (next == other) {
            return generateSequence(next to null as Graph<T>.Edge?) { path[it.first] }
                .mapNotNull { it.second }
                .toList()
                .asReversed()
        }
    }

    return null // no path found
}

/**
 * Find all paths equally short as the shortest path from this node to another node.
 */
fun <T> Graph<T>.Node.findAllShortestPathsTo(other: Graph<T>.Node): List<List<Graph<T>.Edge>> {
    require(parent == other.parent) { "Nodes must be in the same graph" }

    val allShortestPaths = mutableListOf<List<Graph<T>.Edge>>()
    val queue = PriorityQueue<Triple<Int, List<Graph<T>.Edge>, Graph<T>.Node>>(compareBy { it.first })
    queue.add(Triple(0, listOf(), this))

    while (queue.isNotEmpty()) {
        val (cost, path, prev) = queue.poll()
        val last = path.lastOrNull()?.to ?: prev
        if (last == other) {
            if (cost == queue.peek()?.first) allShortestPaths.add(path)
            continue
        }

        prev.edges.forEach { edge ->
            val next = edge.to
            val nextCost = cost + (edge.weight ?: 1)
            if (nextCost > next.dijkstra()[other]!!) return@forEach
            queue.add(Triple(nextCost, path + edge, next))
        }
    }

    return allShortestPaths
}

/**
 * Get all neighboring nodes of this node.
 */
fun <T> Graph<T>.Node.neighbors() = edges.map { it.to }

/**
 * Convert all edges in this list into a set of nodes.
 */
fun <T> List<Graph<T>.Edge>.toNodes() = flatMap { listOf(it.from, it.to) }.toSet()
