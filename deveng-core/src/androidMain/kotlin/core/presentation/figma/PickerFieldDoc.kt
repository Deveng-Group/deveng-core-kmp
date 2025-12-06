package core.presentation.figma

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
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
import core.presentation.component.PickerField
import core.presentation.theme.CoreCustomBlackColor
import global.deveng.deveng_core.generated.resources.Res
import global.deveng.deveng_core.generated.resources.shared_content_desc_icon_direction
import global.deveng.deveng_core.generated.resources.shared_ic_angle_right
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@FigmaConnect(
    url = "https://www.figma.com/design/sJoAsKB4qqqrwvHRlowppo/Design-System?node-id=2-10&t=JntFNGouWhSDj0EP-0"
)
class PickerFieldDoc {
    // --- INTERNAL ENUMS MIRRORING FIGMA VARIANTS / PROPS ---

    enum class ShapeVariant { Rounded, Pill, Square }

    enum class SlotPresence { None, Leading, Trailing, Both }

    // --- FIGMA-PROPERTIES (MAPPED TO FIGMA COMPONENT PROPS) ---

    @FigmaProperty(FigmaType.Text, "Text")
    val text: String? = null

    @FigmaProperty(FigmaType.Text, "Hint")
    val hint: String = "Select option"

    @FigmaProperty(FigmaType.Text, "Title")
    val title: String? = null

    @FigmaProperty(FigmaType.Text, "Error message")
    val errorMessage: String? = null

    @FigmaProperty(FigmaType.Boolean, "Enabled")
    val isEnabled: Boolean = true

    @FigmaProperty(FigmaType.Enum, "Shape")
    val shapeVariant: ShapeVariant = Figma.mapping(
        "Rounded" to ShapeVariant.Rounded,
        "Pill" to ShapeVariant.Pill,
        "Square" to ShapeVariant.Square
    )

    @FigmaProperty(FigmaType.Enum, "Slots")
    val slotPresence: SlotPresence = Figma.mapping(
        "None" to SlotPresence.None,
        "Leading" to SlotPresence.Leading,
        "Trailing" to SlotPresence.Trailing,
        "Both" to SlotPresence.Both
    )

    // --- DERIVED VALUES (NOT DIRECTLY FIGMA PROPS, BUT NEEDED FOR FULL API) ---

    // 1. modifier
    val modifier: Modifier = Modifier

    // 2. shape
    val shape: CornerBasedShape?
        get() = when (shapeVariant) {
            ShapeVariant.Rounded -> RoundedCornerShape(8.dp)
            ShapeVariant.Pill -> RoundedCornerShape(50.dp)
            ShapeVariant.Square -> RoundedCornerShape(0.dp)
        }

    // 3. slots
    val leadingSlot: @Composable (() -> Unit)?
        get() = when (slotPresence) {
            SlotPresence.Leading, SlotPresence.Both -> {
                {
                    Icon(
                        painter = painterResource(Res.drawable.shared_ic_angle_right),
                        contentDescription = null,
                        tint = CoreCustomBlackColor
                    )
                }
            }
            else -> null
        }

    val trailingSlot: @Composable (() -> Unit)?
        get() = when (slotPresence) {
            SlotPresence.Trailing, SlotPresence.Both -> {
                {
                    Icon(
                        painter = painterResource(Res.drawable.shared_ic_angle_right),
                        contentDescription = stringResource(Res.string.shared_content_desc_icon_direction),
                        tint = CoreCustomBlackColor
                    )
                }
            }
            else -> {
                {
                    Icon(
                        painter = painterResource(Res.drawable.shared_ic_angle_right),
                        contentDescription = stringResource(Res.string.shared_content_desc_icon_direction),
                        tint = CoreCustomBlackColor
                    )
                }
            }
        }

    val titleTrailingIcon: @Composable (() -> Unit)? = null

    // 4. colors (null = use theme defaults)
    val titleColor: Color? = null
    val enabledBackGroundColor: Color? = null
    val enabledBorderColor: Color? = null
    val enabledTextColor: Color? = null
    val enabledBorderWidth: Dp? = null
    val hintTextColor: Color? = null
    val disabledBackGroundColor: Color? = null
    val disabledBorderColor: Color? = null
    val disabledTextColor: Color? = null

    // 5. text styles (null = use theme defaults)
    val titleTextStyle: TextStyle? = null
    val textStyle: TextStyle? = null
    val hintTextStyle: TextStyle? = null
    val errorTextStyle: TextStyle? = null

    // 6. callback
    val onClick: () -> Unit = {}

    // --- THE COMPOSABLE SNIPPET (USES *ALL* PARAMETERS EXPLICITLY) ---

    @Composable
    fun Component() {
        PickerField(
            modifier = modifier,
            isEnabled = isEnabled,
            text = text,
            hint = hint,
            title = title,
            titleColor = titleColor,
            titleTrailingIcon = titleTrailingIcon,
            trailingSlot = trailingSlot,
            leadingSlot = leadingSlot,
            errorMessage = errorMessage,
            shape = shape,
            enabledBackGroundColor = enabledBackGroundColor,
            enabledBorderColor = enabledBorderColor,
            enabledTextColor = enabledTextColor,
            enabledBorderWidth = enabledBorderWidth,
            hintTextColor = hintTextColor,
            disabledBackGroundColor = disabledBackGroundColor,
            disabledBorderColor = disabledBorderColor,
            disabledTextColor = disabledTextColor,
            titleTextStyle = titleTextStyle,
            textStyle = textStyle,
            hintTextStyle = hintTextStyle,
            errorTextStyle = errorTextStyle,
            onClick = onClick
        )
    }
}
