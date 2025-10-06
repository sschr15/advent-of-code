package sschr15.aocsolutions.util.graphs

import kotlinx.cinterop.CValuesRef
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.ptr
import lib.igraph.*

actual class Graph<V : Any>(internal val backing: igraph_t, nodes: Array<out V>, weighted: Boolean) : AutoCloseable {
    internal val idToNodeMap = nodes.toMutableList()
    internal val nodeToIdMap: MutableMap<V, igraph_int_t> =
        nodes.withIndex().associate { (index, node) -> node to index.toLong() }.toMutableMap()
    internal val edgeWeights = if (weighted) {
        nativeHeap.alloc<igraph_vector_t>().also { igraph_vector_init(it.ptr, 0) }
    } else null

    actual fun addVertex(v: V): Boolean {
        if (v in this) return false
        igraph_add_vertices(backing.ptr, 1, null)
        nodeToIdMap[v] = idToNodeMap.size.toLong()
        idToNodeMap.add(v)
        return true
    }

    actual fun connect(from: V, to: V, weight: Double) {
        igraph_add_edge(backing.ptr, nodeToIdMap[from]!!, nodeToIdMap[to]!!)
        if (edgeWeights != null) igraph_vector_push_back(edgeWeights.ptr, weight)
    }

    actual fun removeVertex(v: V): Boolean {
        if (v !in this) return false
        if (isWeighted) throw NotImplementedError("Cannot remove vertex with weighted graph on native")
        val id = nodeToIdMap[v]!!
        igraph_delete_vertices(backing.ptr, igraph_vss_1(id))
        if (edgeWeights != null) igraph_vector_remove(edgeWeights.ptr, id)
        nodeToIdMap.remove(v)
        idToNodeMap.removeAt(id.toInt())
        // recompute indices
        idToNodeMap.forEachIndexed { index, n -> nodeToIdMap[n] = index.toLong() }
        return true
    }

    actual operator fun contains(v: V): Boolean {
        return v in nodeToIdMap
    }

    actual val vertices: Set<V>
        get() = idToNodeMap.toSet()

    actual val isDirected: Boolean
        get() = igraph_is_directed(backing.ptr)

    actual val isWeighted: Boolean
        get() = edgeWeights != null

    actual override fun close() {
        idToNodeMap.clear()
        igraph_destroy(backing.ptr)
        edgeWeights?.ptr?.let(::igraph_vector_destroy)
    }
}

actual fun <N : Any> undirectedGraphOf(vararg nodes: N, weighted: Boolean): Graph<N> {
    val backing = nativeHeap.alloc<igraph_t>()
    igraph_create(backing.ptr, null, nodes.size.toLong(), false)

    return Graph(backing, nodes, weighted)
}

actual fun <N : Any> directedGraphOf(vararg nodes: N, weighted: Boolean): Graph<N> {
    val backing = nativeHeap.alloc<igraph_t>()
    igraph_create(backing.ptr, null, nodes.size.toLong(), true)

    return Graph(backing, nodes, weighted)
}

actual fun <N : Any> Graph<in N>.addEdgeAndVertices(from: N, to: N, weight: Double) {
    val fromId = idToNodeMap.size
    val toId = fromId + 1
    igraph_add_vertices(backing.ptr, 2, null)
    nodeToIdMap[from] = fromId.toLong()
    idToNodeMap.add(from)
    nodeToIdMap[to] = toId.toLong()
    idToNodeMap.add(to)
    igraph_add_edge(backing.ptr, fromId.toLong(), toId.toLong())
    if (edgeWeights != null) igraph_vector_push_back(edgeWeights.ptr, weight)
}

private fun <N : Any> Graph<N>.nodeListByVector(block: (CValuesRef<igraph_vector_int_t>) -> Unit) = memScoped { 
    val vector = alloc<igraph_vector_int_t>()
    igraph_vector_int_init(vector.ptr, 0)
    try {
        block(vector.ptr)
        when (val size = igraph_vector_int_size(vector.ptr)) {
            0L -> emptyList()
            1L -> listOf(idToNodeMap[igraph_vector_int_get(vector.ptr, 0).toInt()])
            else -> List(size.toInt()) {
                val nodeId = igraph_vector_int_get(vector.ptr, it.toLong())
                idToNodeMap[nodeId.toInt()]
            }
        }
    } finally {
        igraph_vector_int_destroy(vector.ptr)
    }
}

actual fun <N : Any> Graph<N>.neighborsOf(node: N): List<N> =
    nodeListByVector { igraph_neighbors(backing.ptr, it, nodeToIdMap[node]!!, IGRAPH_ALL, IGRAPH_NO_LOOPS, true) }

actual fun <N : Any> Graph<N>.neighborSetOf(node: N): Set<N> =
    nodeListByVector { igraph_neighbors(backing.ptr, it, nodeToIdMap[node]!!, IGRAPH_ALL, IGRAPH_NO_LOOPS, false) }.toSet()

actual fun <N : Any> Graph<N>.incomingEdgesOf(node: N): List<Edge<N>> = nodeListByVector {
    igraph_neighbors(backing.ptr, it, nodeToIdMap[node]!!, IGRAPH_IN, IGRAPH_LOOPS_ONCE, true)
}.map { Edge(node, it) }

actual fun <N : Any> Graph<N>.outgoingEdgesOf(node: N): List<Edge<N>> = nodeListByVector {
    igraph_neighbors(backing.ptr, it, nodeToIdMap[node]!!, IGRAPH_OUT, IGRAPH_LOOPS_ONCE, true)
}.map { Edge(it, node) }

actual fun <N : Any> Graph<N>.toDot(labelFunction: (N) -> String): String {
    return buildString {
        if (isDirected) append("di")
        appendLine("graph {")
        for (node in vertices) {
            appendLine("n_${nodeToIdMap[node]} [label=\"${labelFunction(node)}\"];")
        }
        memScoped { 
            val edgeList = alloc<igraph_vector_int_t>()
            igraph_vector_int_init(edgeList.ptr, 0)
            try {
                igraph_get_edgelist(backing.ptr, edgeList.ptr, false)
                val vectorSize = igraph_vector_int_size(edgeList.ptr)
                for (i in 0..<vectorSize step 2) {
                    append("n_${igraph_vector_int_get(edgeList.ptr, i)}")
                    append(if (isDirected) " -> " else " -- ")
                    append("n_${igraph_vector_int_get(edgeList.ptr, i + 1)}")
                    if (isWeighted) append(" [label=\"${igraph_vector_get(edgeWeights!!.ptr, i / 2)}\"];")
                    appendLine()
                }
            } finally {
                igraph_vector_int_destroy(edgeList.ptr)
            }
        }
        appendLine("}")
    }
}
