package core.domain.camera.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import core.domain.camera.controller.CameraController

/** WASM/JS: no camera zoom gestures (camera not supported). */
@Composable
actual fun CameraZoomGestureOverlay(
    controller: CameraController,
    modifier: Modifier,
    onZoomChange: (Float) -> Unit,
    onDoubleTap: () -> Unit,
    onFocusPointTapped: (normalizedX: Float, normalizedY: Float) -> Unit,
) {
    Box(modifier = modifier.fillMaxSize())
}
