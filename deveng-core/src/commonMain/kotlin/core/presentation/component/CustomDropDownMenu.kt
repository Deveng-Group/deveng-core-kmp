package core.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import core.presentation.component.scrollbar.scrollbarWithScrollState
import core.presentation.component.textfield.CustomTextField
import core.presentation.theme.LocalComponentTheme
import core.util.EMPTY
import global.deveng.deveng_core.generated.resources.Res
import global.deveng.deveng_core.generated.resources.shared_content_description_angle_down
import global.deveng.deveng_core.generated.resources.shared_ic_angle_down
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> CustomDropDownMenu(
    modifier: Modifier = Modifier,
    title: String? = String.EMPTY,
    hintMessage: String = String.EMPTY,
    titleColor: Color? = null,
    backgroundColor: Color? = null,
    textColor: Color? = null,
    unfocusedBorderColor: Color? = null,
    focusedBorderColor: Color? = null,
    dividerColor: Color? = null,
    isScrollBarVisible: Boolean? = null,
    scrollBarColor: Color? = null,
    scrollBarTrackColor: Color? = null,
    dropDownMenuHeight: Dp = 450.dp,
    items: List<T>,
    selectedItem: T?,
    onItemSelected: (T) -> Unit
) {
    val componentTheme = LocalComponentTheme.current
    val dropdownTheme = componentTheme.customDropDownMenu

    val finalTitleColor = titleColor ?: dropdownTheme.titleColor
    val finalBackgroundColor = backgroundColor ?: dropdownTheme.backgroundColor
    val finalTextColor = textColor ?: dropdownTheme.textColor
    val finalHintTextColor = dropdownTheme.hintTextColor
    val finalUnfocusedBorderColor = unfocusedBorderColor ?: dropdownTheme.unfocusedBorderColor
    val finalFocusedBorderColor = focusedBorderColor ?: dropdownTheme.focusedBorderColor
    val finalDividerColor = dividerColor ?: dropdownTheme.dividerColor
    val finalScrollBarColor = scrollBarColor ?: dropdownTheme.scrollBarColor
    val finalScrollBarTrackColor = scrollBarTrackColor ?: dropdownTheme.scrollBarTrackColor
    val finalIsScrollBarVisible = isScrollBarVisible ?: dropdownTheme.isScrollBarVisible
    val finalFieldTextStyle = dropdownTheme.fieldTextStyle
    val finalMenuItemTextStyle = dropdownTheme.menuItemTextStyle
    val finalShape = dropdownTheme.shape

    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        ExposedDropdownMenuBox(
            modifier = Modifier.fillMaxWidth(),
            expanded = isExpanded,
            onExpandedChange = { isExpanded = !isExpanded }
        ) {
            val displayText = if (selectedItem == null ||
                selectedItem.toString().isEmpty() ||
                selectedItem.toString() == hintMessage
            ) hintMessage else selectedItem.toString()

            val displayColor =
                if (displayText == hintMessage) finalHintTextColor else finalTextColor
            val resolvedFieldTextStyle = finalFieldTextStyle.copy(color = displayColor)

            CustomTextField(
                title = title,
                titleColor = finalTitleColor,
                textFieldModifier = Modifier
                    .menuAnchor(
                        type = MenuAnchorType.PrimaryNotEditable,
                        enabled = true
                    )
                    .height(50.dp)
                    .fillMaxWidth(),
                textStyle = resolvedFieldTextStyle,
                value = displayText,
                onValueChange = {},
                readOnly = true,
                trailingSlot = {
                    Icon(
                        modifier = Modifier
                            .height(15.dp)
                            .width(15.dp),
                        painter = painterResource(Res.drawable.shared_ic_angle_down),
                        tint = finalTextColor,
                        contentDescription = stringResource(Res.string.shared_content_description_angle_down)
                    )
                },
                textColor = displayColor,
                unfocusedBorderColor = finalUnfocusedBorderColor,
                focusedBorderColor = finalFocusedBorderColor,
                containerColor = finalBackgroundColor,
                shape = finalShape
            )

            val scrollState = rememberScrollState()

            val dropdownModifier = Modifier
                .heightIn(
                    max = dropDownMenuHeight
                )
                .background(
                    color = finalBackgroundColor
                )
                .exposedDropdownSize(true)
                .then(
                    if (finalIsScrollBarVisible) {
                        Modifier.scrollbarWithScrollState(
                            scrollState = scrollState,
                            scrollBarColor = finalScrollBarColor,
                            scrollBarTrackColor = finalScrollBarTrackColor
                        )
                    } else {
                        Modifier
                    }
                )

            ExposedDropdownMenu(
                scrollState = scrollState,
                modifier = dropdownModifier,
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false }
            ) {
                items.forEachIndexed { index, item ->
                    DropdownMenuItem(
                        modifier = Modifier.background(finalBackgroundColor),
                        onClick = {
                            onItemSelected(item)
                            isExpanded = false
                        },
                        text = {
                            Text(
                                text = item.toString(),
                                style = finalMenuItemTextStyle.copy(
                                    color = finalTextColor
                                )
                            )
                        }
                    )
                    if (index < items.size - 1) {
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = finalDividerColor
                        )
                    }
                }
            }
        }
    }
}