package sschr15.aocsolutions.util

import java.io.ByteArrayOutputStream
import java.io.PrintStream

class ThreadLocalPrintStream : PrintStream(ByteArrayOutputStream(0)) {
    private val stream = ThreadLocal.withInitial { ByteArrayOutputStream() }

    var out: ByteArrayOutputStream
        get() = stream.get()
        set(value) {
            stream.set(value)
        }

    override fun write(b: Int) = stream.get().write(b)
    override fun write(b: ByteArray, off: Int, len: Int) = stream.get().write(b, off, len)
    override fun write(b: ByteArray) = stream.get().write(b)

    override fun flush() = stream.get().flush()
    override fun close() = stream.get().close()
}