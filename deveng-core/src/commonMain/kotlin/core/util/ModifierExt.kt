package core.util

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal fun Modifier.debouncedCombinedClickable(
    debounceMillis: Long = 600L,
    enabled: Boolean = true,
    onLongClick: (() -> Unit)? = null,
    onClick: () -> Unit
): Modifier = composed {
    val clickLatest by rememberUpdatedState(onClick)
    val longLatest by rememberUpdatedState(onLongClick)

    var canClick by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val interactionSource = remember { MutableInteractionSource() }

    combinedClickable(
        enabled = enabled,
        interactionSource = interactionSource,
        onClick = {
            if (!canClick) return@combinedClickable
            canClick = false
            clickLatest()
            scope.launch {
                delay(debounceMillis)
                canClick = true
            }
        },
        onLongClick = { longLatest?.invoke() }
    )
}

internal fun Modifier.disableSplitMotionEvents() =
    pointerInput(Unit) {
        coroutineScope {
            var currentId: Long = -1L
            awaitPointerEventScope {
                while (true) {
                    awaitPointerEvent(PointerEventPass.Initial).changes.forEach { pointerInfo ->
                        when {
                            pointerInfo.pressed && currentId == -1L -> currentId =
                                pointerInfo.id.value

                            pointerInfo.pressed.not() && currentId == pointerInfo.id.value -> currentId =
                                -1

                            pointerInfo.id.value != currentId && currentId != -1L -> pointerInfo.consume()
                            else -> Unit
                        }
                    }
                }
            }
        }
    }