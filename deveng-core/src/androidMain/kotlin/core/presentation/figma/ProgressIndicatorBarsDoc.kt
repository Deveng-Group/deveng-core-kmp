package core.presentation.figma

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.figma.code.connect.Figma
import com.figma.code.connect.FigmaConnect
import com.figma.code.connect.FigmaProperty
import com.figma.code.connect.FigmaType
import core.presentation.component.progressindicatorbars.IndicatorType
import core.presentation.component.progressindicatorbars.ProgressIndicatorBars

@FigmaConnect(
    url = "https://www.figma.com/design/sJoAsKB4qqqrwvHRlowppo/Design-System?node-id=2-12&t=JntFNGouWhSDj0EP-0"
)
class ProgressIndicatorBarsDoc {
    // --- INTERNAL ENUMS MIRRORING FIGMA VARIANTS / PROPS ---

    enum class IndicatorTypeVariant { HighlightCurrent, HighlightUntilCurrent }

    // --- FIGMA-PROPERTIES (MAPPED TO FIGMA COMPONENT PROPS) ---

    @FigmaProperty(FigmaType.Text, "Page count")
    val pageCount: Int = 3

    @FigmaProperty(FigmaType.Text, "Current page")
    val currentPage: Int = 1

    @FigmaProperty(FigmaType.Enum, "Indicator type")
    val indicatorTypeVariant: IndicatorTypeVariant = Figma.mapping(
        "Highlight current" to IndicatorTypeVariant.HighlightCurrent,
        "Highlight until current" to IndicatorTypeVariant.HighlightUntilCurrent
    )

    // --- DERIVED VALUES (NOT DIRECTLY FIGMA PROPS, BUT NEEDED FOR FULL API) ---

    // 1. modifier
    val modifier: Modifier = Modifier

    // 2. indicatorType
    val indicatorType: IndicatorType
        get() = when (indicatorTypeVariant) {
            IndicatorTypeVariant.HighlightCurrent -> IndicatorType.HIGH_LIGHT_CURRENT
            IndicatorTypeVariant.HighlightUntilCurrent -> IndicatorType.HIGH_LIGHT_UNTIL_CURRENT
        }

    // 3. colors (null = use theme defaults)
    val filledIndicatorColor: Color? = null
    val defaultIndicatorColor: Color? = null

    // 4. dimensions (null = use theme defaults)
    val indicatorHeight: Dp? = null
    val indicatorSpacing: Dp? = null
    val indicatorCornerRadius: Dp? = null

    // --- THE COMPOSABLE SNIPPET (USES *ALL* PARAMETERS EXPLICITLY) ---

    @Composable
    fun Component() {
        ProgressIndicatorBars(
            modifier = modifier,
            pageCount = pageCount,
            currentPage = currentPage,
            indicatorType = indicatorType,
            filledIndicatorColor = filledIndicatorColor,
            defaultIndicatorColor = defaultIndicatorColor,
            indicatorHeight = indicatorHeight,
            indicatorSpacing = indicatorSpacing,
            indicatorCornerRadius = indicatorCornerRadius
        )
    }
}
