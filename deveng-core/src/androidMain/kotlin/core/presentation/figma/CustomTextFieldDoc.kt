package core.presentation.figma

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.figma.code.connect.Figma
import com.figma.code.connect.FigmaConnect
import com.figma.code.connect.FigmaProperty
import com.figma.code.connect.FigmaType
import core.presentation.component.Slot
import core.presentation.component.textfield.CustomTextField
import global.deveng.deveng_core.generated.resources.Res
import global.deveng.deveng_core.generated.resources.shared_ic_password_invisible
import org.jetbrains.compose.resources.painterResource

@FigmaConnect(
    url = "https://www.figma.com/design/sJoAsKB4qqqrwvHRlowppo/Design-System?node-id=2-8&t=JntFNGouWhSDj0EP-0"
)
class CustomTextFieldDoc {
    // --- INTERNAL ENUMS MIRRORING FIGMA VARIANTS / PROPS ---

    enum class KeyboardTypeVariant { Text, Password, Number, Email, Phone, Uri }

    enum class ShapeVariant { Rounded, Pill, Square }

    enum class SlotPresence { None, Leading, Trailing, Suffix, TitleTrailing, Multiple }

    // --- FIGMA-PROPERTIES (MAPPED TO FIGMA COMPONENT PROPS) ---

    @FigmaProperty(FigmaType.Text, "Value")
    val value: String = ""

    @FigmaProperty(FigmaType.Text, "Hint")
    val hint: String = "Enter text"

    @FigmaProperty(FigmaType.Text, "Title")
    val title: String? = null

    @FigmaProperty(FigmaType.Text, "Error message")
    val errorMessage: String? = null

    @FigmaProperty(FigmaType.Boolean, "Enabled")
    val enabled: Boolean = true

    @FigmaProperty(FigmaType.Boolean, "Read only")
    val readOnly: Boolean = false

    @FigmaProperty(FigmaType.Boolean, "Password visible")
    val isPasswordVisible: Boolean = false

    @FigmaProperty(FigmaType.Boolean, "Show character count")
    val isTextCharCountVisible: Boolean = false

    @FigmaProperty(FigmaType.Enum, "Keyboard type")
    val keyboardTypeVariant: KeyboardTypeVariant = Figma.mapping(
        "Text" to KeyboardTypeVariant.Text,
        "Password" to KeyboardTypeVariant.Password,
        "Decimal" to KeyboardTypeVariant.Number,
        "Email" to KeyboardTypeVariant.Email,
        "Phone" to KeyboardTypeVariant.Phone,
        "Uri" to KeyboardTypeVariant.Uri
    )

    @FigmaProperty(FigmaType.Enum, "Shape")
    val shapeVariant: ShapeVariant = Figma.mapping(
        "Rounded" to ShapeVariant.Rounded,
        "Pill" to ShapeVariant.Pill,
        "Square" to ShapeVariant.Square
    )

    @FigmaProperty(FigmaType.Boolean, "Border active")
    val isBorderActive: Boolean = true

    @FigmaProperty(FigmaType.Enum, "Slots")
    val slotPresence: SlotPresence = Figma.mapping(
        "None" to SlotPresence.None,
        "Leading" to SlotPresence.Leading,
        "Trailing" to SlotPresence.Trailing,
        "Suffix" to SlotPresence.Suffix,
        "Title trailing" to SlotPresence.TitleTrailing,
        "Multiple" to SlotPresence.Multiple
    )

    @FigmaProperty(FigmaType.Text, "Max length")
    val maxLength: Int = 254

    // --- DERIVED VALUES (NOT DIRECTLY FIGMA PROPS, BUT NEEDED FOR FULL API) ---

    // 1. keyboardType
    val keyboardType: KeyboardType
        get() = when (keyboardTypeVariant) {
            KeyboardTypeVariant.Text -> KeyboardType.Text
            KeyboardTypeVariant.Password -> KeyboardType.Password
            KeyboardTypeVariant.Number -> KeyboardType.Number
            KeyboardTypeVariant.Email -> KeyboardType.Email
            KeyboardTypeVariant.Phone -> KeyboardType.Phone
            KeyboardTypeVariant.Uri -> KeyboardType.Uri
        }

