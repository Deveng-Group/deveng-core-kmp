package core.presentation.figma

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.figma.code.connect.Figma
import com.figma.code.connect.FigmaConnect
import com.figma.code.connect.FigmaProperty
import com.figma.code.connect.FigmaType
import core.presentation.component.CustomButton
import core.presentation.theme.CoreOnPrimaryColor
import core.presentation.theme.CoreOnSurfaceColor
import core.presentation.theme.CorePrimaryColor
import core.presentation.theme.CoreSecondaryColor
import core.presentation.theme.CoreSurfaceColor
import global.deveng.deveng_core.generated.resources.Res
import global.deveng.deveng_core.generated.resources.shared_ic_arrow_left
import global.deveng.deveng_core.generated.resources.shared_ic_arrow_next
import org.jetbrains.compose.resources.DrawableResource

@FigmaConnect(
    url = "https://www.figma.com/design/sJoAsKB4qqqrwvHRlowppo/Design-System?node-id=57-85&m=dev"
)
class CustomButtonDoc {

    // --- INTERNAL ENUMS MIRRORING FIGMA VARIANTS / PROPS ---

    enum class ColorPreset { Primary, Secondary, Surface, Transparent }

    enum class IconPosition { None, Leading, Trailing, Both }

    enum class Alignment { Center, Start, SpaceBetween }

    enum class ElevationLevel { None, Low, Medium, High }

    // --- FIGMA-PROPERTIES (MAPPED TO FIGMA COMPONENT PROPS) ---

    @FigmaProperty(FigmaType.Text, "Label")
    val text: String = "Custom Button"

    @FigmaProperty(FigmaType.Boolean, "Enabled")
    val enabled: Boolean = true

    @FigmaProperty(FigmaType.Enum, "Color preset")
    val colorPreset: ColorPreset = Figma.mapping(
        "Primary" to ColorPreset.Primary,
        "Secondary" to ColorPreset.Secondary,
        "Surface" to ColorPreset.Surface,
        "Transparent" to ColorPreset.Transparent
    )

    @FigmaProperty(FigmaType.Enum, "Elevation")
    val elevationLevel: ElevationLevel = Figma.mapping(
        "None" to ElevationLevel.None,
        "Low" to ElevationLevel.Low,
        "Medium" to ElevationLevel.Medium,
        "High" to ElevationLevel.High
    )

    @FigmaProperty(FigmaType.Enum, "Content alignment")
    val alignment: Alignment = Figma.mapping(
        "Center" to Alignment.Center,
        "Start" to Alignment.Start,
        "Space between" to Alignment.SpaceBetween
    )

    @FigmaProperty(FigmaType.Enum, "Icons")
    val iconPosition: IconPosition = Figma.mapping(
        "None" to IconPosition.None,
        "Leading" to IconPosition.Leading,
        "Trailing" to IconPosition.Trailing,
        "Both" to IconPosition.Both
    )

    @FigmaProperty(FigmaType.Enum, "Icon tint")
    val iconTint: Color = Figma.mapping(
        "Default" to Color.Black,
        "On primary" to CoreOnPrimaryColor,
        "On surface" to CoreOnSurfaceColor
    )

    @FigmaProperty(FigmaType.Text, "Leading icon description")
    val leadingIconContentDescription: String? = null

    @FigmaProperty(FigmaType.Text, "Trailing icon description")
    val trailingIconContentDescription: String? = null

    // --- DERIVED VALUES (FROM ABOVE PROPS) ---

    val containerColor: Color?
        get() = when (colorPreset) {
            ColorPreset.Primary -> CorePrimaryColor
            ColorPreset.Secondary -> CoreSecondaryColor
            ColorPreset.Surface -> CoreSurfaceColor
            ColorPreset.Transparent -> Color.Transparent
        }

    val contentColor: Color?
        get() = when (colorPreset) {
            ColorPreset.Primary,
            ColorPreset.Secondary -> CoreOnPrimaryColor

            ColorPreset.Surface,
            ColorPreset.Transparent -> CoreOnSurfaceColor
        }

    val disabledContainerColor: Color?
        get() = containerColor?.copy(alpha = 0.4f)

    val disabledContentColor: Color?
        get() = contentColor?.copy(alpha = 0.4f)

    val elevationDp: Dp
        get() = when (elevationLevel) {
            ElevationLevel.None -> 0.dp
            ElevationLevel.Low -> 1.dp
            ElevationLevel.Medium -> 3.dp
            ElevationLevel.High -> 6.dp
        }

    val contentArrangement: Arrangement.Horizontal
        get() = when (alignment) {
            Alignment.Center -> Arrangement.Center
            Alignment.Start -> Arrangement.Start
            Alignment.SpaceBetween -> Arrangement.SpaceBetween
        }

    val leadingIcon: DrawableResource?
        get() = when (iconPosition) {
            IconPosition.Leading,
            IconPosition.Both -> Res.drawable.shared_ic_arrow_left

            IconPosition.None,
            IconPosition.Trailing -> null
        }

    val trailingIcon: DrawableResource?
        get() = when (iconPosition) {
            IconPosition.Trailing,
            IconPosition.Both -> Res.drawable.shared_ic_arrow_next

            IconPosition.None,
            IconPosition.Leading -> null
        }

    val leadingIconModifier: Modifier = Modifier
    val trailingIconModifier: Modifier = Modifier
    val textStyle: TextStyle? = null
    val textModifier: Modifier = Modifier
    val onClick: () -> Unit = {}

    // --- FINAL COMPOSABLE USED BY CODE CONNECT ---

    @Composable
    fun Component() {
        val elevation = ButtonDefaults.buttonElevation(defaultElevation = elevationDp)

        CustomButton(
            text = text,
            textStyle = textStyle,
            textModifier = textModifier,
            enabled = enabled,
            containerColor = containerColor,
            disabledContainerColor = disabledContainerColor,
            disabledContentColor = disabledContentColor,
            contentColor = contentColor,
            elevation = elevation,
            trailingIconModifier = trailingIconModifier,
            trailingIcon = trailingIcon,
            trailingIconTint = iconTint,
            trailingIconContentDescription = trailingIconContentDescription,
            leadingIconModifier = leadingIconModifier,
            leadingIcon = leadingIcon,
            leadingIconTint = iconTint,
            leadingIconContentDescription = leadingIconContentDescription,
            contentArrangement = contentArrangement,
            onClick = onClick
        )
    }
}
