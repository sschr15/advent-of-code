package sschr15.aocsolutions.util

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.java.Java
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.io.path.writeText
import kotlin.io.path.exists
import kotlin.io.path.createDirectories

actual val httpEngine: HttpClientEngineFactory<*> = Java

actual fun copyToClipboard(text: String) {
    val clipboard = Toolkit.getDefaultToolkit().systemClipboard
    val selection = StringSelection(text)
    clipboard.setContents(selection, selection)
}

actual typealias Path = java.nio.file.Path

actual fun Path(path: String) = Path(path)
actual fun Path.readText() = readText()
actual fun Path.writeText(text: String) = writeText(text)
actual fun Path.exists() = exists()
actual val Path.parent get() = parent

actual fun Path.createDirectories() {
    createDirectories()
}

actual fun truthinessOf(any: Any) = when (any) {
    is File -> any.exists()
    else -> true
}

actual typealias PriorityQueue<E> = java.util.PriorityQueue<E & Any>