    // 2. keyboardOptions
    val keyboardOptions: KeyboardOptions
        get() = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = ImeAction.Done
        )

    // 3. shape
    val shape: CornerBasedShape?
        get() = when (shapeVariant) {
            ShapeVariant.Rounded -> RoundedCornerShape(8.dp)
            ShapeVariant.Pill -> RoundedCornerShape(50.dp)
            ShapeVariant.Square -> RoundedCornerShape(0.dp)
        }

    // 4. slots
    val leadingSlot: Slot?
        get() = when (slotPresence) {
            SlotPresence.Leading, SlotPresence.Multiple -> {
                {
                    androidx.compose.material3.Icon(
                        painter = painterResource(Res.drawable.shared_ic_password_invisible),
                        contentDescription = null
                    )
                }
            }
            else -> null
        }

    val trailingSlot: Slot?
        get() = when (slotPresence) {
            SlotPresence.Trailing, SlotPresence.Multiple -> {
                {
                    androidx.compose.material3.Icon(
                        painter = painterResource(Res.drawable.shared_ic_password_invisible),
                        contentDescription = null
                    )
                }
            }
            else -> null
        }

    val suffixSlot: Slot?
        get() = when (slotPresence) {
            SlotPresence.Suffix, SlotPresence.Multiple -> {
                {
                    androidx.compose.material3.Icon(
                        painter = painterResource(Res.drawable.shared_ic_password_invisible),
                        contentDescription = null
                    )
                }
            }
            else -> null
        }

    val titleTrailingSlot: Slot?
        get() = when (slotPresence) {
            SlotPresence.TitleTrailing, SlotPresence.Multiple -> {
                {
                    androidx.compose.material3.Icon(
                        painter = painterResource(Res.drawable.shared_ic_password_invisible),
                        contentDescription = null
                    )
                }
            }
            else -> null
        }

    // 5. password toggle
    val isPasswordToggleDisplayed: Boolean
        get() = keyboardTypeVariant == KeyboardTypeVariant.Password

    // 6. visualTransformation
    val visualTransformation: VisualTransformation
        get() = VisualTransformation.None

    // 7. modifiers
    val textFieldModifier: Modifier = Modifier
    val containerModifier: Modifier = Modifier

    // 8. text styles (null = use theme defaults)
    val textStyle: TextStyle? = null
    val titleTextStyle: TextStyle? = null
    val charCountTextStyle: TextStyle? = null
    val hintTextStyle: TextStyle? = null
    val errorTextStyle: TextStyle? = null

    // 9. colors (null = use theme defaults)
    val containerColor: Color? = null
    val disabledContainerColor: Color? = null
    val textColor: Color? = null
    val disabledTextColor: Color? = null
    val readOnlyTextColor: Color? = null
    val titleColor: Color? = null
    val focusedBorderColor: Color? = null
    val unfocusedBorderColor: Color? = null
    val cursorColor: Color? = null

    // 10. border
    val borderStroke: BorderStroke? = null
    val focusedBorderWidth: Dp? = null
    val unfocusedBorderWidth: Dp? = null

    // 11. other properties
    val maxLines: Int = Int.MAX_VALUE
    val singleLine: Boolean = true
    val isEditable: Boolean = true
    val inlineSuffix: String? = null
    val requestFocus: Boolean = false

    // 12. callbacks
    val onValueChange: (String) -> Unit = {}
    val onPasswordToggleClick: (Boolean) -> Unit = {}
    val onDone: (() -> Unit)? = null
    val onFocusCleared: (() -> Unit)? = null

    // --- THE COMPOSABLE SNIPPET (USES *ALL* PARAMETERS EXPLICITLY) ---

    @Composable
    fun Component() {
        CustomTextField(
            textFieldModifier = textFieldModifier,
            value = value,
            hint = hint,
            containerModifier = containerModifier,
            leadingSlot = leadingSlot,
            trailingSlot = trailingSlot,
            suffixSlot = suffixSlot,
            titleTrailingSlot = titleTrailingSlot,
            textStyle = textStyle,
            isBorderActive = isBorderActive,
            shape = shape,
            borderStroke = borderStroke,
            focusedBorderWidth = focusedBorderWidth,
            unfocusedBorderWidth = unfocusedBorderWidth,
            maxLines = maxLines,
            singleLine = singleLine,
            isEditable = isEditable,
            readOnly = readOnly,
            maxLength = maxLength,
            keyboardType = keyboardType,
            keyboardOptions = keyboardOptions,
            isPasswordToggleDisplayed = isPasswordToggleDisplayed,
            isPasswordVisible = isPasswordVisible,
            onPasswordToggleClick = onPasswordToggleClick,
            inlineSuffix = inlineSuffix,
            errorMessage = errorMessage,
            title = title,
            titleColor = titleColor,
            titleTextStyle = titleTextStyle,
            charCountTextStyle = charCountTextStyle,
            isTextCharCountVisible = isTextCharCountVisible,
            onDone = onDone,
            onFocusCleared = onFocusCleared,
            enabled = enabled,
            requestFocus = requestFocus,
            containerColor = containerColor,
            disabledContainerColor = disabledContainerColor,
            textColor = textColor,
            disabledTextColor = disabledTextColor,
            readOnlyTextColor = readOnlyTextColor,
            hintTextStyle = hintTextStyle,
            errorTextStyle = errorTextStyle,
            focusedBorderColor = focusedBorderColor,
            unfocusedBorderColor = unfocusedBorderColor,
            cursorColor = cursorColor,
            visualTransformation = visualTransformation,
            onValueChange = onValueChange
        )
    }
}
