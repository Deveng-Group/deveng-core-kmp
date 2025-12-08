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
    url = "https://www.figma.com/design/sJoAsKB4qqqrwvHRlowppo/Design-System?node-id=36-34&m=dev"
)
class PickerFieldDoc {
    // --- INTERNAL ENUMS MIRRORING FIGMA VARIANTS / PROPS ---

    enum class ShapeVariant { Rounded, Pill, Square }

    enum class SlotPresence { None, Leading, Trailing, Both }

    // --- FIGMA-PROPERTIES (MAPPED TO FIGMA COMPONENT PROPS) ---

    @FigmaProperty(FigmaType.Text, "Text")
    val text: String? = null
    // Selected text to display. If null or empty, shows hint text instead.
    // Text is truncated with ellipsis if it exceeds available width.

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

    @FigmaProperty(FigmaType.Text, "Title trailing icon")
    val titleTrailingIconText: String? = null
    // Optional icon displayed after the title. When provided, creates a titleTrailingIcon slot.

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
    // Leading slot: Optional icon displayed at the start of the field content.
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

    // Trailing slot: Icon displayed at the end of the field content.
    // Default: Right arrow icon (shared_ic_angle_right) is always shown unless custom trailing slot is provided.
    // When slotPresence is Trailing or Both, a custom icon is used; otherwise, the default arrow is shown.
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
            else -> null // Component provides default trailing icon when null
        }

    // Title trailing icon: Optional icon displayed after the title text.
    // Only shown when titleTrailingIconText is not null.
    val titleTrailingIcon: @Composable (() -> Unit)?
        get() = titleTrailingIconText?.let {
            {
                Icon(
                    painter = painterResource(Res.drawable.shared_ic_angle_right),
                    contentDescription = null,
                    tint = CoreCustomBlackColor
                )
            }
        }

    // 4. Field dimensions and behavior
    // Fixed height: 56dp (applied internally by the component)
    // Text overflow: When text exceeds available width, it's truncated with ellipsis (...)
    // Text display: Shows selected text if available, otherwise shows hint text with hint styling

    // 5. colors (null = use theme defaults)
    val titleColor: Color? = null
    val enabledBackGroundColor: Color? = null
    val enabledBorderColor: Color? = null
    val enabledTextColor: Color? = null
    val enabledBorderWidth: Dp? = null
    val hintTextColor: Color? = null
    val disabledBackGroundColor: Color? = null
    val disabledBorderColor: Color? = null
    val disabledTextColor: Color? = null

    // 6. text styles (null = use theme defaults)
    val titleTextStyle: TextStyle? = null
    val textStyle: TextStyle? = null
    val hintTextStyle: TextStyle? = null
    val errorTextStyle: TextStyle? = null

    // 7. callback
    // onClick: Invoked when the field is clicked. Typically opens a picker dialog or selection screen.
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
