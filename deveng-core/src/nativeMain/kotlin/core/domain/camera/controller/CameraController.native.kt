package core.domain.camera.controller

import core.domain.camera.enums.AspectRatio
import core.domain.camera.enums.CameraDeviceType
import core.domain.camera.enums.CameraLens
import core.domain.camera.enums.Directory
import core.domain.camera.enums.FlashMode
import core.domain.camera.enums.ImageFormat
import core.domain.camera.enums.QualityPrioritization
import core.domain.camera.enums.TorchMode
import core.domain.camera.plugins.CameraPlugin
import core.domain.camera.result.ImageCaptureResult
import core.domain.camera.utils.MemoryManager
import core.util.bytearray.toImageBitmap
import core.domain.camera.utils.fixOrientation
import core.domain.camera.utils.toByteArray
import core.domain.camera.utils.toUIImage
import core.domain.camera.video.VideoCaptureResult
import core.domain.camera.video.VideoConfiguration
import core.domain.camera.video.VideoQuality
import kotlinx.atomicfu.atomic
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import kotlinx.cinterop.autoreleasepool
import kotlinx.cinterop.useContents
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.AVFoundation.AVCaptureDeviceInput
import platform.AVFoundation.AVCaptureFlashMode
import platform.AVFoundation.AVCaptureFlashModeAuto
import platform.AVFoundation.AVCaptureFlashModeOff
import platform.AVFoundation.AVCaptureFlashModeOn
import platform.AVFoundation.AVCaptureMetadataOutput
import platform.AVFoundation.AVCaptureMetadataOutputObjectsDelegateProtocol
import platform.AVFoundation.AVCaptureMovieFileOutput
import platform.AVFoundation.AVCaptureOutput
import platform.AVFoundation.AVCaptureSessionPreset1280x720
import platform.AVFoundation.AVCaptureSessionPreset1920x1080
import platform.AVFoundation.AVCaptureSessionPreset3840x2160
import platform.AVFoundation.AVCaptureSessionPreset640x480
import platform.AVFoundation.AVCaptureTorchMode
import platform.AVFoundation.AVCaptureTorchModeAuto
import platform.AVFoundation.AVCaptureTorchModeOff
import platform.AVFoundation.AVCaptureTorchModeOn
import platform.AVFoundation.AVCaptureVideoOrientation
import platform.AVFoundation.AVCaptureVideoOrientationLandscapeLeft
import platform.AVFoundation.AVCaptureVideoOrientationLandscapeRight
import platform.AVFoundation.AVCaptureVideoOrientationPortrait
import platform.AVFoundation.AVCaptureVideoOrientationPortraitUpsideDown
import platform.AVFoundation.AVMediaTypeAudio
import platform.Foundation.NSData
import platform.Foundation.NSDate
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.timeIntervalSince1970
import platform.Photos.PHAssetChangeRequest
import platform.Photos.PHPhotoLibrary
import platform.Foundation.NSSelectorFromString
import platform.UIKit.UIDevice
import platform.UIKit.UIDeviceOrientation
import platform.UIKit.UIScreen
import platform.UIKit.UIGestureRecognizerStateBegan
import platform.UIKit.UIGestureRecognizerStateChanged
import platform.UIKit.UIImagePNGRepresentation
import platform.UIKit.UIPinchGestureRecognizer
import platform.UIKit.UITapGestureRecognizer
import platform.UIKit.UIViewController
import platform.darwin.DISPATCH_QUEUE_PRIORITY_HIGH
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_global_queue
import platform.darwin.dispatch_get_main_queue
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.random.Random

