package core.presentation.figma

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
    url = "https://www.figma.com/design/sJoAsKB4qqqrwvHRlowppo/Design-System?node-id=2-7&t=JntFNGouWhSDj0EP-0&m=dev"
)
class CustomButtonDoc {
    // --- INTERNAL ENUMS MIRRORING FIGMA VARIANTS / PROPS ---

    enum class Size { Small, Medium, Large }

    enum class ShapeVariant { Rounded, Pill, Square }

    enum class ColorPreset { Primary, Secondary, Surface, Transparent }

    enum class IconPosition { None, Leading, Trailing, Both }

    enum class Alignment { Center, Start, SpaceBetween }

    enum class ElevationLevel { None, Low, Medium, High }

    // --- FIGMA-PROPERTIES (MAPPED TO FIGMA COMPONENT PROPS) ---

    @FigmaProperty(FigmaType.Text, "Label")
    val text: String = "Custom Button"

    @FigmaProperty(FigmaType.Boolean, "Enabled")
    val enabled: Boolean = true

    @FigmaProperty(FigmaType.Enum, "Size")
    val size: Size = Figma.mapping(
        "Small" to Size.Small,
        "Medium" to Size.Medium,
        "Large" to Size.Large
    )

    // Which shape variant the designer picks
    @FigmaProperty(FigmaType.Enum, "Shape")
    val shapeVariant: ShapeVariant = Figma.mapping(
        "Rounded" to ShapeVariant.Rounded,
        "Pill" to ShapeVariant.Pill,
        "Square" to ShapeVariant.Square
    )

    // High-level color preset; we derive the actual 4 Color? params from this
    @FigmaProperty(FigmaType.Enum, "Color preset")
    val colorPreset: ColorPreset = Figma.mapping(
        "Primary" to ColorPreset.Primary,
        "Secondary" to ColorPreset.Secondary,
        "Surface" to ColorPreset.Surface,
        "Transparent" to ColorPreset.Transparent
    )

    // Optional override: let designers explicitly mark button as "Disabled (visual)"
    // even if Enabled is true, if you need that. For now, we just use Enabled directly.
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

    // Single prop to decide where icons appear
    @FigmaProperty(FigmaType.Enum, "Icons")
    val iconPosition: IconPosition = Figma.mapping(
        "None" to IconPosition.None,
        "Leading" to IconPosition.Leading,
        "Trailing" to IconPosition.Trailing,
        "Both" to IconPosition.Both
    )

    // Optional text for content descriptions
    @FigmaProperty(FigmaType.Text, "Leading icon description")
    val leadingIconContentDescription: String? = null

    @FigmaProperty(FigmaType.Text, "Trailing icon description")
    val trailingIconContentDescription: String? = null

    // Tints as enums mapped to actual colors
    @FigmaProperty(FigmaType.Enum, "Icon tint")
    val iconTint: Color = Figma.mapping(
        "Default" to Color.Black,
        "On primary" to CoreOnPrimaryColor,
        "On surface" to CoreOnSurfaceColor
    )

    // --- DERIVED VALUES (NOT DIRECTLY FIGMA PROPS, BUT NEEDED FOR FULL API) ---

    // 1. modifier (size)
    val modifier: Modifier
        get() = when (size) {
            Size.Small -> Modifier
                .height(40.dp)
                .width(140.dp)
            Size.Medium -> Modifier
                .height(50.dp)
                .width(200.dp)
            Size.Large -> Modifier
                .height(56.dp)
                .width(240.dp)
        }

    // 2. shape (from ShapeVariant)
    val shape: CornerBasedShape
        get() = when (shapeVariant) {
            ShapeVariant.Rounded -> RoundedCornerShape(8.dp)
            ShapeVariant.Pill -> RoundedCornerShape(50.dp)
            ShapeVariant.Square -> RoundedCornerShape(0.dp)
        }

    // 3. colors (from ColorPreset)
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

    // 4. elevation (Dp value - will be converted to ButtonElevation in Component)
    val elevationDp: Dp
        get() = when (elevationLevel) {
            ElevationLevel.None -> 0.dp
            ElevationLevel.Low -> 1.dp
            ElevationLevel.Medium -> 3.dp
            ElevationLevel.High -> 6.dp
        }

    // 5. content alignment
    val contentArrangement: Arrangement.Horizontal
        get() = when (alignment) {
            Alignment.Center -> Arrangement.Center
            Alignment.Start -> Arrangement.Start
            Alignment.SpaceBetween -> Arrangement.SpaceBetween
        }

    // 6. icons + modifiers
    val leadingIcon: DrawableResource?
        get() = when (iconPosition) {
            IconPosition.Leading, IconPosition.Both -> Res.drawable.shared_ic_arrow_left
            IconPosition.None, IconPosition.Trailing -> null
        }

    val trailingIcon: DrawableResource?
        get() = when (iconPosition) {
            IconPosition.Trailing, IconPosition.Both -> Res.drawable.shared_ic_arrow_next
            IconPosition.None, IconPosition.Leading -> null
        }

    // In case you ever want to vary these later, they exist explicitly.
    val leadingIconModifier: Modifier = Modifier

    val trailingIconModifier: Modifier = Modifier

    // 7. text style / text modifier – you currently derive textStyle from theme.
    // Here we just keep them explicit so they show up in the snippet.
    val textStyle: TextStyle? = null

    val textModifier: Modifier = Modifier

    // 8. onClick – Code Connect/Dev Mode doesn't care, but it's part of your API.
    val onClick: () -> Unit = {}

    // --- THE COMPOSABLE SNIPPET (USES *ALL* PARAMETERS EXPLICITLY) ---

    @Composable
    fun Component() {
        val elevation = ButtonDefaults.buttonElevation(defaultElevation = elevationDp)

        CustomButton(
            modifier = modifier,
            text = text,
            textStyle = textStyle,
            textModifier = textModifier,
            enabled = enabled,
            shape = shape,
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

