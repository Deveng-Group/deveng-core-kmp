package core.presentation.component.scrollbar

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import core.presentation.theme.LocalComponentTheme

@Composable
fun Modifier.scrollbarWithLazyListState(
    listState: LazyListState,
    alwaysShowScrollBar: Boolean? = null,
    width: Dp? = null,
    isScrollBarTrackVisible: Boolean? = null,
    scrollBarTrackColor: Color? = null,
    scrollBarColor: Color? = null,
    scrollBarCornerRadius: Float? = null,
    endPadding: Float? = null,
    topPadding: Dp? = null,
    bottomPadding: Dp? = null
): Modifier {
    val componentTheme = LocalComponentTheme.current
    val scrollbarTheme = componentTheme.scrollbarWithLazyListState

    val finalAlwaysShowScrollbar = alwaysShowScrollBar ?: scrollbarTheme.alwaysShowScrollBar
    val finalWidth = width ?: scrollbarTheme.scrollBarWidth
    val finalIsTrackVisible =
        isScrollBarTrackVisible ?: scrollbarTheme.isScrollBarTrackVisible
    val finalTrackColor = scrollBarTrackColor ?: scrollbarTheme.scrollBarTrackColor
    val finalScrollBarColor = scrollBarColor ?: scrollbarTheme.scrollBarColor
    val finalCornerRadius = scrollBarCornerRadius ?: scrollbarTheme.scrollBarCornerRadius
    val finalEndPadding = endPadding ?: scrollbarTheme.scrollBarEndPadding
    val finalTopPadding = topPadding ?: scrollbarTheme.scrollBarTopPadding
    val finalBottomPadding = bottomPadding ?: scrollbarTheme.scrollBarBottomPadding

    val shouldShowScrollbar =
        finalAlwaysShowScrollbar || isContentSizeEnoughToShowScrollbar(listState)

    return if (shouldShowScrollbar) {
        this.then(
            drawWithContent {
                drawContent()

                val viewportHeight =
                    size.height - (finalTopPadding.toPx() + finalBottomPadding.toPx())
                val firstVisibleItem =
                    listState.layoutInfo.visibleItemsInfo.firstOrNull()
                val itemSize = firstVisibleItem?.size?.toFloat() ?: 1f
                val totalContentHeight =
                    (listState.layoutInfo.totalItemsCount * itemSize).coerceAtLeast(viewportHeight)

                val firstVisibleItemIndex = listState.firstVisibleItemIndex
                val firstItemOffset = listState.firstVisibleItemScrollOffset.toFloat()
                val itemSizeNonNull = (firstVisibleItem?.size ?: 1).toFloat()

                val totalScrollRange =
                    (totalContentHeight - viewportHeight).coerceAtLeast(1f)
                val scrollProgress = (
                        (firstVisibleItemIndex * itemSizeNonNull) + firstItemOffset
                        ).coerceIn(0f, totalScrollRange) / totalScrollRange

                val scrollBarHeight =
                    (viewportHeight / totalContentHeight) * viewportHeight
                val scrollableHeight = (viewportHeight - scrollBarHeight).coerceAtLeast(0f)
                val scrollBarStartOffset =
                    scrollProgress * scrollableHeight + finalTopPadding.toPx()

                if (finalIsTrackVisible) {
                    drawRoundRect(
                        cornerRadius = CornerRadius(finalCornerRadius),
                        color = finalTrackColor,
                        topLeft = Offset(size.width - finalEndPadding, finalTopPadding.toPx()),
                        size = Size(finalWidth.toPx(), viewportHeight)
                    )
                }

                drawRoundRect(
                    cornerRadius = CornerRadius(finalCornerRadius),
                    color = finalScrollBarColor,
                    topLeft = Offset(size.width - finalEndPadding, scrollBarStartOffset),
                    size = Size(finalWidth.toPx(), scrollBarHeight)
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
        (listState.layoutInfo.totalItemsCount * (listState.layoutInfo.visibleItemsInfo.firstOrNull()?.size
            ?: 1)).toFloat()

    return totalContentHeight > viewportHeight
}