package core.data.temp

/**
 * WASM/JS: temp file ops not supported; operations throw.
 */
actual class TempFileOps actual constructor() {

    actual fun ensureDir(path: String) {
        throw UnsupportedOperationException("Temp file storage not supported on WASM")
    }

    actual fun writeFile(path: String, bytes: ByteArray) {
        throw UnsupportedOperationException("Temp file storage not supported on WASM")
    }

    actual fun readFile(path: String): ByteArray? {
        throw UnsupportedOperationException("Temp file storage not supported on WASM")
    }

    actual fun deleteFile(path: String) {
        throw UnsupportedOperationException("Temp file storage not supported on WASM")
    }

    actual fun fileExists(path: String): Boolean {
        return false
    }
}