actual class CameraController(
    internal var flashMode: FlashMode,
    internal var torchMode: TorchMode,
    internal var cameraLens: CameraLens,
    internal var imageFormat: ImageFormat,
    internal var qualityPriority: QualityPrioritization,
    internal var directory: Directory,
    internal var cameraDeviceType: CameraDeviceType,
    internal var aspectRatio: AspectRatio,
    internal var returnFilePath: Boolean,
    internal var plugins: MutableList<CameraPlugin>,
    internal var targetResolution: Pair<Int, Int>? = null,
) : UIViewController(null, null) {
    private var isCapturing = atomic(false)
    private val customCameraController = CustomCameraController(
        qualityPrioritization = qualityPriority,
        initialCameraLens = cameraLens,
        aspectRatio = aspectRatio,
        targetResolution = targetResolution,
    )
    private var imageCaptureListeners = mutableListOf<(ByteArray) -> Unit>()
    private var metadataOutput = AVCaptureMetadataOutput()
    private var metadataObjectsDelegate: AVCaptureMetadataOutputObjectsDelegateProtocol? = null

    // Video recording
    private var movieFileOutput: AVCaptureMovieFileOutput? = null
    private var videoRecordingDelegate: VideoRecordingDelegate? = null
    private var videoOutputFilePath: String? = null

    actual var onPreviewTapListener: ((Float, Float) -> Unit)? = null
    actual var onPreviewDoubleTapListener: (() -> Unit)? = null

    private val memoryManager = MemoryManager
    private val pendingCaptures = atomic(0)
    private val maxConcurrentCaptures = 3

    override fun viewDidLoad() {
        super.viewDidLoad()

        memoryManager.initialize()
        setupCamera()
        setupNativeGestureRecognizers()
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun setupNativeGestureRecognizers() {
        val singleTap = UITapGestureRecognizer(
            target = this,
            action = NSSelectorFromString("handleNativeSingleTap:"),
        )
        singleTap.numberOfTapsRequired = 1u

        val doubleTap = UITapGestureRecognizer(
            target = this,
            action = NSSelectorFromString("handleNativeDoubleTap:"),
        )
        doubleTap.numberOfTapsRequired = 2u

        val pinch = UIPinchGestureRecognizer(
            target = this,
            action = NSSelectorFromString("handleNativePinch:"),
        )

        view.addGestureRecognizer(singleTap)
        view.addGestureRecognizer(doubleTap)
        view.addGestureRecognizer(pinch)
    }

    @ObjCAction
    @OptIn(ExperimentalForeignApi::class)
    fun handleNativeSingleTap(sender: UITapGestureRecognizer) {
        val viewWidth = view.bounds.useContents { size.width }
        val viewHeight = view.bounds.useContents { size.height }
        if (viewWidth <= 0.0 || viewHeight <= 0.0) return
        val location = sender.locationInView(view)
        val nx = (location.useContents { x } / viewWidth).toFloat().coerceIn(0f, 1f)
        val ny = (location.useContents { y } / viewHeight).toFloat().coerceIn(0f, 1f)
        println("[CameraFocus] Native handleSingleTap: nx=$nx ny=$ny")
        setFocusPoint(nx, ny)
        onPreviewTapListener?.invoke(nx, ny)
    }

    @ObjCAction
    fun handleNativeDoubleTap(sender: UITapGestureRecognizer) {
        println("[CameraFocus] Native handleDoubleTap")
        onPreviewDoubleTapListener?.invoke()
    }

    @ObjCAction
    @OptIn(ExperimentalForeignApi::class)
    fun handleNativePinch(sender: UIPinchGestureRecognizer) {
        val state = sender.state
        if (state != UIGestureRecognizerStateBegan && state != UIGestureRecognizerStateChanged) return
        val scale = sender.scale.toFloat()
        sender.setScale(1.0)
        val currentZoom = getZoom()
        val maxZoom = getMaxZoom().coerceAtLeast(1f)
        val newZoom = (currentZoom * scale).coerceIn(1f, maxZoom)
        setZoom(newZoom)
    }

    override fun viewWillAppear(animated: Boolean) {
        super.viewWillAppear(animated)
        memoryManager.updateMemoryStatus()
    }

    override fun viewDidDisappear(animated: Boolean) {
        super.viewDidDisappear(animated)

        memoryManager.clearBufferPools()
    }

    fun getCameraPreviewLayer() = customCameraController.cameraPreviewLayer

    /**
     * Returns whether the capture session is ready for use.
     * Used by plugins to check if they can add outputs.
     */
    fun isSessionReady(): Boolean = customCameraController.captureSession != null

    /**
     * Queues a configuration change to be applied atomically (plugin outputs, etc).
     * Used by plugins (OCR, QRScanner) to safely add outputs without crashes.
     */
    fun queueConfigurationChange(change: () -> Unit) {
        customCameraController.queueConfigurationChange(change)
    }

    /**
     * Safely adds an output within the queued configuration block.
     */
    fun safeAddOutput(output: AVCaptureOutput) {
        customCameraController.safeAddOutput(output)
    }

    internal fun currentVideoOrientation(): AVCaptureVideoOrientation {
        val orientation = UIDevice.currentDevice.orientation
        return when (orientation) {
            UIDeviceOrientation.UIDeviceOrientationPortrait -> AVCaptureVideoOrientationPortrait
            UIDeviceOrientation.UIDeviceOrientationPortraitUpsideDown -> AVCaptureVideoOrientationPortraitUpsideDown
            UIDeviceOrientation.UIDeviceOrientationLandscapeLeft -> AVCaptureVideoOrientationLandscapeRight
            UIDeviceOrientation.UIDeviceOrientationLandscapeRight -> AVCaptureVideoOrientationLandscapeLeft
            else -> AVCaptureVideoOrientationPortrait
        }
    }

    private fun setupCamera() {
        customCameraController.onSessionReady = {
            try {
                customCameraController.setupPreviewLayer(view)
            } catch (e: Exception) {
                platform.Foundation.NSLog("CameraK Error: setupPreviewLayer - ${e.message}")
            }

            try {
                if (customCameraController.captureSession?.canAddOutput(metadataOutput) == true) {
                    customCameraController.captureSession?.addOutput(metadataOutput)
                }
            } catch (e: Exception) {
                platform.Foundation.NSLog("CameraK Error: metadata output - ${e.message}")
            }

            // Add movie file output for video recording
            try {
                val movieOutput = AVCaptureMovieFileOutput()
                if (customCameraController.captureSession?.canAddOutput(movieOutput) == true) {
                    customCameraController.captureSession?.addOutput(movieOutput)
                    movieFileOutput = movieOutput
                }
            } catch (e: Exception) {
                platform.Foundation.NSLog("CameraK Error: movie output - ${e.message}")
            }

            // Pre-add audio input so recording starts without session reconfiguration stutter
            addAudioInputIfNeeded()

            startSession()
        }

        try {
            customCameraController.setupSession(cameraDeviceType)
        } catch (e: Exception) {
            platform.Foundation.NSLog("CameraK Error: setupSession - ${e.message}")
            return
        }

        customCameraController.onPhotoCapture = { image ->
            image?.let {
                processImageCapture(it)
            }
        }

        customCameraController.onError = { error ->
            platform.Foundation.NSLog("CameraK Error: ${error::class.simpleName} - ${error.message ?: "no message"}")
        }
    }

    private fun writeDataToFile(data: NSData, filePath: String): Boolean =
        NSFileManager.defaultManager.createFileAtPath(filePath, data, null)

    private fun processImageCapture(imageData: NSData) {
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH.toLong(), 0u)) {
            autoreleasepool {
                memoryManager.updateMemoryStatus()

                try {
                    val estimatedSize = imageData.length.toInt()
                    val buffer = if (estimatedSize > 0) {
                        memoryManager.getBuffer(estimatedSize)
                    } else {
                        ByteArray(imageData.length.toInt())
                    }

                    val data = imageData.toByteArray(reuseBuffer = buffer)

                    dispatch_async(dispatch_get_main_queue()) {
                        imageCaptureListeners.forEach { it(data) }
                    }

                    if (buffer.size >= estimatedSize) {
                        memoryManager.recycleBuffer(buffer)
                    }
                } catch (e: Exception) {
                    platform.Foundation.NSLog("CameraK: Error processing image data: ${e.message ?: "Unknown error"}")
                }
            }
        }
    }

    fun setMetadataObjectsDelegate(delegate: AVCaptureMetadataOutputObjectsDelegateProtocol) {
        metadataObjectsDelegate = delegate
        metadataOutput.setMetadataObjectsDelegate(delegate, dispatch_get_main_queue())
    }

    fun updateMetadataObjectTypes(newTypes: List<String>) {
        // Note: This is called from queueConfigurationChange, so session may be in config mode
        // Don't check isRunning() - just set the types
        if (metadataOutput.availableMetadataObjectTypes.isNotEmpty()) {
            // Filter to only supported types
            val supportedTypes = newTypes.filter { type ->
                metadataOutput.availableMetadataObjectTypes.contains(type)
            }
            metadataOutput.metadataObjectTypes = supportedTypes
        } else {
            // Metadata output not fully ready yet, try setting all requested types
            metadataOutput.metadataObjectTypes = newTypes
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        customCameraController.cameraPreviewLayer?.let { layer ->
            layer.setFrame(view.bounds)
            layer.contentsScale = UIScreen.mainScreen.scale
        }
    }

    @Deprecated(
        message = "Use takePictureToFile() instead for better performance",
        replaceWith = ReplaceWith("takePictureToFile()"),
        level = DeprecationLevel.WARNING,
    )
    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun takePicture(): ImageCaptureResult = suspendCancellableCoroutine { continuation ->
        // Atomic counter rejection pattern
        if (pendingCaptures.incrementAndGet() > maxConcurrentCaptures) {
            pendingCaptures.decrementAndGet()
            platform.Foundation.NSLog(
                "CameraK: Burst queue full, dropping frame (${pendingCaptures.value} in progress)",
            )
            continuation.resume(ImageCaptureResult.Error(Exception("Burst queue full, capture rejected")))
            return@suspendCancellableCoroutine
        }

        if (!isCapturing.compareAndSet(expect = false, update = true)) {
            pendingCaptures.decrementAndGet()
            continuation.resume(ImageCaptureResult.Error(Exception("Capture in progress")))
            return@suspendCancellableCoroutine
        }

        // Update memory status before capture
        memoryManager.updateMemoryStatus()

        val captureHandler = object {
            var completed = false

            fun process(image: NSData?, error: String?) {
                if (completed) return
                completed = true

                if (image != null) {
                    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH.toLong(), 0u)) {
                        try {
                            autoreleasepool {
                                // Constant quality (0.95 for JPEG) - no dynamic adjustment
                                val quality = 0.95

                                val result = if (returnFilePath) {
                                    // Same as byte-array path: produce bytes + bitmap; app saves via PhotoSaveUtils
                                    when (imageFormat) {
                                        ImageFormat.JPEG -> {
                                            image.toByteArray().let { bytes ->
                                                val bitmap = try { bytes.toImageBitmap() } catch (_: Exception) { null }
                                                ImageCaptureResult.Success(bytes, bitmap)
                                            }
                                        }
                                        ImageFormat.PNG -> {
                                            val uiImage = image.toUIImage()
                                            val orientedImage = uiImage.fixOrientation()
                                            UIImagePNGRepresentation(orientedImage)?.toByteArray()?.let { bytes ->
                                                val bitmap = try { bytes.toImageBitmap() } catch (_: Exception) { null }
                                                ImageCaptureResult.Success(bytes, bitmap)
                                            }
                                        }
                                        else -> null
                                    }
                                } else {

                                    when (imageFormat) {
                                        ImageFormat.JPEG -> {

                                            image.toByteArray().let { bytes ->
                                                val bitmap = try { bytes.toImageBitmap() } catch (_: Exception) { null }
                                                ImageCaptureResult.Success(bytes, bitmap)
                                            }
                                        }
                                        ImageFormat.PNG -> {
                                            // Convert to PNG - fix orientation
                                            val uiImage = image.toUIImage()
                                            val orientedImage = uiImage.fixOrientation()
                                            UIImagePNGRepresentation(orientedImage)?.toByteArray()?.let { bytes ->
                                                val bitmap = try { bytes.toImageBitmap() } catch (_: Exception) { null }
                                                ImageCaptureResult.Success(bytes, bitmap)
                                            }
                                        }
                                        else -> null
                                    }
                                }

                                dispatch_async(dispatch_get_main_queue()) {
                                    isCapturing.value = false
                                    pendingCaptures.decrementAndGet()
                                    continuation.resume(
                                        result ?: ImageCaptureResult.Error(Exception("Image processing failed")),
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            dispatch_async(dispatch_get_main_queue()) {
                                isCapturing.value = false
                                pendingCaptures.decrementAndGet()
                                continuation.resume(ImageCaptureResult.Error(e))
                            }
                        }
                    }
                } else {
                    isCapturing.value = false
                    pendingCaptures.decrementAndGet()
                    continuation.resume(ImageCaptureResult.Error(Exception(error ?: "Capture failed")))
                }
            }
        }

        customCameraController.onPhotoCapture = { image ->
            captureHandler.process(image, null)
        }

        customCameraController.onError = { error ->
            captureHandler.process(null, error.toString())
        }

        continuation.invokeOnCancellation {
            isCapturing.value = false
            pendingCaptures.decrementAndGet()
            captureHandler.process(null, "Capture cancelled")
        }

        // Trigger capture with constant quality (95)
        customCameraController.captureImage(0.95)
    }

    /**
     * Capture returns bytes + bitmap; app saves via [core.util.image.PhotoSaveUtils.savePhoto].
     */
    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    actual suspend fun takePictureToFile(): ImageCaptureResult = suspendCancellableCoroutine { continuation ->
        if (pendingCaptures.incrementAndGet() > maxConcurrentCaptures) {
            pendingCaptures.decrementAndGet()
            platform.Foundation.NSLog("CameraK: Burst queue full, dropping frame")
            continuation.resume(ImageCaptureResult.Error(Exception("Burst queue full, capture rejected")))
            return@suspendCancellableCoroutine
        }

        if (!isCapturing.compareAndSet(expect = false, update = true)) {
            pendingCaptures.decrementAndGet()
            continuation.resume(ImageCaptureResult.Error(Exception("Capture in progress")))
            return@suspendCancellableCoroutine
        }

        val captureHandler = object {
            var completed = false

            fun process(image: NSData?, error: String?) {
                if (completed) return
                completed = true

                if (image != null) {
                    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH.toLong(), 0u)) {
                        try {
                            autoreleasepool {
                                val result = try {
                                    when (imageFormat) {
                                        ImageFormat.JPEG -> {
                                            image.toByteArray().let { bytes ->
                                                val bitmap = try { bytes.toImageBitmap() } catch (_: Exception) { null }
                                                ImageCaptureResult.Success(bytes, bitmap)
                                            }
                                        }
                                        ImageFormat.PNG -> {
                                            val uiImage = image.toUIImage()
                                            val orientedImage = uiImage.fixOrientation()
                                            UIImagePNGRepresentation(orientedImage)?.toByteArray()?.let { bytes ->
                                                val bitmap = try { bytes.toImageBitmap() } catch (_: Exception) { null }
                                                ImageCaptureResult.Success(bytes, bitmap)
                                            }
                                        }
                                        else -> null
                                    }
                                } catch (e: Exception) {
                                    platform.Foundation.NSLog("CameraK: takePictureToFile error: ${e.message ?: "Unknown"}")
                                    ImageCaptureResult.Error(e)
                                }

                                dispatch_async(dispatch_get_main_queue()) {
                                    isCapturing.value = false
                                    pendingCaptures.decrementAndGet()
                                    continuation.resume(
                                        result ?: ImageCaptureResult.Error(Exception("Unsupported image format")),
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            dispatch_async(dispatch_get_main_queue()) {
                                isCapturing.value = false
                                pendingCaptures.decrementAndGet()
                                continuation.resume(ImageCaptureResult.Error(e))
                            }
                        }
                    }
                } else {
                    isCapturing.value = false
                    pendingCaptures.decrementAndGet()
                    continuation.resume(ImageCaptureResult.Error(Exception(error ?: "Capture failed")))
                }
            }
        }

        customCameraController.onPhotoCapture = { image ->
            captureHandler.process(image, null)
        }

        customCameraController.onError = { error ->
            captureHandler.process(null, error.toString())
        }

        continuation.invokeOnCancellation {
            isCapturing.value = false
            pendingCaptures.decrementAndGet()
            captureHandler.process(null, "Capture cancelled")
        }

        customCameraController.captureImage(0.95)
    }

    actual fun toggleFlashMode() {
        // Same as Android: only ON and OFF (no AUTO)
        flashMode = when (flashMode) {
            FlashMode.ON -> FlashMode.OFF
            else -> FlashMode.ON  // OFF or AUTO -> ON
        }
        customCameraController.setFlashMode(flashMode.toAVCaptureFlashMode())
        applyTorchForPreviewAfterFlashChange()
    }

    actual fun setFlashMode(mode: FlashMode) {
        flashMode = mode
        customCameraController.setFlashMode(mode.toAVCaptureFlashMode())
        applyTorchForPreviewAfterFlashChange()
    }

    actual fun getFlashMode(): FlashMode? {
        // Same as Android: only report ON or OFF (map AUTO to OFF on iOS)
        fun AVCaptureFlashMode.toCameraKFlashMode(): FlashMode? = when (this) {
            AVCaptureFlashModeOn -> FlashMode.ON
            AVCaptureFlashModeOff -> FlashMode.OFF
            AVCaptureFlashModeAuto -> FlashMode.OFF  // iOS treats AUTO as OFF for parity with Android
            else -> null
        }

        return customCameraController.flashMode.toCameraKFlashMode()
    }

    actual fun toggleTorchMode() {
        torchMode = when (torchMode) {
            TorchMode.OFF -> TorchMode.ON
            TorchMode.ON -> TorchMode.AUTO
            TorchMode.AUTO -> TorchMode.OFF
        }
        customCameraController.setTorchMode(torchMode.toAVCaptureTorchMode())
    }

    actual fun setTorchMode(mode: TorchMode) {
        torchMode = mode
        customCameraController.setTorchMode(mode.toAVCaptureTorchMode())
    }

    actual fun getTorchMode(): TorchMode? = torchMode

    actual fun setZoom(zoomRatio: Float) {
        customCameraController.setZoom(zoomRatio)
    }

    actual fun getZoom(): Float = customCameraController.getZoom()

    actual fun getMaxZoom(): Float = customCameraController.getMaxZoom()

    actual fun setFocusPoint(normalizedX: Float, normalizedY: Float) {
        customCameraController.setFocusPoint(normalizedX, normalizedY)
    }

    actual fun getExposureCompensationRange(): Pair<Int, Int> =
        customCameraController.getExposureCompensationRange()

    actual fun setExposureCompensationIndex(index: Int) {
        customCameraController.setExposureTargetBias(index)
    }

    actual fun getExposureCompensationIndex(): Int =
        customCameraController.getExposureTargetBias()

    actual fun toggleCameraLens() {
        memoryManager.updateMemoryStatus()

        if (memoryManager.isUnderMemoryPressure()) {
            memoryManager.clearBufferPools()
        }

        customCameraController.switchCamera()
        // Sync with actual state (controller may have restored previous lens or succeeded on retry)
        cameraLens = customCameraController.getCurrentLens()
    }

    actual fun getCameraLens(): CameraLens? = customCameraController.getCurrentLens()

    actual fun getImageFormat(): ImageFormat = imageFormat

    actual fun getQualityPrioritization(): QualityPrioritization = qualityPriority

    actual fun getPreferredCameraDeviceType(): CameraDeviceType = cameraDeviceType

    actual fun setPreferredCameraDeviceType(deviceType: CameraDeviceType) {
        cameraDeviceType = deviceType
        customCameraController.switchToDeviceType(deviceType)
    }

    actual fun startSession() {
        // Note: The actual session start happens in onSessionReady callback (setupCamera).
        // This method is called from CameraKStateHolder.initialize() which may be before
        // viewDidLoad() triggers setupCamera(). The session will start automatically
        // when ready, so we only need to handle the case where session IS ready.
        if (customCameraController.captureSession != null) {
            memoryManager.clearBufferPools()
            customCameraController.startSession()
            initializeControllerPlugins()
        }
        // If captureSession is null, onSessionReady will call startSession() when ready
    }

    actual fun stopSession() {
        customCameraController.stopSession()
    }

    actual fun addImageCaptureListener(listener: (ByteArray) -> Unit) {
        imageCaptureListeners.add(listener)
    }

    private fun applyTorchForPreviewAfterFlashChange() {
        torchMode = if (flashMode == FlashMode.ON) TorchMode.ON else TorchMode.OFF
        customCameraController.setTorchMode(torchMode.toAVCaptureTorchMode())
    }

    actual fun setWideSelfieMode(enabled: Boolean) {
        // iOS: no vendor-specific FOV expansion equivalent. AVFoundation already exposes
        // ultra-wide via setPreferredCameraDeviceType(ULTRA_WIDE) on supported devices.
    }

    actual fun isWideSelfieEnabled(): Boolean = false

    actual fun initializeControllerPlugins() {
        plugins.forEach {
            it.initialize(this)
        }
    }

    actual fun cleanup() {
        movieFileOutput = null
        videoRecordingDelegate = null
        customCameraController.cleanupSession()
        memoryManager.clearBufferPools()
    }


    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun startRecording(configuration: VideoConfiguration): String = suspendCancellableCoroutine { cont ->
        val output = movieFileOutput ?: run {
            cont.resumeWithException(IllegalStateException("Movie file output not available"))
            return@suspendCancellableCoroutine
        }

        if (output.isRecording()) {
            cont.resume(videoOutputFilePath ?: "")
            return@suspendCancellableCoroutine
        }

        // Audio input is pre-added at session setup to avoid stutter
        if (configuration.enableAudio) {
            addAudioInputIfNeeded()
        }

        val filePath = createVideoTempFile(configuration)
        videoOutputFilePath = filePath
        val fileURL = NSURL.fileURLWithPath(filePath)

        // Delete existing file at path if any
        NSFileManager.defaultManager.removeItemAtPath(filePath, null)

        val delegate = VideoRecordingDelegate()
        videoRecordingDelegate = delegate

        // Set video orientation on the movie file output connection
        output.connectionWithMediaType(platform.AVFoundation.AVMediaTypeVideo)?.let { connection ->
            if (connection.isVideoOrientationSupported()) {
                connection.videoOrientation = currentVideoOrientation()
            }
        }

        dispatch_async(dispatch_get_main_queue()) {
            output.startRecordingToOutputFileURL(fileURL, recordingDelegate = delegate)
        }

        cont.resume(filePath)
    }

    actual suspend fun stopRecording(): VideoCaptureResult = suspendCancellableCoroutine { cont ->
        val output = movieFileOutput
        val delegate = videoRecordingDelegate

        if (output == null || delegate == null || !output.isRecording()) {
            cont.resume(VideoCaptureResult.Error(Exception("No active recording")))
            return@suspendCancellableCoroutine
        }

        delegate.onFinished = { result ->
            // Return raw result; app saves to gallery in callback via VideoSaveUtils (same as photos)
            cont.resume(result)
        }

        dispatch_async(dispatch_get_main_queue()) {
            output.stopRecording()
        }
    }

    actual suspend fun pauseRecording() {
        movieFileOutput?.pauseRecording()
    }

    actual suspend fun resumeRecording() {
        movieFileOutput?.resumeRecording()
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun addAudioInputIfNeeded() {
        val session = customCameraController.captureSession ?: return
        // Check if audio input already exists
        val hasAudioInput = session.inputs.any { input ->
            val deviceInput = input as? AVCaptureDeviceInput
            deviceInput?.device?.hasMediaType(AVMediaTypeAudio) == true
        }
        if (hasAudioInput) return

        val audioDevice = platform.AVFoundation.AVCaptureDevice.defaultDeviceWithMediaType(AVMediaTypeAudio)
            ?: return
        val audioInput = AVCaptureDeviceInput.deviceInputWithDevice(audioDevice, null) ?: return

        customCameraController.queueConfigurationChange {
            if (session.canAddInput(audioInput)) {
                session.addInput(audioInput)
            }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun createVideoTempFile(config: VideoConfiguration): String {
        val timestamp = NSDate().timeIntervalSince1970.toLong()
        val fileName = "${config.filePrefix}_$timestamp.mp4"

        return if (config.outputDirectory != null) {
            val dir = config.outputDirectory
            val fileManager = NSFileManager.defaultManager
            if (!fileManager.fileExistsAtPath(dir)) {
                fileManager.createDirectoryAtPath(
                    dir,
                    withIntermediateDirectories = true,
                    attributes = null,
                    error = null,
                )
            }
            "$dir/$fileName"
        } else {
            val tempDir = platform.Foundation.NSTemporaryDirectory()
            "$tempDir$fileName"
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun createTempFile(): String {
        val timestamp = Random.nextLong()
        val fileName = "IMG_$timestamp.${imageFormat.extension}"

        return when (directory) {
            Directory.PICTURES, Directory.DCIM -> {
                // For Photos library, use temp directory first
                val tempDir = platform.Foundation.NSTemporaryDirectory()
                "$tempDir$fileName"
            }
            Directory.DOCUMENTS -> {
                // For Documents, create permanent location
                val documentsDir = platform.Foundation.NSSearchPathForDirectoriesInDomains(
                    platform.Foundation.NSDocumentDirectory,
                    platform.Foundation.NSUserDomainMask,
                    true,
                ).firstOrNull() as? String ?: platform.Foundation.NSTemporaryDirectory()

                // Create CameraK subdirectory
                val cameraKDir = "$documentsDir/CameraK"
                val fileManager = NSFileManager.defaultManager
                if (!fileManager.fileExistsAtPath(cameraKDir)) {
                    fileManager.createDirectoryAtPath(
                        cameraKDir,
                        withIntermediateDirectories = true,
                        attributes = null,
                        error = null,
                    )
                }

                "$cameraKDir/$fileName"
            }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun saveToPhotosLibrary(filePath: String, imageData: NSData): String? {
        var savedAssetId: String? = null
        var saveError: String? = null

        // Save to Photos library synchronously (blocking the capture flow)
        val semaphore = platform.darwin.dispatch_semaphore_create(0)

        PHPhotoLibrary.sharedPhotoLibrary().performChanges(
            changeBlock = {
                val creationRequest = PHAssetChangeRequest.creationRequestForAssetFromImageAtFileURL(
                    NSURL.fileURLWithPath(filePath),
                )
                savedAssetId = creationRequest?.placeholderForCreatedAsset?.localIdentifier
            },
            completionHandler = { success, error ->
                if (!success || error != null) {
                    saveError = error?.localizedDescription ?: "Failed to save to Photos"
                    platform.Foundation.NSLog("CameraK: Failed to save to Photos: ${saveError ?: "Unknown error"}")
                }
                platform.darwin.dispatch_semaphore_signal(semaphore)
            },
        )

        platform.darwin.dispatch_semaphore_wait(semaphore, platform.darwin.DISPATCH_TIME_FOREVER)

        // Return the asset identifier or error
        return if (saveError == null && savedAssetId != null) {
            "ph://$savedAssetId" // Use custom scheme to indicate Photos library asset
        } else {
            null
        }
    }

    private fun FlashMode.toAVCaptureFlashMode(): AVCaptureFlashMode = when (this) {
        FlashMode.ON -> AVCaptureFlashModeOn
        FlashMode.OFF -> AVCaptureFlashModeOff
        FlashMode.AUTO -> AVCaptureFlashModeOff  // iOS: only ON/OFF like Android; AUTO applied as OFF
    }

    private fun TorchMode.toAVCaptureTorchMode(): AVCaptureTorchMode = when (this) {
        TorchMode.ON -> AVCaptureTorchModeOn
        TorchMode.OFF -> AVCaptureTorchModeOff
        TorchMode.AUTO -> AVCaptureTorchModeAuto
    }
}
