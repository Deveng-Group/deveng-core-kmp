package core.presentation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import core.presentation.component.textfield.CustomTextField
import core.presentation.theme.CoreCustomDividerColor
import core.presentation.theme.LocalComponentTheme
import org.jetbrains.compose.resources.DrawableResource

@Composable
fun SearchField(
    // Row
    modifier: Modifier = Modifier,
    // Value and actions
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onSearchButtonClick: () -> Unit,
    // TextField
    searchBarHint: String,
    textFieldContainerModifier: Modifier = Modifier,
    textFieldModifier: Modifier = Modifier,
    textFieldShape: RoundedCornerShape = RoundedCornerShape(16.dp),
    textFieldBorder: BorderStroke = BorderStroke(1.dp, CoreCustomDividerColor),
    maxLines: Int = Int.MAX_VALUE,
    singleLine: Boolean = true,
    isEditable: Boolean = true,
    readOnly: Boolean = false,
    maxLength: Int = 254,
    keyboardType: KeyboardType = KeyboardType.Text,
    errorMessage: String? = null,
    title: String? = null,
    leadingSlot: Slot? = null,
    trailingSlot: Slot? = null,
    isTextCharCountVisible: Boolean = false,
    onDone: (() -> Unit)? = null,
    onFocusCleared: (() -> Unit)? = null,
    enabled: Boolean = true,
    requestFocus: Boolean = false,
    textStyle: TextStyle? = null,
    containerColor: Color? = null,
    disabledContainerColor: Color? = null,
    textColor: Color? = null,
    disabledTextColor: Color? = null,
    // Button customization
    isButtonEnabled: Boolean = true,
    buttonSize: Dp? = null,
    buttonShape: Shape? = null,
    buttonBackgroundColor: Color? = null,
    buttonIcon: DrawableResource,
    buttonIconDescription: String? = null,
    buttonIconTint: Color? = null,
    buttonShadowElevation: Dp? = null,
    // Layout
    isButtonAtEnd: Boolean = true
) {
    val componentTheme = LocalComponentTheme.current
    val searchFieldTheme = componentTheme.searchField
    val customTextFieldTheme = componentTheme.customTextField

    val finalButtonSize = buttonSize ?: searchFieldTheme.buttonSize
    val finalButtonShape = buttonShape ?: searchFieldTheme.buttonShape
    val finalButtonBackgroundColor = buttonBackgroundColor ?: searchFieldTheme.buttonBackgroundColor
    val finalButtonIconTint = buttonIconTint ?: searchFieldTheme.buttonIconTint
    val finalButtonShadowElevation = buttonShadowElevation ?: searchFieldTheme.buttonShadowElevation
    val finalTextFieldShape = textFieldShape
    val finalTextFieldBorder = textFieldBorder
    val finalTextStyle = textStyle ?: customTextFieldTheme.textStyle
    val finalContainerColor = containerColor ?: customTextFieldTheme.containerColor
    val finalDisabledContainerColor =
        disabledContainerColor ?: customTextFieldTheme.disabledContainerColor
    val finalTextColor = textColor ?: customTextFieldTheme.textColor
    val finalDisabledTextColor = disabledTextColor ?: customTextFieldTheme.disabledTextColor

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        val actionButton = @Composable {
            CustomIconButton(
                modifier = Modifier.size(finalButtonSize),
                isEnable = isButtonEnabled,
                shape = finalButtonShape,
                backgroundColor = finalButtonBackgroundColor,
                icon = buttonIcon,
                iconDescription = buttonIconDescription
                    ?: searchFieldTheme.defaultButtonIconDescription,
                iconTint = finalButtonIconTint,
                shadowElevation = finalButtonShadowElevation,
                onClick = onSearchButtonClick
            )
        }

        if (!isButtonAtEnd) {
            actionButton()
        }

        CustomTextField(
            containerModifier = textFieldContainerModifier.weight(1f),
            textFieldModifier = textFieldModifier,
            borderStroke = finalTextFieldBorder,
            value = searchText,
            hint = searchBarHint,
            shape = finalTextFieldShape,
            maxLines = maxLines,
            singleLine = singleLine,
            isEditable = isEditable,
            readOnly = readOnly,
            maxLength = maxLength,
            keyboardType = keyboardType,
            errorMessage = errorMessage,
            title = title,
            leadingSlot = leadingSlot,
            trailingSlot = trailingSlot,
            isTextCharCountVisible = isTextCharCountVisible,
            onDone = onDone,
            onFocusCleared = onFocusCleared,
            enabled = enabled,
            requestFocus = requestFocus,
            textStyle = finalTextStyle,
            containerColor = finalContainerColor,
            disabledContainerColor = finalDisabledContainerColor,
            textColor = finalTextColor,
            disabledTextColor = finalDisabledTextColor,
            onValueChange = onSearchTextChange
        )

        if (isButtonAtEnd) {
            actionButton()
        }
    }
}

