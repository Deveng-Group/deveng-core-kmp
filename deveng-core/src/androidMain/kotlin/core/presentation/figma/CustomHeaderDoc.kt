package core.presentation.figma

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.figma.code.connect.Figma
import com.figma.code.connect.FigmaConnect
import com.figma.code.connect.FigmaProperty
import com.figma.code.connect.FigmaType
import core.presentation.component.CustomHeader
import global.deveng.deveng_core.generated.resources.Res
import global.deveng.deveng_core.generated.resources.shared_ic_angle_right
import global.deveng.deveng_core.generated.resources.shared_ic_arrow_left
import org.jetbrains.compose.resources.DrawableResource

@FigmaConnect(
    url = "https://www.figma.com/design/sJoAsKB4qqqrwvHRlowppo/Design-System?node-id=2-9&t=JntFNGouWhSDj0EP-0"
)
class CustomHeaderDoc {
    // --- INTERNAL ENUMS MIRRORING FIGMA VARIANTS / PROPS ---

    enum class LeftIconVariant { ArrowLeft, AngleRight, None }

    enum class RightIconVariant { AngleRight, ArrowLeft, None }

    enum class CenterIconVariant { AngleRight, ArrowLeft, None }

    // --- FIGMA-PROPERTIES (MAPPED TO FIGMA COMPONENT PROPS) ---

    @FigmaProperty(FigmaType.Enum, "Left icon")
    val leftIconVariant: LeftIconVariant = Figma.mapping(
        "Arrow left" to LeftIconVariant.ArrowLeft,
        "Angle right" to LeftIconVariant.AngleRight,
        "None" to LeftIconVariant.None
    )

    @FigmaProperty(FigmaType.Enum, "Right icon")
    val rightIconVariant: RightIconVariant = Figma.mapping(
        "Angle right" to RightIconVariant.AngleRight,
        "Arrow left" to RightIconVariant.ArrowLeft,
        "None" to RightIconVariant.None
    )

    @FigmaProperty(FigmaType.Enum, "Center icon")
    val centerIconVariant: CenterIconVariant = Figma.mapping(
        "Angle right" to CenterIconVariant.AngleRight,
        "Arrow left" to CenterIconVariant.ArrowLeft,
        "None" to CenterIconVariant.None
    )

    @FigmaProperty(FigmaType.Boolean, "Left icon visible")
    val isLeftIconButtonVisible: Boolean = true

    @FigmaProperty(FigmaType.Boolean, "Right icon visible")
    val isRightIconButtonVisible: Boolean = true

    @FigmaProperty(FigmaType.Boolean, "Center icon visible")
    val isCenterIconVisible: Boolean = true

    @FigmaProperty(FigmaType.Text, "Left icon description")
    val leftIconDescription: String = "Back"

    @FigmaProperty(FigmaType.Text, "Right icon description")
    val rightIconDescription: String = "Next"

    @FigmaProperty(FigmaType.Text, "Center icon description")
    val centerIconDescription: String = "Center icon"

    // --- DERIVED VALUES (NOT DIRECTLY FIGMA PROPS, BUT NEEDED FOR FULL API) ---

    // 1. leftIcon
    val leftIcon: DrawableResource
        get() = when (leftIconVariant) {
            LeftIconVariant.ArrowLeft -> Res.drawable.shared_ic_arrow_left
            LeftIconVariant.AngleRight -> Res.drawable.shared_ic_angle_right
            LeftIconVariant.None -> Res.drawable.shared_ic_arrow_left // default
        }

    // 2. rightIcon
    val rightIcon: DrawableResource?
        get() = when (rightIconVariant) {
            RightIconVariant.AngleRight -> Res.drawable.shared_ic_angle_right
            RightIconVariant.ArrowLeft -> Res.drawable.shared_ic_arrow_left
            RightIconVariant.None -> null
        }

    // 3. centerIcon
    val centerIcon: DrawableResource?
        get() = when (centerIconVariant) {
            CenterIconVariant.AngleRight -> Res.drawable.shared_ic_angle_right
            CenterIconVariant.ArrowLeft -> Res.drawable.shared_ic_arrow_left
            CenterIconVariant.None -> null
        }

    // 4. modifier
    val modifier: Modifier = Modifier

    // 5. containerPadding
    val containerPadding: PaddingValues? = null

    // 6. colors (null = use theme defaults)
    val backgroundColor: Color? = null
    val leftIconTint: Color? = null
    val rightIconTint: Color? = null
    val leftIconBackgroundColor: Color? = null
    val rightIconBackgroundColor: Color? = null

    // 7. iconButtonSize
    val iconButtonSize: Dp? = null

    // 8. callbacks
    val onLeftIconClick: () -> Unit = {}
    val onRightIconClick: () -> Unit = {}

    // --- THE COMPOSABLE SNIPPET (USES *ALL* PARAMETERS EXPLICITLY) ---

    @Composable
    fun Component() {
        CustomHeader(
            modifier = modifier,
            leftIcon = leftIcon,
            rightIcon = rightIcon,
            centerIcon = centerIcon,
            leftIconDescription = leftIconDescription,
            rightIconDescription = rightIconDescription,
            centerIconDescription = centerIconDescription,
            isLeftIconButtonVisible = isLeftIconButtonVisible,
            isRightIconButtonVisible = isRightIconButtonVisible,
            isCenterIconVisible = isCenterIconVisible,
            containerPadding = containerPadding,
            backgroundColor = backgroundColor,
            leftIconTint = leftIconTint,
            rightIconTint = rightIconTint,
            leftIconBackgroundColor = leftIconBackgroundColor,
            rightIconBackgroundColor = rightIconBackgroundColor,
            iconButtonSize = iconButtonSize,
            onLeftIconClick = onLeftIconClick,
            onRightIconClick = onRightIconClick
        )
    }
}
