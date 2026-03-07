package core.domain.camera.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import core.domain.camera.state.CameraConfiguration
import core.domain.camera.state.CameraKState
import core.domain.camera.state.CameraKStateHolder

/**
 * WASM/JS noop: camera is not supported; returns Error state.
 */
@Composable
actual fun rememberCameraKState(
    config: CameraConfiguration,
    setupPlugins: suspend (CameraKStateHolder) -> Unit,
    onHolder: (CameraKStateHolder) -> Unit,
): State<CameraKState> {
    return remember {
        mutableStateOf(
            CameraKState.Error(
                exception = UnsupportedOperationException("Camera is not supported on WASM/JS"),
                message = "Camera is not supported on this platform",
                isRetryable = false,
            ),
        )
    }
}
