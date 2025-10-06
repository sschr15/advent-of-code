package sschr15.aocsolutions.util.graphs

import org.jgrapht.Graphs
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.DefaultUndirectedGraph
import org.jgrapht.graph.DefaultWeightedEdge
import sschr15.aocsolutions.util.uncheckedCast
import org.jgrapht.Graph as JGraph

actual class Graph<V : Any>(val underlying: JGraph<V, Any>) : AutoCloseable, JGraph<V, Any> by underlying {
    actual operator fun contains(v: V) = underlying.containsVertex(v)
    actual fun connect(from: V, to: V, weight: Double) {
        if (isWeighted) {
            Graphs.addEdge(underlying, from, to, weight)
        } else {
            underlying.addEdge(from, to)
        }
    }

    actual override fun addVertex(v: V) = underlying.addVertex(v)
    actual override fun removeVertex(v: V) = underlying.removeVertex(v)

    actual val vertices: Set<V> get() = underlying.vertexSet()
    actual val isDirected: Boolean get() = underlying.type.isDirected
    actual val isWeighted: Boolean get() = underlying.type.isWeighted

    actual override fun close() {
        // Do nothing
    }
}

// exists to avoid inferentially typing as an invisible type (future kotlin error prevention)
private fun edgeSupplier(weighted: Boolean): () -> Any =
    if (weighted) ::DefaultWeightedEdge else ::DefaultEdge

actual fun <N : Any> undirectedGraphOf(vararg nodes: N, weighted: Boolean): Graph<N> {
    val graph = DefaultUndirectedGraph<N, Any>(null, edgeSupplier(weighted), weighted)
    nodes.forEach(graph::addVertex)
    return Graph(graph)
}

actual fun <N : Any> directedGraphOf(vararg nodes: N, weighted: Boolean): Graph<N> {
    val graph = DefaultDirectedGraph<N, Any>(null, edgeSupplier(weighted), weighted)
    nodes.forEach(graph::addVertex)
    return Graph(graph)
}

actual fun <N : Any> Graph<in N>.addEdgeAndVertices(from: N, to: N, weight: Double) {
    if (isWeighted) Graphs.addEdgeWithVertices(underlying, from, to, weight)
    else Graphs.addEdgeWithVertices(underlying, from, to)
}

actual fun <N : Any> Graph<N>.neighborsOf(node: N): List<N> = Graphs.neighborListOf(this, node)
actual fun <N : Any> Graph<N>.neighborSetOf(node: N): Set<N> = Graphs.neighborSetOf(this, node)

actual fun <N : Any> Graph<N>.incomingEdgesOf(node: N): List<Edge<N>> = underlying.incomingEdgesOf(node).map { edge -> Edge(underlying.getEdgeSource(edge), underlying.getEdgeTarget(edge)) }
actual fun <N : Any> Graph<N>.outgoingEdgesOf(node: N): List<Edge<N>> = underlying.outgoingEdgesOf(node).map { edge -> Edge(underlying.getEdgeSource(edge), underlying.getEdgeTarget(edge)) }

actual fun <N : Any> Graph<N>.toDot(labelFunction: (N) -> String): String {
    if (type.isDirected) {
        return "digraph {\n" + vertexSet().joinToString("\n") { vertex ->
            val internalName = "n_${vertex.hashCode()}"
            "$internalName [label=\"${labelFunction(vertex)}\"]"
        } + "\n" + edgeSet().joinToString("\n") { edge ->
            val source = getEdgeSource(edge.uncheckedCast())
            val target = getEdgeTarget(edge.uncheckedCast())
            val sourceName = "n_${source.hashCode()}"
            val targetName = "n_${target.hashCode()}"
            "$sourceName -> $targetName"
        } + "\n}"
    } else {
        return "graph {\n" + vertexSet().joinToString("\n") { vertex ->
            val internalName = "n_${vertex.hashCode()}"
            "$internalName [label=\"${labelFunction(vertex)}\"]"
        } + "\n" + edgeSet().joinToString("\n") { edge ->
            val source = getEdgeSource(edge.uncheckedCast())
            val target = getEdgeTarget(edge.uncheckedCast())
            val sourceName = "n_${source.hashCode()}"
            val targetName = "n_${target.hashCode()}"
            "$sourceName -- $targetName"
        } + "\n}"
    }
}
