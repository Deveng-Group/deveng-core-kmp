package core.presentation.component.scrollbar

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.scrollbarWithLazyListState(
    listState: LazyListState,
    alwaysShowScrollBar: Boolean = false,
    width: Dp = 5.dp,
    isScrollBarTrackVisible: Boolean = true,
    scrollBarTrackColor: Color = Color.Gray,
    scrollBarColor: Color = MaterialTheme.colorScheme.primary,
    scrollBarCornerRadius: Float = 4f,
    endPadding: Float = 12f,
    topPadding: Dp = 0.dp,
    bottomPadding: Dp = 0.dp
): Modifier {
    val shouldShowScrollbar =
        alwaysShowScrollBar || isContentSizeEnoughToShowScrollbar(listState)

    return if (shouldShowScrollbar) {
        this.then(
            drawWithContent {
                drawContent()

                val viewportHeight = size.height - (topPadding.toPx() + bottomPadding.toPx())
                val itemSize =
                    listState.layoutInfo.visibleItemsInfo.firstOrNull()?.size?.toFloat() ?: 1f
                val totalContentHeight = listState.layoutInfo.totalItemsCount * itemSize

                val firstVisibleItemIndex = listState.firstVisibleItemIndex
                val firstItemOffset = listState.firstVisibleItemScrollOffset.toFloat()

                val totalScrollRange = totalContentHeight - viewportHeight
                val itemSizeNonNull = listState.layoutInfo.visibleItemsInfo.firstOrNull()?.size ?: 1
                val scrollProgress =
                    ((firstVisibleItemIndex * itemSizeNonNull) + firstItemOffset) / totalScrollRange

                val scrollBarHeight = (viewportHeight / totalContentHeight) * viewportHeight
                val scrollBarStartOffset =
                    scrollProgress * (viewportHeight - scrollBarHeight) + topPadding.toPx()

                if (isScrollBarTrackVisible) {
                    drawRoundRect(
                        cornerRadius = CornerRadius(scrollBarCornerRadius),
                        color = scrollBarTrackColor,
                        topLeft = Offset(size.width - endPadding, topPadding.toPx()),
                        size = Size(width.toPx(), viewportHeight)
                    )
                }

                drawRoundRect(
                    cornerRadius = CornerRadius(scrollBarCornerRadius),
                    color = scrollBarColor,
                    topLeft = Offset(size.width - endPadding, scrollBarStartOffset),
                    size = Size(width.toPx(), scrollBarHeight)
                )
            }
        )
    } else {
        this
    }
}

@Composable
fun isContentSizeEnoughToShowScrollbar(listState: LazyListState): Boolean {
    val viewportHeight = listState.layoutInfo.viewportSize.height.toFloat()
    val totalContentHeight =
        (listState.layoutInfo.totalItemsCount * (listState.layoutInfo.visibleItemsInfo.firstOrNull()?.size ?: 1)).toFloat()

    return totalContentHeight > viewportHeight
}