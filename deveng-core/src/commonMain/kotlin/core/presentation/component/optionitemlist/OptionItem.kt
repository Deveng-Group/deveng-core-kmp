package core.presentation.component.optionitemlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import core.presentation.component.CustomIconButton
import core.presentation.component.Slot
import core.presentation.theme.LocalComponentTheme
import core.util.debouncedCombinedClickable
import global.deveng.deveng_core.generated.resources.Res
import global.deveng.deveng_core.generated.resources.shared_cont_desc_icon_check_box
import global.deveng.deveng_core.generated.resources.shared_ic_checked_circle
import global.deveng.deveng_core.generated.resources.shared_ic_unchecked_circle
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * A single option item component that can display text, optional leading icon/slot, and check icon.
 * Supports bold leading text followed by regular text.
 *
 * @param text Main text to display in the item.
 * @param boldLeadingText Optional bold text displayed before the main text.
 * @param isCheckIconVisible Whether the check icon is visible. Default is true.
 * @param isSelected Whether the item is selected (affects check icon appearance). Default is false.
 * @param leadingIcon Optional drawable resource for a leading icon.
 * @param leadingSlot Optional composable slot for custom leading content.
 * @param onCheckboxClick Callback invoked when the check icon is clicked.
 * @param onItemClick Callback invoked when the item is clicked.
 * @param backgroundColor Background color of the item. If null, uses theme default.
 * @param horizontalPadding Horizontal padding of the item. If null, uses theme default.
 * @param checkIconTint Color tint for the check icon. If null, uses theme default.
 * @param textStyle Text style for the main text. If null, uses theme default.
 */
@Composable
fun OptionItem(
    text: String,
    boldLeadingText: String? = null,
    isCheckIconVisible: Boolean = true,
    isSelected: Boolean = false,
    leadingIcon: DrawableResource? = null,
    leadingSlot: Slot? = null,
    onItemClick: () -> Unit,
    onCheckboxClick: () -> Unit = onItemClick,
    backgroundColor: Color? = null,
    horizontalPadding: Dp? = null,
    checkIconTint: Color? = null,
    textStyle: TextStyle? = null
) {
    val componentTheme = LocalComponentTheme.current
    val optionItemTheme = componentTheme.optionItem
    val finalBackgroundColor = backgroundColor ?: optionItemTheme.backgroundColor
    val finalHorizontalPadding = horizontalPadding ?: optionItemTheme.horizontalPadding
    val finalCheckIconTint = checkIconTint ?: optionItemTheme.checkIconTint
    val finalTextStyle = textStyle ?: optionItemTheme.textStyle

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(optionItemTheme.rowHeight)
            .background(color = finalBackgroundColor)
            .debouncedCombinedClickable { onItemClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(finalHorizontalPadding))

        if (leadingSlot != null) {
            leadingSlot()
        }

        if (leadingIcon != null) {
            Icon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(leadingIcon),
                tint = optionItemTheme.leadingIconTint,
                contentDescription = text
            )

            Spacer(modifier = Modifier.width(11.dp))
        }

        Text(
            text = buildAnnotatedString {
                if (boldLeadingText != null) {
                    withStyle(
                        style = optionItemTheme.boldLeadingTextStyle.toSpanStyle()
                    ) {
                        append(boldLeadingText)
                    }
                    append(" ")
                }
                append(text)
            },
            modifier = Modifier.weight(1f),
            style = finalTextStyle,
            overflow = TextOverflow.Ellipsis
        )

        if (isCheckIconVisible) {
            Spacer(modifier = Modifier.width(8.dp))

            CustomIconButton(
                icon = if (isSelected) {
                    Res.drawable.shared_ic_checked_circle
                } else {
                    Res.drawable.shared_ic_unchecked_circle
                },
                iconModifier = Modifier,
                backgroundColor = optionItemTheme.checkIconBackgroundColor,
                iconDescription = stringResource(Res.string.shared_cont_desc_icon_check_box),
                iconTint = finalCheckIconTint,
                onClick = { onCheckboxClick() }
            )
        }

        Spacer(modifier = Modifier.width(finalHorizontalPadding))

    }
}

@Preview
@Composable
fun OptionItemPreview() {
    OptionItem(
        text = "test 1",
        onItemClick = {}
    )
}