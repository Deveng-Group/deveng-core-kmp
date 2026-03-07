package core.domain.camera.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import core.domain.camera.controller.CameraController

/**
 * WASM/JS noop: camera preview is not supported; renders nothing.
 */
@Composable
actual fun CameraPreviewView(controller: CameraController, modifier: Modifier) {
    // No-op: no camera on WASM
}
