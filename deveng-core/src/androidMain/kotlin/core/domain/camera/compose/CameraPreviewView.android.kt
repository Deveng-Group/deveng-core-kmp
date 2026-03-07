package core.domain.camera.compose

import androidx.camera.view.PreviewView
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import core.domain.camera.controller.CameraController

/**
 * Android implementation of stateless camera preview.
 * Displays the camera feed using CameraX's PreviewView.
 * Double-tap on the preview switches between front and back camera.
 */
@Composable
actual fun CameraPreviewView(controller: CameraController, modifier: Modifier) {
    val context = LocalContext.current
    val previewView = remember { PreviewView(context) }

    DisposableEffect(controller, previewView) {
        controller.bindCamera(previewView) {
            // Camera is already bound and started by the state holder
        }
        onDispose {
            // Cleanup is handled by the state holder
        }
    }

    Box(modifier = modifier) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(controller) {
                    detectTapGestures(
                        onDoubleTap = { controller.toggleCameraLens() },
                    )
                },
        )
    }
}
