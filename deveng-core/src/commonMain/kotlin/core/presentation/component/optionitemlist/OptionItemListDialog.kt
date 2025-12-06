package core.presentation.component.optionitemlist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import core.presentation.component.alertdialog.CustomDialog
import core.presentation.theme.LocalComponentTheme
import org.jetbrains.compose.resources.DrawableResource

/**
 * A dialog component displaying a list of selectable option items.
 * Maximum height is constrained to 350.dp with scrolling support.
 *
 * @param optionList List of items of type T to display.
 * @param optionText Composable function that returns the text to display for each item.
 * @param optionId Function that returns a unique identifier (Int) for each item, used for selection comparison.
 * @param leadingIcon Optional function that returns a drawable resource for the leading icon of each item.
 * @param leadingOptionSlot Composable slot for custom leading content for each item. Default is empty.
 * @param isDialogVisible Whether the dialog is visible.
 * @param isCheckIconsVisible Whether to display check icons indicating selected items. Default is true.
 * @param selectedOption Currently selected item, or null if none selected.
 * @param onOptionItemClick Callback invoked when an option item is clicked, receives the clicked item.
 * @param onDismissRequest Callback invoked when the dialog should be dismissed.
 */
@Composable
fun <T> OptionItemListDialog(
    optionList: List<T>,
    optionText: @Composable (T) -> String,
    optionId: (T) -> Int,
    leadingIcon: ((T) -> DrawableResource)? = null,
    leadingOptionSlot: @Composable (T) -> Unit = {},
    isDialogVisible: Boolean,
    isCheckIconsVisible: Boolean = true,
    selectedOption: T? = null,
    onOptionItemClick: (T) -> Unit,
    onDismissRequest: () -> Unit
) {
    val componentTheme = LocalComponentTheme.current
    val optionListTheme = componentTheme.optionItemList

    if (isDialogVisible) {
        CustomDialog(
            modifier = Modifier
                .heightIn(max = 350.dp),
            onDismissRequest = onDismissRequest
        ) {
            Column {
                optionList.forEachIndexed { index, item ->
                    OptionItem(
                        text = optionText(item),
                        isCheckIconVisible = isCheckIconsVisible,
                        leadingIcon = leadingIcon?.invoke(item),
                        leadingSlot = { leadingOptionSlot(item) },
                        isSelected = selectedOption?.let { optionId(it) == optionId(item) }
                            ?: false,
                        onItemClick = { onOptionItemClick(item) }
                    )

                    if (index < optionList.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.height(1.dp),
                            color = optionListTheme.dividerColor
                        )
                    }
                }
            }
        }
    }
}