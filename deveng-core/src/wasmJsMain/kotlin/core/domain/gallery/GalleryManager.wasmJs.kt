package core.domain.gallery

import androidx.compose.runtime.Composable
import core.domain.camera.SharedImage

@Composable
actual fun rememberGalleryManager(onResult: (SharedImage?) -> Unit): GalleryManager {
    return GalleryManager {
    }

}

actual class GalleryManager actual constructor(onLaunch: () -> Unit) {
    actual fun launch() {
    }

}