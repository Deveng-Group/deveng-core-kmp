package core.presentation.component.mediaviewer

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object MediaViewerDefaults {
    val BackgroundColor: Color = Color.Black
    val PageSpacing: Dp = 16.dp
    const val BeyondViewportPageCount: Int = 2
    const val DismissThreshold: Float = 0.25f
    const val DismissVelocityThreshold: Float = 1000f
}
