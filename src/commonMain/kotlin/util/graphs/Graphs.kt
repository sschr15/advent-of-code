package sschr15.aocsolutions.util.graphs

data class Edge<N : Any>(val from: N, val to: N) {
    fun oppositeOf(node: N) = when (node) {
        from -> to
        to -> from
        else -> error("Node $node is not in edge $this")
    }
}

expect class Graph<V : Any> : AutoCloseable {
    fun addVertex(v: V): Boolean
    fun connect(from: V, to: V, weight: Double = 1.0)
    fun removeVertex(v: V): Boolean

    operator fun contains(v: V): Boolean

    val vertices: Set<V>
    val isDirected: Boolean
    val isWeighted: Boolean
    override fun close()
}

expect fun <N : Any> undirectedGraphOf(vararg nodes: N, weighted: Boolean = false): Graph<N>
expect fun <N : Any> directedGraphOf(vararg nodes: N, weighted: Boolean = false): Graph<N>

expect fun <N : Any> Graph<in N>.addEdgeAndVertices(from: N, to: N, weight: Double = 1.0)
expect fun <N : Any> Graph<N>.neighborsOf(node: N): List<N>
expect fun <N : Any> Graph<N>.neighborSetOf(node: N): Set<N>

expect fun <N : Any> Graph<N>.incomingEdgesOf(node: N): List<Edge<N>>
expect fun <N : Any> Graph<N>.outgoingEdgesOf(node: N): List<Edge<N>>
