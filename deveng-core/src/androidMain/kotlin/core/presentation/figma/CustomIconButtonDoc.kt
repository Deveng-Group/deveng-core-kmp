package core.presentation.figma

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.figma.code.connect.Figma
import com.figma.code.connect.FigmaConnect
import com.figma.code.connect.FigmaProperty
import com.figma.code.connect.FigmaType
import core.presentation.component.CustomIconButton
import global.deveng.deveng_core.generated.resources.Res
import global.deveng.deveng_core.generated.resources.shared_ic_angle_right
import global.deveng.deveng_core.generated.resources.shared_ic_arrow_left
import global.deveng.deveng_core.generated.resources.shared_ic_arrow_next
import org.jetbrains.compose.resources.DrawableResource

@FigmaConnect(
    url = "https://www.figma.com/design/sJoAsKB4qqqrwvHRlowppo/Design-System?node-id=2-13&t=JntFNGouWhSDj0EP-0"
)
class CustomIconButtonDoc {
    // --- INTERNAL ENUMS MIRRORING FIGMA VARIANTS / PROPS ---

    enum class IconVariant { ArrowLeft, ArrowNext, AngleRight }

    enum class ShapeVariant { Circle, Rounded, Square }

    enum class ElevationLevel { None, Low, Medium, High }

    // --- FIGMA-PROPERTIES (MAPPED TO FIGMA COMPONENT PROPS) ---

    @FigmaProperty(FigmaType.Enum, "Icon")
    val iconVariant: IconVariant = Figma.mapping(
        "Arrow left" to IconVariant.ArrowLeft,
        "Arrow next" to IconVariant.ArrowNext,
        "Angle right" to IconVariant.AngleRight
    )

    @FigmaProperty(FigmaType.Text, "Icon description")
    val iconDescription: String = "Icon button"

    @FigmaProperty(FigmaType.Boolean, "Enabled")
    val isEnable: Boolean = true

    @FigmaProperty(FigmaType.Enum, "Shape")
    val shapeVariant: ShapeVariant = Figma.mapping(
        "Circle" to ShapeVariant.Circle,
        "Rounded" to ShapeVariant.Rounded,
        "Square" to ShapeVariant.Square
    )

    @FigmaProperty(FigmaType.Enum, "Elevation")
    val elevationLevel: ElevationLevel = Figma.mapping(
        "None" to ElevationLevel.None,
        "Low" to ElevationLevel.Low,
        "Medium" to ElevationLevel.Medium,
        "High" to ElevationLevel.High
    )

    // --- DERIVED VALUES (NOT DIRECTLY FIGMA PROPS, BUT NEEDED FOR FULL API) ---

    // 1. modifier
    val modifier: Modifier = Modifier

    // 2. icon
    val icon: DrawableResource
        get() = when (iconVariant) {
            IconVariant.ArrowLeft -> Res.drawable.shared_ic_arrow_left
            IconVariant.ArrowNext -> Res.drawable.shared_ic_arrow_next
            IconVariant.AngleRight -> Res.drawable.shared_ic_angle_right
        }

    // 3. iconModifier
    val iconModifier: Modifier = Modifier

    // 4. buttonSize
    val buttonSize: Dp? = null

    // 5. shape
    val shape: Shape?
        get() = when (shapeVariant) {
            ShapeVariant.Circle -> CircleShape
            ShapeVariant.Rounded -> RoundedCornerShape(8.dp)
            ShapeVariant.Square -> RoundedCornerShape(0.dp)
        }

    // 6. shadowElevation
    val shadowElevation: Dp
        get() = when (elevationLevel) {
            ElevationLevel.None -> 0.dp
            ElevationLevel.Low -> 1.dp
            ElevationLevel.Medium -> 3.dp
            ElevationLevel.High -> 6.dp
        }

    // 7. colors (null = use theme defaults)
    val backgroundColor: Color? = null
    val iconTint: Color? = null

    // 8. callback
    val onClick: () -> Unit = {}

    // --- THE COMPOSABLE SNIPPET (USES *ALL* PARAMETERS EXPLICITLY) ---

    @Composable
    fun Component() {
        CustomIconButton(
            modifier = modifier,
            isEnable = isEnable,
            buttonSize = buttonSize,
            iconModifier = iconModifier,
            backgroundColor = backgroundColor,
            shape = shape,
            icon = icon,
            iconDescription = iconDescription,
            iconTint = iconTint,
            shadowElevation = shadowElevation,
            onClick = onClick
        )
    }
}
