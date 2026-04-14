package core.util.image

/**
 * Result of saving a photo to disk.
 * @property path Absolute path of the saved file on success.
 */
sealed class SavePhotoResult {
    data class Success(val path: String) : SavePhotoResult()
    data class Error(val exception: Throwable) : SavePhotoResult()
}

/**
 * Platform-specific utilities for saving photo bytes to disk and adding location EXIF.
 * Use after capturing: capture → addLocationExif(bytes, lat, lon) → savePhoto(bytes, path).
 */
expect object PhotoSaveUtils {

    /**
     * Optional: call once with the application context so that on Android the saved photo
     * is notified to MediaStore and appears in the system Gallery. No-op on other platforms.
     */
    fun setApplicationContext(context: Any?)

    /**
     * Writes [imageBytes] to [targetPath]. Creates parent directories if needed.
     * On Android, if [setApplicationContext] was called, notifies MediaStore so the photo appears in the Gallery.
     *
     * @param imageBytes JPEG or PNG image bytes (e.g. from [ImageCaptureResult.Success.byteArray]).
     * @param targetPath Absolute path where the file should be written.
     * @return [SavePhotoResult.Success] with the path, or [SavePhotoResult.Error] on failure.
     */
    fun savePhoto(imageBytes: ByteArray, targetPath: String): SavePhotoResult

    /**
     * Returns image bytes with orientation normalized: EXIF orientation is applied to pixels
     * and the result has orientation = normal. Use before saving so the saved file displays
     * correctly in all viewers (e.g. when camera returns EXIF 6 = 90° but pixels are not rotated).
     * On unsupported platforms or on error, returns [imageBytes] unchanged.
     */
    fun imageBytesWithNormalOrientation(imageBytes: ByteArray): ByteArray

    /**
     * Returns a copy of [imageBytes] with GPS location EXIF tags set.
     * Preserves existing EXIF (e.g. orientation). Best used with JPEG bytes.
     *
     * @param imageBytes JPEG (or PNG) image bytes.
     * @param latitude Latitude in decimal degrees (e.g. 41.0082).
     * @param longitude Longitude in decimal degrees (e.g. 28.9784).
     * @return New byte array with location EXIF added; or [imageBytes] unchanged on unsupported platform or error.
     */
    fun addLocationExif(
        imageBytes: ByteArray,
        latitude: Double,
        longitude: Double,
    ): ByteArray

    /**
     * Reads GPS latitude/longitude from JPEG (or compatible) [imageBytes] if present.
     * Use for upload metadata: prefer capture-time location embedded with [addLocationExif]
     * instead of a fresh GPS fix at swipe/upload time.
     *
     * @return `(latitude, longitude)` in decimal degrees, or `null` if missing or unsupported.
     */
    fun readLocationFromExif(imageBytes: ByteArray): Pair<Double, Double>?
}
