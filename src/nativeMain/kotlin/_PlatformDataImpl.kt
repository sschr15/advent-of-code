@file:OptIn(ExperimentalForeignApi::class)

package sschr15.aocsolutions

import kotlinx.cinterop.*
import platform.posix.fileno
import platform.posix.fopen
import platform.posix.fread

internal val props = mutableMapOf<String, String>()

actual fun getProperty(prop: String) = props[prop]
actual fun setProperty(prop: String, value: String) {
    props[prop] = value
}

actual fun fileExists(location: String): Boolean {
    return fopen(location, "r")?.useFile { file ->
        fileno(file) != -1
    } ?: false
}

actual fun readFile(location: String): String {
    return fopen(location, "r")?.useFile { file ->
        val buffer = ByteArray(4096)
        val sb = StringBuilder()
        while (true) {
            val read = fread(buffer.refTo(0), 1u, buffer.size.toULong(), file)
            if (read == 0UL) break
            sb.append(buffer.decodeToString(0, read.toInt()))
        }
        sb.toString()
    } ?: throw IllegalArgumentException("File $location does not exist")
}
