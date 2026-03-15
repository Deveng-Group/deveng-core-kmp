package core.util.image

import core.domain.camera.utils.toNSData
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Photos.PHAssetChangeRequest
import platform.Photos.PHPhotoLibrary

@OptIn(ExperimentalForeignApi::class)
actual object PhotoSaveUtils {

    actual fun setApplicationContext(context: Any?) {}

    actual fun imageBytesWithNormalOrientation(imageBytes: ByteArray): ByteArray = imageBytes

    actual fun savePhoto(imageBytes: ByteArray, targetPath: String): SavePhotoResult = try {
        val nsData = imageBytes.toNSData()
        val parentPath = targetPath.substringBeforeLast("/", "")
        if (parentPath.isNotEmpty()) {
            NSFileManager.defaultManager.createDirectoryAtPath(
                parentPath,
                withIntermediateDirectories = true,
                attributes = null,
                error = null,
            )
        }
        val success = NSFileManager.defaultManager.createFileAtPath(targetPath, nsData, null)
        if (!success) {
            return SavePhotoResult.Error(Exception("createFileAtPath returned false"))
        }
        // Add to Photos library so the photo appears in the gallery (like CameraK / videos)
        saveToPhotosLibrary(targetPath)
        SavePhotoResult.Success(targetPath)
    } catch (e: Exception) {
        SavePhotoResult.Error(e)
    }

    /**
     * Saves the image file at [filePath] to the Photos library so it appears in the gallery.
     * Uses the same pattern as video saving in CameraController (PHPhotoLibrary.performChanges).
     */
    private fun saveToPhotosLibrary(filePath: String) {
        var saveError: String? = null
        val semaphore = platform.darwin.dispatch_semaphore_create(0)

        PHPhotoLibrary.sharedPhotoLibrary().performChanges(
            changeBlock = {
                PHAssetChangeRequest.creationRequestForAssetFromImageAtFileURL(
                    NSURL.fileURLWithPath(filePath),
                )
            },
            completionHandler = { success, error ->
                if (!success || error != null) {
                    saveError = error?.localizedDescription ?: "Failed to save to Photos"
                    platform.Foundation.NSLog("PhotoSaveUtils: Failed to save to Photos: $saveError")
                }
                platform.darwin.dispatch_semaphore_signal(semaphore)
            },
        )

        platform.darwin.dispatch_semaphore_wait(semaphore, platform.darwin.DISPATCH_TIME_FOREVER)
    }

    actual fun addLocationExif(
        imageBytes: ByteArray,
        latitude: Double,
        longitude: Double,
    ): ByteArray {
        // TODO: Add EXIF GPS to image bytes on iOS (e.g. via ImageIO)
        return imageBytes
    }
}
