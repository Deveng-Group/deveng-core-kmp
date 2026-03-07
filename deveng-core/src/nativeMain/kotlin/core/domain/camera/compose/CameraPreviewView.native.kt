package core.domain.camera.compose

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.viewinterop.UIKitViewController
import core.domain.camera.controller.CameraController
import platform.Foundation.NSNotificationCenter
import platform.UIKit.UIDeviceOrientationDidChangeNotification

/**
 * iOS implementation of stateless camera preview.
 * Displays the camera feed using AVFoundation preview layer.
 * Double-tap on the preview switches between front and back camera.
 */
@Composable
actual fun CameraPreviewView(controller: CameraController, modifier: Modifier) {
    key(controller) {
        DisposableEffect(controller) {
            val notificationCenter = NSNotificationCenter.defaultCenter
            val observer = notificationCenter.addObserverForName(
                UIDeviceOrientationDidChangeNotification,
                null,
                null,
            ) { _ ->
                controller.getCameraPreviewLayer()?.connection?.videoOrientation =
                    controller.currentVideoOrientation()
            }

            onDispose {
                notificationCenter.removeObserver(observer)
            }
        }

        Box(modifier = modifier) {
            UIKitViewController(
                factory = { controller },
                modifier = Modifier.fillMaxSize(),
                update = { }
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
}
