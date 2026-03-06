package core.util.image

actual object PhotoSaveUtils {

    actual fun setApplicationContext(context: Any?) {}

    actual fun savePhoto(imageBytes: ByteArray, targetPath: String): SavePhotoResult =
        SavePhotoResult.Error(UnsupportedOperationException("savePhoto not supported on WASM/JS"))

    actual fun addLocationExif(
        imageBytes: ByteArray,
        latitude: Double,
        longitude: Double,
    ): ByteArray = imageBytes
}
