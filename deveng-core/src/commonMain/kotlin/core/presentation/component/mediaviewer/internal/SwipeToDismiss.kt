package core.presentation.component.mediaviewer.internal

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
internal fun SwipeToDismissBox(
    enabled: Boolean,
    threshold: Float,
    velocityThreshold: Float,
    onDismiss: () -> Unit,
    onProgressChanged: (Float) -> Unit,
    onDragging: (Boolean) -> Unit,
    content: @Composable () -> Unit,
) {
    val offsetY = remember { Animatable(0f) }
    var containerHeight by remember { mutableFloatStateOf(1f) }
    val scope = rememberCoroutineScope()

    val draggableState = rememberDraggableState { delta ->
        scope.launch {
            offsetY.snapTo(offsetY.value + delta)
            val progress = (abs(offsetY.value) / containerHeight).coerceIn(0f, 1f)
            onProgressChanged(progress)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { containerHeight = it.height.toFloat().coerceAtLeast(1f) }
            // Keep pointer node stable; toggling modifier on/off mid-pinch can cancel
            // sibling gesture detectors while fingers are still down.
            .draggable(
                enabled = enabled,
                state = draggableState,
                orientation = Orientation.Vertical,
                onDragStarted = { onDragging(true) },
                onDragStopped = { velocity ->
                    val fraction = abs(offsetY.value) / containerHeight
                    if (fraction >= threshold || abs(velocity) >= velocityThreshold) {
                        onDragging(false)
                        onDismiss()
                    } else {
                        scope.launch {
                            offsetY.animateTo(0f, spring())
                            onProgressChanged(0f)
                            onDragging(false)
                        }
                    }
                },
            )
            .graphicsLayer {
                if (enabled) {
                    val progress = (abs(offsetY.value) / containerHeight).coerceIn(0f, 1f)
                    translationY = offsetY.value
                    val scale = 1f - (progress * 0.2f)
                    scaleX = scale
                    scaleY = scale
                }
            },
    ) {
        content()
    }
}
