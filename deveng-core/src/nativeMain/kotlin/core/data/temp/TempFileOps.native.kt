package core.data.temp

import core.domain.camera.utils.toByteArray
import core.domain.camera.utils.toNSData
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSData
import platform.Foundation.NSFileManager

@OptIn(ExperimentalForeignApi::class)
actual class TempFileOps actual constructor() {

    private val fileManager = NSFileManager.defaultManager

    actual fun ensureDir(path: String) {
        if (!fileManager.fileExistsAtPath(path)) {
            fileManager.createDirectoryAtPath(
                path,
                withIntermediateDirectories = true,
                attributes = null,
                error = null,
            )
        }
    }

    actual fun writeFile(path: String, bytes: ByteArray) {
        val nsData: NSData = bytes.toNSData()
        fileManager.createFileAtPath(path, nsData, null)
    }

    actual fun readFile(path: String): ByteArray? {
        val data = fileManager.contentsAtPath(path) ?: return null
        return data.toByteArray()
    }

    actual fun deleteFile(path: String) {
        fileManager.removeItemAtPath(path, null)
    }

    actual fun fileExists(path: String): Boolean {
        return fileManager.fileExistsAtPath(path)
    }
}
