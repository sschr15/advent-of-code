package sschr15.aocsolutions.util

import io.ktor.client.engine.HttpClientEngineFactory

expect val httpEngine: HttpClientEngineFactory<*>

expect fun copyToClipboard(text: String)
expect fun truthinessOf(any: Any): Boolean

expect interface Path
expect fun Path(path: String): Path
expect fun Path.readText(): String
expect fun Path.writeText(text: String)
expect fun Path.createDirectories()
expect fun Path.exists(): Boolean
expect val Path.parent: Path
