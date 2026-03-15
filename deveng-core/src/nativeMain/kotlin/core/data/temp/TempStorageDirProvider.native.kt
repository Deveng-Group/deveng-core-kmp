package core.data.temp

import platform.Foundation.NSTemporaryDirectory

/**
 * iOS: uses NSTemporaryDirectory() + [subdir] (default "temp_storage") so temp files survive until app is cleared.
 */
class IosTempStorageDirProvider(
    private val subdir: String = "temp_storage"
) : TempStorageDirProvider {

    override fun getPath(): String {
        val tempDir = NSTemporaryDirectory()
        val base = tempDir.trimEnd('/')
        return "$base/$subdir"
    }
}
