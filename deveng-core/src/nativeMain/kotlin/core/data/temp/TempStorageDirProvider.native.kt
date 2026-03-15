package core.data.temp

import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.URLByAppendingPathComponent

/**
 * iOS: uses NSTemporaryDirectory() + [subdir] (default "temp_storage") so temp files survive until app is cleared.
 */
class IosTempStorageDirProvider(
    private val subdir: String = "temp_storage"
) : TempStorageDirProvider {

    override fun getPath(): String {
        val tempDir = NSTemporaryDirectory()
        val url = NSURL.fileURLWithPath(tempDir).URLByAppendingPathComponent(subdir)!!
        return url.path!!
    }
}
