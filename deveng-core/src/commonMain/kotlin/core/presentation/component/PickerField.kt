package core.presentation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import core.presentation.theme.CoreCustomBlackColor
import core.presentation.theme.LocalComponentTheme
import core.util.debouncedCombinedClickable
import global.deveng.deveng_core.generated.resources.Res
import global.deveng.deveng_core.generated.resources.shared_content_desc_icon_direction
import global.deveng.deveng_core.generated.resources.shared_ic_angle_right
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun PickerField(
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    text: String? = null,
    hint: String,
    title: String? = null,
    titleColor: Color? = null,
    titleTrailingIcon: @Composable (() -> Unit)? = null,
    trailingSlot: @Composable (() -> Unit)? = {
        Icon(
            painter = painterResource(Res.drawable.shared_ic_angle_right),
            contentDescription = stringResource(Res.string.shared_content_desc_icon_direction),
            tint = CoreCustomBlackColor
        )
    },
    leadingSlot: @Composable (() -> Unit)? = null,
    errorMessage: String? = null,
    shape: CornerBasedShape? = null,
    enabledBackGroundColor: Color? = null,
    enabledBorderColor: Color? = null,
    enabledTextColor: Color? = null,
    enabledBorderWidth: Dp? = null,
    hintTextColor: Color? = null,
    disabledBackGroundColor: Color? = null,
    disabledBorderColor: Color? = null,
    disabledTextColor: Color? = null,
    titleTextStyle: TextStyle? = null,
    textStyle: TextStyle? = null,
    hintTextStyle: TextStyle? = null,
    errorTextStyle: TextStyle? = null,
    onClick: () -> Unit
) {
    val componentTheme = LocalComponentTheme.current
    val pickerFieldTheme = componentTheme.pickerField

    val finalShape = shape ?: pickerFieldTheme.shape
    val finalTitleColor = titleColor ?: pickerFieldTheme.titleTextStyle.color
    val finalTitleTextStyle = (titleTextStyle ?: pickerFieldTheme.titleTextStyle).copy(
        color = finalTitleColor
    )
    val finalEnabledBackgroundColor =
        enabledBackGroundColor ?: pickerFieldTheme.enabledBackgroundColor
    val finalEnabledBorderColor = enabledBorderColor ?: pickerFieldTheme.enabledBorderColor
    val finalEnabledBorderWidth = enabledBorderWidth ?: pickerFieldTheme.enabledBorderWidth
    val finalEnabledTextColor = enabledTextColor ?: pickerFieldTheme.enabledTextColor
    val finalHintTextColor = hintTextColor ?: pickerFieldTheme.hintTextColor
    val finalDisabledBackgroundColor =
        disabledBackGroundColor ?: pickerFieldTheme.disabledBackgroundColor
    val finalDisabledBorderColor = disabledBorderColor ?: pickerFieldTheme.disabledBorderColor
    val finalDisabledTextColor = disabledTextColor ?: pickerFieldTheme.disabledTextColor
    val finalTextStyle = textStyle ?: pickerFieldTheme.textStyle
    val finalHintStyle = hintTextStyle ?: pickerFieldTheme.hintTextStyle
    val finalErrorStyle = errorTextStyle ?: pickerFieldTheme.errorTextStyle

    Column(modifier = modifier) {
        if (title != null || titleTrailingIcon != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (title != null) {
                    Text(
                        text = title,
                        style = finalTitleTextStyle
                    )
                }

                if (titleTrailingIcon != null) {
                    titleTrailingIcon()
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        RoundedSurface(
            shape = finalShape,
            color = if (isEnabled) finalEnabledBackgroundColor else finalDisabledBackgroundColor,
            borderStroke = if (!isEnabled) BorderStroke(
                width = 1.dp,
                color = finalDisabledBorderColor
            ) else BorderStroke(finalEnabledBorderWidth, finalEnabledBorderColor),
            modifier = Modifier
                .height(56.dp)
                .fillMaxWidth()
                .debouncedCombinedClickable {
                    if (isEnabled) {
                        onClick()
                    }
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (leadingSlot != null) {
                    leadingSlot()

                    Spacer(modifier = Modifier.width(12.dp))
                }

                Text(
                    modifier = Modifier.weight(1f),
                    text = text?.takeIf { it.isNotEmpty() } ?: hint,
                    color = if (isEnabled) {
                        if (!text.isNullOrEmpty()) {
                            finalEnabledTextColor
                        } else {
                            finalHintTextColor
                        }
                    } else {
                        finalDisabledTextColor
                    },
                    style = if (!text.isNullOrEmpty()) {
                        finalTextStyle
                    } else {
                        finalHintStyle
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (trailingSlot != null) {
                    trailingSlot()
                }
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
