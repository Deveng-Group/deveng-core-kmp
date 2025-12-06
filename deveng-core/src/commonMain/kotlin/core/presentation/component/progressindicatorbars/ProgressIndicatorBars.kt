package core.presentation.component.progressindicatorbars

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import core.presentation.theme.AppTheme
import core.presentation.theme.LocalComponentTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * A horizontal progress indicator bar component showing page/step progress.
 * Displays multiple indicator bars where some are highlighted based on the current page and indicator type.
 *
 * @param modifier Modifier to be applied to the row container.
 * @param pageCount Total number of pages/steps to indicate.
 * @param currentPage Current page/step index (0-based).
 * @param indicatorType Type of highlighting: HIGH_LIGHT_CURRENT highlights only current page,
 *                      HIGH_LIGHT_UNTIL_CURRENT highlights all pages up to and including current. Default is HIGH_LIGHT_CURRENT.
 * @param filledIndicatorColor Color of filled/highlighted indicators. If null, uses theme default.
 * @param defaultIndicatorColor Color of unfilled/default indicators. If null, uses theme default.
 * @param indicatorHeight Height of each indicator bar. If null, uses theme default.
 * @param indicatorSpacing Spacing between indicator bars. If null, uses theme default.
 * @param indicatorCornerRadius Corner radius of the indicator bars. If null, uses theme default.
 */
@Composable
fun ProgressIndicatorBars(
    modifier: Modifier = Modifier,
    pageCount: Int,
    currentPage: Int,
    indicatorType: IndicatorType = IndicatorType.HIGH_LIGHT_CURRENT,
    filledIndicatorColor: Color? = null,
    defaultIndicatorColor: Color? = null,
    indicatorHeight: Dp? = null,
    indicatorSpacing: Dp? = null,
    indicatorCornerRadius: Dp? = null
) {
    val componentTheme = LocalComponentTheme.current
    val progressIndicatorBarsTheme = componentTheme.progressIndicatorBars

    val finalFilledIndicatorColor =
        filledIndicatorColor ?: progressIndicatorBarsTheme.filledIndicatorColor
    val finalDefaultIndicatorColor =
        defaultIndicatorColor ?: progressIndicatorBarsTheme.defaultIndicatorColor
    val finalIndicatorHeight = indicatorHeight ?: progressIndicatorBarsTheme.indicatorHeight
    val finalIndicatorSpacing = indicatorSpacing ?: progressIndicatorBarsTheme.indicatorSpacing
    val finalIndicatorCornerRadius =
        indicatorCornerRadius ?: progressIndicatorBarsTheme.indicatorCornerRadius

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(finalIndicatorSpacing)
    ) {
        for (index in 0 until pageCount) {
            val isCurrentIndicatorHighLighted = when (indicatorType) {
                IndicatorType.HIGH_LIGHT_CURRENT -> index == currentPage
                IndicatorType.HIGH_LIGHT_UNTIL_CURRENT -> index <= currentPage
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(finalIndicatorHeight)
                    .clip(RoundedCornerShape(finalIndicatorCornerRadius))
                    .background(
                        if (isCurrentIndicatorHighLighted) finalFilledIndicatorColor else finalDefaultIndicatorColor
                    )
            )
        }
    }
}

@Preview
@Composable
fun ProgressIndicatorBarsPreview() {
    AppTheme {
        ProgressIndicatorBars(
            pageCount = 3,
            currentPage = 1,
            filledIndicatorColor = Color.Blue,
            defaultIndicatorColor = Color.LightGray
        )
    }
}

