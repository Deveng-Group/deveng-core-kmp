package core.domain.camera.controller

import core.domain.camera.enums.CameraDeviceType
import core.domain.camera.enums.CameraLens
import core.domain.camera.enums.FlashMode
import core.domain.camera.enums.ImageFormat
import core.domain.camera.enums.QualityPrioritization
import core.domain.camera.enums.TorchMode
import core.domain.camera.result.ImageCaptureResult
import core.domain.camera.video.VideoCaptureResult
import core.domain.camera.video.VideoConfiguration
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * WASM/JS noop: CameraController is not supported; all operations are no-op or return error.
 */
actual class CameraController {

    actual var onPreviewTapListener: ((Float, Float) -> Unit)? = null
    actual var onPreviewDoubleTapListener: (() -> Unit)? = null

    actual suspend fun takePicture(): ImageCaptureResult =
        ImageCaptureResult.Error(UnsupportedOperationException("Camera not supported on WASM"))

    actual suspend fun takePictureToFile(): ImageCaptureResult =
        ImageCaptureResult.Error(UnsupportedOperationException("Camera not supported on WASM"))

    actual fun toggleFlashMode() {}
    actual fun setFlashMode(mode: FlashMode) {}
    actual fun getFlashMode(): FlashMode? = null
    actual fun toggleTorchMode() {}
    actual fun setTorchMode(mode: TorchMode) {}
    actual fun getTorchMode(): TorchMode? = null
    actual fun toggleCameraLens() {}
    actual fun getCameraLens(): CameraLens? = null
    actual fun getImageFormat(): ImageFormat = ImageFormat.JPEG
    actual fun getQualityPrioritization(): QualityPrioritization = QualityPrioritization.QUALITY
    actual fun getPreferredCameraDeviceType(): CameraDeviceType = CameraDeviceType.DEFAULT
    actual fun setPreferredCameraDeviceType(deviceType: CameraDeviceType) {}
    actual fun setZoom(zoomRatio: Float) {}
    actual fun getZoom(): Float = 1f
    actual fun getMaxZoom(): Float = 1f
    actual fun setFocusPoint(normalizedX: Float, normalizedY: Float) {}
    actual fun getExposureCompensationRange(): Pair<Int, Int> = Pair(0, 0)
    actual fun setExposureCompensationIndex(index: Int) {}
    actual fun getExposureCompensationIndex(): Int = 0
    actual fun startSession() {}
    actual fun stopSession() {}
    actual fun addImageCaptureListener(listener: (ByteArray) -> Unit) {}
    actual fun toggleNightMode() {}
    actual fun setNightMode(enabled: Boolean) {}
    actual fun isNightModeEnabled(): Boolean = false
    actual fun setWideSelfieMode(enabled: Boolean) {}
    actual fun isWideSelfieEnabled(): Boolean = false
    actual fun initializeControllerPlugins() {}
    actual fun cleanup() {}

    actual suspend fun startRecording(configuration: VideoConfiguration): String =
        suspendCancellableCoroutine { cont ->
            cont.resume("")
        }

    actual suspend fun stopRecording(): VideoCaptureResult =
        VideoCaptureResult.Error(UnsupportedOperationException("Camera not supported on WASM"))

    actual suspend fun pauseRecording() {}
    actual suspend fun resumeRecording() {}
}
