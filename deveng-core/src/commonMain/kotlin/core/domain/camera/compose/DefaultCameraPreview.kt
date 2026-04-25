package core.domain.camera.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.draw.alpha
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import core.domain.camera.controller.CameraController
import core.domain.camera.state.CameraKEvent
import core.domain.camera.state.CameraKStateHolder
import core.domain.camera.state.CameraUIState
import core.domain.camera.video.VideoCaptureResult
import core.domain.camera.video.VideoConfiguration
import kotlinx.coroutines.flow.collect
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.painterResource
import core.domain.camera.enums.CameraLens
import core.domain.camera.enums.FlashMode
import core.domain.camera.result.ImageCaptureResult
import core.domain.camera.ui.CameraIcons
import core.presentation.component.CustomIconButton
import core.presentation.theme.CoreRegularTextStyle
import kotlinx.coroutines.launch

private enum class CameraCaptureMode { Photo, Video }

private fun formatRecordingDuration(ms: Long): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val secondsStr = seconds.toString().padStart(2, '0')
    return "$minutes:$secondsStr"
}

/**
 * Default camera preview with built-in controls: flash, switch camera, zoom chips,
 * gallery button, and capture button. Use this when you want a ready-to-use camera UI.
 *
 * @param controller The camera controller from [CameraKState.Ready].
 * @param onImageCaptured Callback when a photo is taken (file or error). The app handles the result.
 * @param onGalleryClick Optional callback when the gallery button is tapped (e.g. open gallery).
 * @param onLastPhotoClick Optional callback when the thumbnail is tapped; receives the [ImageBitmap] (initial or last captured).
 * @param initialThumbnailBitmap Optional bitmap to show as thumbnail when opening the camera (e.g. last photo). Replaced by captured photo when user takes a picture.
 * @param thumbnailTopEndContent Slot for content placed at the top-end of the thumbnail (e.g. a count badge). Design is fully controlled by the caller.
 * @param stateHolder Optional [CameraKStateHolder] from [rememberCameraKState] (via onHolder). When set, shows Photo/Video tab selection and the center button acts as capture in Photo mode or start/stop in Video mode.
 * @param onRecordingStopped Optional callback when a video recording stops (success or error). Use it to load a first-frame thumbnail and pass it as [lastRecordedVideoThumbnail].
 * @param lastRecordedVideoThumbnail Optional bitmap to show as thumbnail for the last recorded video (e.g. first frame). Shown when the last capture was video; replaced when user takes a photo.
 * @param modifier Modifier for the root layout.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultCameraPreview(
    controller: CameraController,
    onImageCaptured: (ImageCaptureResult) -> Unit,
    onGalleryClick: (() -> Unit)? = null,
    onLastPhotoClick: ((ImageBitmap) -> Unit)? = null,
    initialThumbnailBitmap: ImageBitmap? = null,
    thumbnailTopEndContent: @Composable () -> Unit = {},
    stateHolder: CameraKStateHolder? = null,
    onRecordingStopped: ((VideoCaptureResult) -> Unit)? = null,
    lastRecordedVideoThumbnail: ImageBitmap? = null,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val recordingUiState by (stateHolder?.uiState?.collectAsState(CameraUIState())
        ?: remember { mutableStateOf(CameraUIState()) })
    val zoomLevelState = remember { mutableStateOf(1f) }
    var zoomLevel by zoomLevelState
    val maxZoomState = remember { mutableStateOf(1f) }
    var maxZoom by maxZoomState
    var currentFlashMode by remember { mutableStateOf(FlashMode.OFF) }
    var isNightMode by remember { mutableStateOf(false) }
    /** Tracked in Compose so flash visibility updates when lens changes (controller alone does not trigger recomposition). */
    var currentCameraLens by remember { mutableStateOf(controller.getCameraLens() ?: CameraLens.BACK) }
    var focusTapOffset by remember { mutableStateOf<Offset?>(null) }
    var overlaySizePx by remember { mutableStateOf(IntSize.Zero) }
    var brightnessIndex by remember { mutableStateOf(0f) }
    var isAdjustingBrightness by remember { mutableStateOf(false) }
    var lastCapturedBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var captureMode by remember { mutableStateOf(CameraCaptureMode.Photo) }
    var shutterEffectTrigger by remember { mutableStateOf(0) }
    var showShutterFlash by remember { mutableStateOf(false) }

    LaunchedEffect(stateHolder) {
        stateHolder?.events?.collect { event ->
            when (event) {
                is CameraKEvent.RecordingStopped -> {
                    lastCapturedBitmap = null
                    onRecordingStopped?.invoke(event.result)
                }
                else -> { }
            }
        }
    }

    LaunchedEffect(focusTapOffset) {
        if (focusTapOffset != null) {
            controller.setExposureCompensationIndex(0)
            brightnessIndex = 0f
        }
    }

    // iOS: native UIKit gesture recognizers handle taps because the first Compose touch
    // is lost to UIKit interop routing. Wire callbacks to update Compose state.
    DisposableEffect(controller) {
        controller.onPreviewTapListener = { nx, ny ->
            val w = overlaySizePx.width
            val h = overlaySizePx.height
            println("[CameraFocus] onPreviewTapListener: nx=$nx ny=$ny overlaySizePx=${w}x$h")
            if (w > 0 && h > 0) {
                focusTapOffset = Offset(x = nx * w, y = ny * h)
            }
        }
        controller.onPreviewDoubleTapListener = {
            if (stateHolder != null) stateHolder.toggleCameraLens()
            else controller.toggleCameraLens()
            currentCameraLens = controller.getCameraLens() ?: CameraLens.BACK
            maxZoomState.value = controller.getMaxZoom()
            zoomLevelState.value = controller.getZoom()
        }
        onDispose {
            controller.onPreviewTapListener = null
            controller.onPreviewDoubleTapListener = null
        }
    }

    LaunchedEffect(recordingUiState.cameraLens, stateHolder) {
        if (stateHolder != null) {
            recordingUiState.cameraLens?.let { currentCameraLens = it }
        }
    }

    LaunchedEffect(controller) {
        currentCameraLens = controller.getCameraLens() ?: CameraLens.BACK
        currentFlashMode = controller.getFlashMode() ?: FlashMode.OFF
        maxZoomState.value = controller.getMaxZoom()
        zoomLevelState.value = controller.getZoom()
        // Re-read zoom when camera may have bound (Android reports maxZoom only after bind)
        repeat(5) {
            kotlinx.coroutines.delay(400L)
            val newMax = controller.getMaxZoom()
            val newZoom = controller.getZoom()
            if (newMax > 0f) maxZoomState.value = newMax
            if (newZoom > 0f) zoomLevelState.value = newZoom
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { overlaySizePx = it },
    ) {
        // Preview first so overlay can be drawn on top (important on iOS: native preview must not steal first tap)
        CameraPreviewView(
            controller = controller,
            modifier = Modifier.fillMaxSize(),
        )
        // Full-screen pinch/tap for zoom & focus (Android/Desktop). Must stay *below* chrome (zIndex 4f+)
        // or it wins hit-testing and blocks capture / flash / gallery.
        CameraZoomGestureOverlay(
            controller = controller,
            modifier = Modifier.fillMaxSize().zIndex(3f),
            onZoomChange = { zoomLevelState.value = it },
            onDoubleTap = {
                if (stateHolder != null) stateHolder.toggleCameraLens()
                else controller.toggleCameraLens()
                currentCameraLens = controller.getCameraLens() ?: CameraLens.BACK
                maxZoomState.value = controller.getMaxZoom()
                zoomLevelState.value = controller.getZoom()
            },
            onFocusPointTapped = { nx, ny ->
                val w = overlaySizePx.width
                val h = overlaySizePx.height
                println("[CameraFocus] DefaultCameraPreview onFocusPointTapped: nx=$nx ny=$ny overlaySizePx=${w}x$h")
                if (w > 0 && h > 0) {
                    val offset = Offset(x = nx * w, y = ny * h)
                    println("[CameraFocus] DefaultCameraPreview setting focusTapOffset=$offset")
                    focusTapOffset = offset
                } else {
                    println("[CameraFocus] DefaultCameraPreview SKIP (overlaySizePx invalid)")
                }
            },
        )
        // Shutter flash effect when a photo is captured
        if (showShutterFlash) {
            ShutterFlashOverlay(
                trigger = shutterEffectTrigger,
                onFlashComplete = { showShutterFlash = false },
                modifier = Modifier.fillMaxSize().zIndex(5f),
            )
        }
        // Snapchat-style focus indicator: circular reticle with pop-in, ripple, bounce, fade-out
        FocusIndicator(
            tapPosition = focusTapOffset,
            onAnimationComplete = { focusTapOffset = null },
            modifier = Modifier.fillMaxSize().zIndex(2f),
            keepVisible = isAdjustingBrightness,
        )
        // Brightness slider under focus circle; reset to default when user taps again (handled in onFocusPointTapped + LaunchedEffect)
        focusTapOffset?.let { tap ->
            val (min, max) = controller.getExposureCompensationRange()
                if (min != max) {
                val density = LocalDensity.current
                with(density) {
                    val sliderWidthPx = 100.dp.toPx()
                    val circleRadiusPx = 38.dp.toPx()
                    // Negative gap: Slider has internal top padding / centered track, so we pull the box up
                    // so the visible track line sits just below the circle
                    val gapPx = (-9).dp.toPx()
                    val leftPx = (tap.x - sliderWidthPx / 2f).coerceIn(0f, (overlaySizePx.width - sliderWidthPx).toFloat())
                    val minTop = 0f
                    val maxTop = (overlaySizePx.height - 32f).coerceAtLeast(minTop)
                    val topPx = (tap.y + circleRadiusPx + gapPx).coerceIn(minTop, maxTop)
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .zIndex(4f)
                            .absoluteOffset { IntOffset(leftPx.roundToInt(), topPx.roundToInt()) }
                            .width(100.dp)
                            .padding(horizontal = 8.dp),
                    ) {
                        Slider(
                            value = brightnessIndex,
                            onValueChange = { new ->
                                isAdjustingBrightness = true
                                brightnessIndex = new
                                controller.setExposureCompensationIndex(new.toInt())
                            },
                            onValueChangeFinished = { isAdjustingBrightness = false },
                            valueRange = min.toFloat()..max.toFloat(),
                            colors = SliderDefaults.colors(
                                thumbColor = Color.Transparent,
                                activeTrackColor = Color.White,
                                inactiveTrackColor = Color.White,
                            ),
                            thumb = {
                                Icon(
                                    painter = painterResource(CameraIcons.sun),
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp),
                                )
                            },
                            track = { sliderState ->
                                SliderDefaults.Track(
                                    sliderState = sliderState,
                                    modifier = Modifier.height(1.5.dp),
                                    drawStopIndicator = null,
                                    colors = SliderDefaults.colors(
                                        thumbColor = Color.Transparent,
                                        activeTrackColor = Color.White,
                                        inactiveTrackColor = Color.White,
                                    ),
                                    enabled = true,
                                )
                            },
                        )
                    }
                }
            }
        }
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .zIndex(4f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (currentCameraLens != CameraLens.FRONT) {
                CustomIconButton(
                    icon = CameraIcons.flash,
                    iconDescription = "Flash",
                    iconTint = when (currentFlashMode) {
                        FlashMode.ON, FlashMode.AUTO -> Color.White
                        FlashMode.OFF -> Color.White.copy(alpha = 0.5f)
                    },
                    backgroundColor = Color.Transparent,
                    shadowElevation = 0.dp,
                    onClick = {
                        controller.toggleFlashMode()
                        currentFlashMode = controller.getFlashMode() ?: FlashMode.OFF
                    },
                )
            }
            if (captureMode == CameraCaptureMode.Photo) {
                CustomIconButton(
                    icon = CameraIcons.moon,
                    iconDescription = "Night mode",
                    iconTint = if (isNightMode) Color.White else Color.White.copy(alpha = 0.5f),
                    backgroundColor = Color.Transparent,
                    shadowElevation = 0.dp,
                    onClick = {
                        controller.toggleNightMode()
                        isNightMode = controller.isNightModeEnabled()
                    },
                )
            }
            CustomIconButton(
                icon = CameraIcons.switchCamera,
                iconDescription = "Switch camera",
                iconTint = Color.White,
                backgroundColor = Color.Transparent,
                shadowElevation = 0.dp,
                    onClick = {
                        if (stateHolder != null) stateHolder.toggleCameraLens()
                        else controller.toggleCameraLens()
                        currentCameraLens = controller.getCameraLens() ?: CameraLens.BACK
                        maxZoomState.value = controller.getMaxZoom()
                        zoomLevelState.value = controller.getZoom()
                    },
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .zIndex(4f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ZoomChips(
                zoomLevel = zoomLevel,
                maxZoom = maxZoom,
                onZoomChange = { level ->
                    controller.setZoom(level)
                    zoomLevelState.value = controller.getZoom()
                },
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                ) {
                    val thumbnailBitmap = lastCapturedBitmap ?: lastRecordedVideoThumbnail ?: initialThumbnailBitmap
                    thumbnailBitmap?.let { bitmap ->
                        val thumbShape = RoundedCornerShape(8.dp)
                        Box(
                            modifier = Modifier
                                .size(width = 44.dp, height = 56.dp)
                                .clip(thumbShape)
                                .border(2.dp, Color.White, thumbShape)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = { onLastPhotoClick?.invoke(bitmap) },
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Image(
                                bitmap = bitmap,
                                contentDescription = "Photo thumbnail",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 4.dp, y = (-4).dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                thumbnailTopEndContent()
                            }
                        }
                        Spacer(modifier = Modifier.size(8.dp))
                    }
                    if (thumbnailBitmap == null) {
                        CustomIconButton(
                            icon = CameraIcons.galleryPhotoLibrary,
                            iconDescription = "Gallery",
                            iconTint = Color.White,
                            backgroundColor = Color.Transparent,
                            shadowElevation = 0.dp,
                            onClick = { onGalleryClick?.invoke() ?: Unit },
                        )
                    }
                }
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    ShutterButton(
                        mode = captureMode,
                        isRecording = recordingUiState.isRecording,
                        recordingDurationMs = recordingUiState.recordingDurationMs,
                        stateHolder = stateHolder,
                        onPhotoCapture = {
                            showShutterFlash = true
                            shutterEffectTrigger++
                            scope.launch {
                                val result = controller.takePictureToFile()
                                lastCapturedBitmap = (result as? ImageCaptureResult.Success)?.bitmap
                                onImageCaptured(result)
                            }
                        },
                        onVideoStart = { stateHolder?.startRecording(VideoConfiguration()) },
                        onVideoStop = { stateHolder?.stopRecording() },
                    )
                }
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (stateHolder != null) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            ModeSwitcher(
                                currentMode = captureMode,
                                onModeChange = { newMode ->
                                    if (newMode == CameraCaptureMode.Video && isNightMode) {
                                        controller.setNightMode(false)
                                        isNightMode = false
                                    }
                                    captureMode = newMode
                                },
                                enabled = !recordingUiState.isRecording,
                            )
                            Box(
                                modifier = Modifier.heightIn(min = 20.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (recordingUiState.isRecording) {
                                    Text(
                                        text = formatRecordingDuration(recordingUiState.recordingDurationMs),
                                        color = Color.White,
                                        style = CoreRegularTextStyle().copy(fontSize = 16.sp),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Full-screen black flash overlay to indicate photo capture (shutter effect).
 * Animates in quickly, then fades out and calls [onFlashComplete].
 */
@Composable
private fun ShutterFlashOverlay(
    trigger: Int,
    onFlashComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(trigger) {
        if (trigger <= 0) return@LaunchedEffect
        alpha.snapTo(0f)
        alpha.animateTo(0.75f, animationSpec = tween(durationMillis = 40))
        alpha.animateTo(0f, animationSpec = tween(durationMillis = 120))
        onFlashComplete()
    }

    Box(
        modifier = modifier
            .alpha(alpha.value)
            .background(Color.Black),
    )
}

@Composable
private fun ModeSwitcher(
    currentMode: CameraCaptureMode,
    onModeChange: (CameraCaptureMode) -> Unit,
    enabled: Boolean = true,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(32.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CameraCaptureMode.entries.forEach { mode ->
            val isSelected = mode == currentMode
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .clickable(enabled = enabled) { onModeChange(mode) },
            ) {
                Text(
                    text = mode.name.uppercase(),
                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
                    style = CoreRegularTextStyle().copy(
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    ),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .height(3.dp)
                        .width(if (isSelected) 24.dp else 0.dp)
                        .background(
                            if (isSelected) Color.White else Color.Transparent,
                            RoundedCornerShape(1.5.dp),
                        ),
                )
            }
        }
    }
}

@Composable
private fun ShutterButton(
    mode: CameraCaptureMode,
    isRecording: Boolean,
    recordingDurationMs: Long,
    stateHolder: CameraKStateHolder?,
    onPhotoCapture: () -> Unit,
    onVideoStart: () -> Unit,
    onVideoStop: () -> Unit,
) {
    val isVideoMode = mode == CameraCaptureMode.Video && stateHolder != null
    val outerColor = if (isRecording) Color.Red else Color.White
    val innerColor = if (isVideoMode) Color.Red.copy(alpha = 0.9f) else Color.White
    val innerSize = if (isRecording) 24.dp else 60.dp
    val innerShape = if (isRecording) RoundedCornerShape(8.dp) else CircleShape

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(72.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    if (isVideoMode) {
                        if (isRecording) onVideoStop() else onVideoStart()
                    } else {
                        onPhotoCapture()
                    }
                },
            ),
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .border(3.dp, outerColor, CircleShape),
        )
        Box(
            modifier = Modifier
                .size(innerSize)
                .clip(innerShape)
                .background(innerColor)
                .border(
                    width = 2.dp,
                    color = Color.White,
                    shape = innerShape,
                ),
        )
    }
}

@Composable
private fun ZoomChips(
    modifier: Modifier = Modifier,
    zoomLevel: Float,
    maxZoom: Float,
    onZoomChange: (Float) -> Unit,
) {
    val stops = buildList {
        add(1f)
        if (maxZoom >= 2f) add(2f)
        if (maxZoom > 2f && maxZoom >= 4f) add(4f.coerceAtMost(maxZoom))
        if (maxZoom > 4f) add(maxZoom)
    }.distinct()

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black.copy(alpha = 0.5f)),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 3.dp, vertical = 3.dp),
            horizontalArrangement = Arrangement.spacedBy(1.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            stops.forEach { stop ->
                val isActive = (zoomLevel - stop).let { it > -0.15f && it < 0.15f }
                val label = if (stop == stop.toLong().toFloat()) {
                    "${stop.toInt()}x"
                } else {
                    "${(stop * 10).toInt() / 10f}x"
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (isActive) Color.White.copy(alpha = 0.25f) else Color.Transparent)
                        .clickable { onZoomChange(stop) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = label,
                        color = if (isActive) Color.White else Color.White.copy(alpha = 0.7f),
                        style = CoreRegularTextStyle().copy(fontSize = 10.sp),
                    )
                }
            }
        }
    }
}
