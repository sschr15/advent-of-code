package sschr15.aocsolutions.util.graphwrap

import sschr15.aocsolutions.util.*

fun <T> Graph<T>.toDot(label: (T) -> String = { "n_${it.hashCode()}" }): String {
    if (type.isDirected) {
        return "digraph {\n" + vertexSet().joinToString("\n") { vertex ->
            val internalName = "n_${vertex.hashCode()}"
            "$internalName [label=\"${label(vertex)}\"]"
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
            "$internalName [label=\"${label(vertex)}\"]"
        } + "\n" + edgeSet().joinToString("\n") { edge ->
            val source = getEdgeSource(edge.uncheckedCast())
            val target = getEdgeTarget(edge.uncheckedCast())
            val sourceName = "n_${source.hashCode()}"
            val targetName = "n_${target.hashCode()}"
            "$sourceName -- $targetName"
        } + "\n}"
    }
}
