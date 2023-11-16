package sschr15.aocsolutions

expect fun getProperty(prop: String): String?
expect fun setProperty(prop: String, value: String)

expect fun setClipboard(text: String)

expect fun fileExists(location: String): Boolean
expect fun readFile(location: String): String
