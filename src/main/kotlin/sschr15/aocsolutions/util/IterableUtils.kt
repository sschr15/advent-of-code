package sschr15.aocsolutions.util

/**
 * A [Map] which returns non-null values for missing keys.
 */
class SolidMap<K, V>(private val map: Map<K, V>, private val default: (K) -> V) : Map<K, V> by map {
    override operator fun get(key: K) = map[key] ?: default(key)
    override fun getOrDefault(key: K, defaultValue: V) = map.getOrDefault(key, defaultValue)
}

fun <K, V> Map<K, V>.solid(default: V): SolidMap<K, V> = SolidMap(this) { default }
fun <K, V> Map<K, V>.solid(default: (K) -> V): SolidMap<K, V> = SolidMap(this, default)

fun <K, V> Map<K, MutableList<V>>.removeAll(value: V) {
    this.forEach { (_, list) ->
        list.remove(value)
    }
}

fun <T> Iterable<T>.containsAll(other: Iterable<T>): Boolean {
    return other.all { this.contains(it) }
}

fun <T> Iterable<T>.containsAll(vararg other: T): Boolean {
    return other.all { this.contains(it) }
}

fun MutableList<in Char>.addAll(other: CharSequence) {
    other.forEach { this.add(it) }
}

fun <K, V> Map<K, MutableList<V>>.addToAll(value: V, vararg keys: K) {
    keys.forEach { this[it]?.add(value) }
}

fun <K> Map<K, MutableList<in Char>>.addAllToAll(other: CharSequence, vararg keys: K) {
    keys.forEach { this[it]?.addAll(other) }
}

fun <T> List<T>.only(vararg indices: Int) = indices.map { this[it] }

fun <T> Array<T>.only(vararg indices: Int) = indices.map { this[it] }
fun CharArray.only(vararg indices: Int) = indices.map { this[it] }
fun ByteArray.only(vararg indices: Int) = indices.map { this[it] }
fun ShortArray.only(vararg indices: Int) = indices.map { this[it] }
fun IntArray.only(vararg indices: Int) = indices.map { this[it] }
fun LongArray.only(vararg indices: Int) = indices.map { this[it] }
fun FloatArray.only(vararg indices: Int) = indices.map { this[it] }
fun DoubleArray.only(vararg indices: Int) = indices.map { this[it] }
