package core.presentation.component.scrollbar

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import core.presentation.theme.CustomGrayHintColor

@Composable
fun Modifier.scrollbarWithScrollState(
    scrollState: ScrollState,
    alwaysShowScrollBar: Boolean = false,
    width: Dp = 5.dp,
    isScrollBarTrackVisible: Boolean = true,
    scrollBarTrackColor: Color = Color.Gray,
    scrollBarColor: Color = CustomGrayHintColor,
    scrollBarCornerRadius: Float = 4f,
    endPadding: Float = 12f,
    topPadding: Dp = 0.dp,
    bottomPadding: Dp = 0.dp
): Modifier {
    val shouldShowScrollbar = alwaysShowScrollBar || isContentSizeEnoughToShowScrollbar(scrollState)

    return if (shouldShowScrollbar) {
        this.then(
            drawWithContent {
                drawContent()

                val viewportHeight = this.size.height - (topPadding.toPx() + bottomPadding.toPx())
                val totalContentHeight = scrollState.maxValue.toFloat() + viewportHeight
                val scrollValue = scrollState.value.toFloat()

                val scrollBarHeight =
                    (viewportHeight / totalContentHeight) * viewportHeight

                val scrollBarStartOffset =
                    (scrollValue / totalContentHeight) * viewportHeight + topPadding.toPx()

                if (isScrollBarTrackVisible) {
                    drawRoundRect(
                        cornerRadius = CornerRadius(scrollBarCornerRadius),
                        color = scrollBarTrackColor,
                        topLeft = Offset(this.size.width - endPadding, topPadding.toPx()),
                        size = Size(width.toPx(), viewportHeight),
                    )
                }

                drawRoundRect(
                    cornerRadius = CornerRadius(scrollBarCornerRadius),
                    color = scrollBarColor,
                    topLeft = Offset(this.size.width - endPadding, scrollBarStartOffset),
                    size = Size(width.toPx(), scrollBarHeight)
                )
            }
        )
    } else {
        this
    }
}

@Composable
fun isContentSizeEnoughToShowScrollbar(scrollState: ScrollState): Boolean {
    val viewportHeight = scrollState.maxValue.toFloat() + scrollState.value.toFloat()
    val totalContentHeight = scrollState.maxValue.toFloat() + viewportHeight

    return totalContentHeight > viewportHeight
}
