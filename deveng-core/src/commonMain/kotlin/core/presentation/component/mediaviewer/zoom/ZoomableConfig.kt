package core.presentation.component.mediaviewer.zoom

import androidx.compose.runtime.Immutable

@Immutable
data class ZoomableConfig(
    val minZoom: Float = ZoomableDefaults.MinZoom,
    val maxZoom: Float = ZoomableDefaults.MaxZoom,
    val doubleTapZoom: Float = ZoomableDefaults.DoubleTapZoom,
    val enableDoubleTapZoom: Boolean = true,
)
