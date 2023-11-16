@file:OptIn(ExperimentalForeignApi::class, ExperimentalStdlibApi::class)

package sschr15.aocsolutions

import kotlinx.cinterop.*
import platform.posix.memcpy
import platform.windows.*

private fun BOOL.orThrow(desc: String) {
    if (this != 0) return // succeeded

    val errorCode = GetLastError()
    val errorHex = errorCode.toHexString()
    error("Error $errorCode / $errorHex while $desc")
}

private fun definitelyThrow(desc: String): Nothing {
    val errorCode = GetLastError()
    val errorHex = errorCode.toHexString()
    error("Error $errorCode / $errorHex while $desc")
}

actual fun setClipboard(text: String) {
    OpenClipboard(null).orThrow("opening clipboard") // "ask windows for the clipboard"
    EmptyClipboard().orThrow("emptying clipboard") // "tell windows to actually give us the clipboard"
    val hGlobal = GlobalAlloc(GMEM_MOVEABLE.toUInt(), (text.length.toULong() + 1u) * 2u)
        ?: definitelyThrow("allocating memory") // "get a place to put the text"
    val lpGlobal = GlobalLock(hGlobal) ?: definitelyThrow("locking memory")
    memScoped {
        memcpy(lpGlobal, text.wcstr, text.length.toULong() * 2u) // "copy the text"
    }
    GlobalUnlock(hGlobal).inv().orThrow("unlocking memory") // inv() because *this returns false on success*
    // why do you do this, windows?
    SetClipboardData(CF_UNICODETEXT.toUInt(), hGlobal) ?: definitelyThrow("setting clipboard data") // "put the text in the clipboard"
    CloseClipboard() // "tell windows we're done with the clipboard"
}
