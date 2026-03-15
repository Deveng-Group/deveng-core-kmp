package core.data.temp

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.createDirectoryAtPath
import platform.Foundation.dataWithContentsOfURL
import platform.Foundation.fileExistsAtPath
import platform.Foundation.removeItemAtPath
import platform.Foundation.writeToURL
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class)
actual class TempFileOps actual constructor() {

    private val fileManager = NSFileManager.defaultManager

    actual fun ensureDir(path: String) {
        if (!fileManager.fileExistsAtPath(path)) {
            fileManager.createDirectoryAtPath(path, true, null, null)
        }
    }

    actual fun writeFile(path: String, bytes: ByteArray) {
        bytes.usePinned { pinned ->
            val data = NSData.dataWithBytes(pinned.addressOf(0), bytes.size.toULong())
            val url = NSURL.fileURLWithPath(path)
            data?.writeToURL(url, true)
        }
    }

    actual fun readFile(path: String): ByteArray? {
        val url = NSURL.fileURLWithPath(path)
        val data = NSData.dataWithContentsOfURL(url) ?: return null
        val size = data.length.toInt()
        return ByteArray(size).apply {
            usePinned { pinned ->
                memcpy(pinned.addressOf(0), data.bytes!!, size.toLong())
            }
        }
    }

    actual fun deleteFile(path: String) {
        fileManager.removeItemAtPath(path, null)
    }

    actual fun fileExists(path: String): Boolean {
        return fileManager.fileExistsAtPath(path)
    }
}
