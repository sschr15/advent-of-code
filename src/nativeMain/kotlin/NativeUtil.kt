@file:OptIn(ExperimentalForeignApi::class)

package sschr15.aocsolutions

import kotlinx.cinterop.*
import platform.posix.FILE
import platform.posix.fclose

inline fun <T : CPointer<FILE>?, R> T.useFile(block: (T) -> R) = try {
    block(this)
} finally {
    if (this != null) {
        fclose(this)
    }
}
