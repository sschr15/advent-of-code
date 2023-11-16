package sschr15.aocsolutions

import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.readText

actual fun getProperty(prop: String): String? = System.getProperty(prop)
actual fun setProperty(prop: String, value: String) {
    System.setProperty(prop, value)
}

actual fun setClipboard(text: String) {
    val clipboard = Toolkit.getDefaultToolkit().systemClipboard
    val selection = StringSelection(text)
    clipboard.setContents(selection, selection)
}

actual fun fileExists(location: String) = Path(location).exists()
actual fun readFile(location: String) = Path(location).readText()
