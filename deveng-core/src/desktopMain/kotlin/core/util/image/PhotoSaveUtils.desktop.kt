package core.util.image

import java.io.File

actual object PhotoSaveUtils {

    actual fun setApplicationContext(context: Any?) {}

    actual fun savePhoto(imageBytes: ByteArray, targetPath: String): SavePhotoResult = try {
        val file = File(targetPath)
        file.parentFile?.mkdirs()
        file.writeBytes(imageBytes)
        SavePhotoResult.Success(targetPath)
    } catch (e: Exception) {
        SavePhotoResult.Error(e)
    }

    actual fun addLocationExif(
        imageBytes: ByteArray,
        latitude: Double,
        longitude: Double,
    ): ByteArray {
        // TODO: Add EXIF GPS on Desktop if needed (e.g. via metadata library)
        return imageBytes
    }
}
