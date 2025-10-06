package sschr15.aocsolutions.util.graphs

expect fun <N : Any> Graph<N>.toDot(labelFunction: (N) -> String): String

fun <N : Any> Graph<N>.toDot() = toDot { "n_${it.hashCode()}" }
