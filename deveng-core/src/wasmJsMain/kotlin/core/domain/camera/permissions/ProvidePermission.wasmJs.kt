package core.domain.camera.permissions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * WASM/JS noop: permissions not supported; camera is unavailable.
 */
@Composable
actual fun providePermissions(): Permissions = remember {
    object : Permissions {
        override fun hasCameraPermission(): Boolean = false
        override fun hasStoragePermission(): Boolean = false

        @Composable
        override fun RequestCameraPermission(onGranted: () -> Unit, onDenied: () -> Unit) {
            onDenied()
        }

        @Composable
        override fun RequestStoragePermission(onGranted: () -> Unit, onDenied: () -> Unit) {
            onDenied()
        }
    }
}
