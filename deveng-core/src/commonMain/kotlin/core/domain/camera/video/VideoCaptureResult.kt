package core.domain.camera.video

import androidx.compose.runtime.Immutable

/**
 * Sealed class representing the result of a completed video recording.
 * Mirrors [core.domain.camera.result.ImageCaptureResult] in structure.
 */
@Immutable
sealed class VideoCaptureResult {

    /**
     * Recording completed successfully.
     *
     * @property filePath Absolute path to the saved video file.
     * @property durationMs Actual recorded duration in milliseconds.
     */
    @Immutable
    data class Success(val filePath: String, val durationMs: Long) : VideoCaptureResult()

    /**
     * Recording failed or was cancelled.
     *
     * @property exception The underlying cause.
     */
    @Immutable
    data class Error(val exception: Exception) : VideoCaptureResult()
}
