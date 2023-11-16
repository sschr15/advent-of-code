package sschr15.aocsolutions

private const val CLIPBOARD_PREFIX = "\u001B]52;c;"
private const val CLIPBOARD_SUFFIX = "\u0007"

// using the terminal clipboard isn't the *best* solution, but i don't want to fight with x11, wayland, or other
// windowing systems that linux might use
actual fun setClipboard(text: String) {
    println("$CLIPBOARD_PREFIX$text$CLIPBOARD_SUFFIX")
}
