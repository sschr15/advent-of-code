package sschr15.aocsolutions.util

import io.ktor.client.engine.*
import io.ktor.client.engine.curl.*
import kotlinx.cinterop.*
import platform.posix.*

actual val httpEngine: HttpClientEngineFactory<*> = Curl

actual fun copyToClipboard(text: String) {
    //TODO
}

actual fun truthinessOf(any: Any) = true

internal class LinuxPath(path: String) : Path {
    private val path = path.trimEnd('/')

    private fun mkdir() {
        if (path.isEmpty()) return
        if (exists()) return
        mkdir(path, mask)
    }

    override fun readText(): String = memScoped {
        val file = fopen(path, "r")
            ?: error("File could not be opened")
        try {
            val bufferLength = 64 * 1024
            val buffer = allocArray<ByteVar>(bufferLength)
            return buildString {
                while (true) {
                    val nextLine = fgets(buffer, bufferLength, file)?.toKString() ?: break
                    append(nextLine)
                }
                if (feof(file) != 0) error("File unexpectedly ended")
            }
        } finally {
            fclose(file)
        }
    }

    override fun writeText(text: String) {
        val file = fopen(path, "w")
            ?: error("File could not be opened")

        try {
            memScoped {
                if (fputs(text, file) == EOF) error("File could not be written")
            }
        } finally {
            fclose(file)
        }
    }

    override fun createDirectories() {
        if (!parent.exists()) parent.createDirectories()
        mkdir()
    }

    override fun exists() = access(path, F_OK) == 0

    override val parent: Path get() = dirname(path.cstr)?.toKString()?.let(::Path)
        ?: error("Parent could not be determined")

    companion object {
        val mask = "0777".toUInt(8)
    }
}

actual interface Path {
    fun readText(): String
    fun writeText(text: String)
    fun createDirectories()
    fun exists(): Boolean
    val parent: Path
}

actual fun Path(path: String): Path = LinuxPath(path)

// this may look recursive, but member functions are preferred over extension functions
actual fun Path.readText(): String = readText()
actual fun Path.writeText(text: String) = writeText(text)
actual fun Path.createDirectories() = createDirectories()
actual fun Path.exists() = exists()
actual val Path.parent: Path get() = parent
