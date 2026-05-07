package core.domain.camera.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import core.domain.camera.controller.CameraController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

/**
 * WASM/JS: shows frames from [getFrameChannel] (JPEG snapshots from the live video track).
 */
@Composable
actual fun CameraPreviewView(controller: CameraController, modifier: Modifier) {
    BoxWithConstraints(modifier = modifier) {
        val scope = rememberCoroutineScope()
        val frameChannel = controller.getFrameChannel()
        var currentFrame by remember { mutableStateOf<ImageBitmap?>(null) }

        LaunchedEffect(controller) {
            scope.launch(Dispatchers.Default) {
                frameChannel.consumeAsFlow().collect { frame ->
                    currentFrame = frame
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            currentFrame?.let { frame ->
                Image(
                    bitmap = frame,
                    contentDescription = "Camera Preview",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }
        }
    }
}
