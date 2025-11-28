package core.presentation.component.textfield

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import core.presentation.component.RoundedSurface
import core.presentation.component.Slot
import core.presentation.theme.AppTheme
import core.presentation.theme.CustomGrayHintColor
import core.presentation.theme.LocalComponentTheme
import core.util.EMPTY
import global.deveng.deveng_core.generated.resources.Res
import global.deveng.deveng_core.generated.resources.char_count
import global.deveng.deveng_core.generated.resources.shared_cont_desc_icon_password_invisible
import global.deveng.deveng_core.generated.resources.shared_cont_desc_icon_password_visible
import global.deveng.deveng_core.generated.resources.shared_ic_password_invisible
import global.deveng.deveng_core.generated.resources.shared_ic_password_visible
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CustomTextField(
    textFieldModifier: Modifier = Modifier,
    value: String,
    hint: String = String.EMPTY,
    containerModifier: Modifier = Modifier,
    leadingSlot: Slot? = null,
    trailingSlot: Slot? = null,
    suffixSlot: Slot? = null,
    titleTrailingSlot: Slot? = null,
    textStyle: TextStyle? = null,
    isBorderActive: Boolean? = null,
    shape: CornerBasedShape? = null,
    borderStroke: BorderStroke? = null,
    focusedBorderWidth: Dp? = null,
    unfocusedBorderWidth: Dp? = null,
    maxLines: Int = Int.MAX_VALUE,
    singleLine: Boolean = true,
    isEditable: Boolean = true,
    readOnly: Boolean = false,
    maxLength: Int = 254,
    keyboardType: KeyboardType = KeyboardType.Text,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        keyboardType = keyboardType, imeAction = ImeAction.Done
    ),
    isPasswordToggleDisplayed: Boolean = keyboardType == KeyboardType.Password,
    isPasswordVisible: Boolean = false,
    onPasswordToggleClick: (Boolean) -> Unit = {},
    inlineSuffix: String? = null,
    errorMessage: String? = null,
    title: String? = null,
    titleColor: Color? = null,
    titleTextStyle: TextStyle? = null,
    charCountTextStyle: TextStyle? = null,
    isTextCharCountVisible: Boolean = false,
    onDone: (() -> Unit)? = null,
    onFocusCleared: (() -> Unit)? = null,
    enabled: Boolean = true,
    requestFocus: Boolean = false,
    containerColor: Color? = null,
    disabledContainerColor: Color? = null,
    textColor: Color? = null,
    disabledTextColor: Color? = null,
    readOnlyTextColor: Color? = null,
    hintTextStyle: TextStyle? = null,
    errorTextStyle: TextStyle? = null,
    focusedBorderColor: Color? = null,
    unfocusedBorderColor: Color? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onValueChange: (String) -> Unit
) {
    val componentTheme = LocalComponentTheme.current
    val customTextFieldTheme = componentTheme.customTextField
    val finalTextStyle = textStyle ?: customTextFieldTheme.textStyle
    val finalTitleTextStyleBase = titleTextStyle ?: customTextFieldTheme.titleTextStyle
    val finalTitleTextStyle = titleColor?.let { finalTitleTextStyleBase.copy(color = it) }
        ?: finalTitleTextStyleBase
    val finalCharCountTextStyle =
        charCountTextStyle ?: customTextFieldTheme.charCountTextStyle
    val finalHintStyle = hintTextStyle ?: customTextFieldTheme.hintTextStyle
    val finalErrorStyle = errorTextStyle ?: customTextFieldTheme.errorTextStyle
    val finalShape = shape ?: customTextFieldTheme.containerShape
    val finalBorderStroke = borderStroke ?: customTextFieldTheme.borderStroke
    val borderEnabled = isBorderActive ?: customTextFieldTheme.isBorderActive
    val finalContainerColor = containerColor ?: customTextFieldTheme.containerColor
    val finalDisabledContainerColor =
        disabledContainerColor ?: customTextFieldTheme.disabledContainerColor
    val finalEnabledTextColor = textColor ?: customTextFieldTheme.textColor
    val finalDisabledTextColor = disabledTextColor ?: customTextFieldTheme.disabledTextColor
    val finalReadOnlyTextColor = readOnlyTextColor ?: customTextFieldTheme.readOnlyTextColor
    val resolvedTextColor = when {
        !enabled -> finalDisabledTextColor
        readOnly -> finalReadOnlyTextColor
        else -> finalEnabledTextColor
    }

    val textFieldColors = TextFieldDefaults.colors(
        disabledIndicatorColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledTextColor = finalDisabledTextColor,
        disabledContainerColor = finalDisabledContainerColor,
        focusedContainerColor = finalContainerColor,
        unfocusedContainerColor = finalContainerColor
    )

    val inlineSuffixTransformation = remember(
        inlineSuffix,
        resolvedTextColor,
        finalTextStyle
    ) {
        inlineSuffix?.takeIf { it.isNotEmpty() }?.let {
            InlineSuffixTransformation(it, finalTextStyle.copy(color = resolvedTextColor))
        }
    }

    val shouldShowInlineSuffix = inlineSuffixTransformation != null && value.isNotBlank()

    val finalVisualTransformation = when {
        visualTransformation != VisualTransformation.None -> visualTransformation
        !isPasswordVisible && isPasswordToggleDisplayed -> PasswordVisualTransformation()
        shouldShowInlineSuffix -> inlineSuffixTransformation!!
        else -> VisualTransformation.None
    }

    val focusRequester = remember { FocusRequester() }

    var isFocused by remember { mutableStateOf(false) }
    var wasFocused by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    LaunchedEffect(key1 = requestFocus) {
        if (requestFocus) {
            focusRequester.requestFocus()
        }
    }

    val focusedBorderBrushOverride = focusedBorderColor?.let { SolidColor(it) }
    val unfocusedBorderBrushOverride = unfocusedBorderColor?.let { SolidColor(it) }
    val focusedStroke = BorderStroke(
        width = focusedBorderWidth ?: finalBorderStroke.width,
        brush = focusedBorderBrushOverride ?: finalBorderStroke.brush
    )
    val unfocusedStroke = BorderStroke(
        width = unfocusedBorderWidth ?: finalBorderStroke.width,
        brush = unfocusedBorderBrushOverride ?: finalBorderStroke.brush
    )
    val appliedBorderStroke = when {
        !borderEnabled -> BorderStroke(0.dp, Color.Transparent)
        isFocused -> focusedStroke
        else -> unfocusedStroke
    }

    Column(modifier = containerModifier) {
        if (title != null || titleTrailingSlot != null || isTextCharCountVisible) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    if (title != null) {
                        Text(
                            text = title,
                            style = finalTitleTextStyle
                        )
                    }

                    if (titleTrailingSlot != null) {
                        if (title != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        titleTrailingSlot()
                    }
                }

                if (isTextCharCountVisible) {
                    Text(
                        text = stringResource(Res.string.char_count, value.length, maxLength),
                        style = finalCharCountTextStyle
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        RoundedSurface(
            borderStroke = appliedBorderStroke,
            color = if (enabled) finalContainerColor else finalDisabledContainerColor,
            shape = finalShape
        ) {
            Box {
                TextField(
                    value = value,
                    onValueChange = {
                        if (isEditable && it.length <= maxLength) {
                            onValueChange(it)
                        }
                    },
                    modifier = textFieldModifier
                        .fillMaxWidth()
                        .onFocusEvent {
                            isFocused = it.isFocused
                            if (wasFocused && !it.isFocused) {
                                onFocusCleared?.invoke()
                            }
                            wasFocused = it.isFocused
                        }
                        .align(Alignment.CenterStart)
                        .focusRequester(focusRequester),
                    enabled = enabled,
                    readOnly = readOnly,
                    textStyle = finalTextStyle.copy(color = resolvedTextColor),
                    placeholder = {
                        Text(
                            text = hint,
                            style = finalHintStyle,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    leadingIcon = leadingSlot,
                    trailingIcon = if (isPasswordToggleDisplayed) {
                        {
                            IconButton(
                                onClick = {
                                    onPasswordToggleClick(!isPasswordVisible)
                                }) {
                                Icon(
                                    painter = painterResource(
                                        if (isPasswordVisible) Res.drawable.shared_ic_password_visible else Res.drawable.shared_ic_password_invisible
                                    ),
                                    tint = CustomGrayHintColor,
                                    contentDescription = stringResource(
                                        if (isPasswordVisible) Res.string.shared_cont_desc_icon_password_visible else Res.string.shared_cont_desc_icon_password_invisible
                                    )
                                )
                            }
                        }
                    } else trailingSlot,
                    suffix = suffixSlot,
                    visualTransformation = finalVisualTransformation,
                    keyboardOptions = keyboardOptions,
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            onDone?.invoke()
                        }),
                    maxLines = maxLines,
                    singleLine = singleLine,
                    colors = textFieldColors,
                )
            }
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = it,
                style = finalErrorStyle
            )
        }
    }
}

@Preview
@Composable
fun CustomTextFieldPreview() {
    AppTheme {
        CustomTextField(
            value = "test", onValueChange = {})
    }
}

@Preview
@Composable
fun CustomTextFieldWithErrorPreview() {
    AppTheme {
        CustomTextField(
            value = "test",
            onValueChange = {},
            errorMessage = "Error message",
        )
    }
}

@Preview
@Composable
fun CustomTextFieldLightSurfacePreview() {
    AppTheme {
        CustomTextField(value = "test", onValueChange = {}, trailingSlot = {
            Icon(
                painter = painterResource(Res.drawable.shared_ic_password_invisible),
                contentDescription = ""
            )
        })
    }
}

@Preview
@Composable
fun CustomTextFieldWithErrorLightSurfacePreview() {
    AppTheme {
        CustomTextField(
            value = "test",
            onValueChange = {},
            errorMessage = "Error message",
            trailingSlot = {
                Icon(
                    painter = painterResource(Res.drawable.shared_ic_password_invisible),
                    contentDescription = ""
                )
            })
    }
}