package core.presentation.component.optionitemlist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import core.presentation.component.CustomButton
import core.presentation.component.alertdialog.CustomDialog
import core.presentation.component.scrollbar.scrollbarWithLazyListState
import core.presentation.theme.CoreBoldTextStyle
import core.presentation.theme.LocalComponentTheme
import global.deveng.deveng_core.generated.resources.Res
import global.deveng.deveng_core.generated.resources.shared_save
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.stringResource

/**
 * A dialog component displaying a lazy-scrollable list of multi-selectable option items.
 * Uses LazyColumn for efficient rendering of large lists. Includes a save button at the bottom.
 * Maximum height is constrained to 585.dp. Supports multiple item selection.
 *
 * @param optionsList List of items of type T to display.
 * @param optionText Function that returns the text to display for each item.
 * @param optionId Function that returns a unique identifier (Int) for each item, used as LazyColumn key.
 * @param leadingIcon Optional function that returns a drawable resource for the leading icon of each item.
 * @param leadingOptionSlot Composable slot for custom leading content for each item. Default is empty.
 * @param isDialogVisible Whether the dialog is visible.
 * @param isCheckIconsVisible Whether to display check icons indicating selected items. Default is true.
 * @param selectedOptions List of currently selected items, or null if none selected.
 * @param onOptionItemClick Callback invoked when an option item is clicked, receives the clicked item.
 * @param onSaveButtonClick Callback invoked when the save button is clicked.
 * @param onDismissRequest Callback invoked when the dialog should be dismissed.
 */
@Composable
fun <T> OptionItemMultiSelectLazyListDialog(
    optionsList: List<T>,
    optionText: (T) -> String,
    optionId: (T) -> Int,
    leadingIcon: ((T) -> DrawableResource)? = null,
    leadingOptionSlot: @Composable (T) -> Unit = {},
    isDialogVisible: Boolean,
    isCheckIconsVisible: Boolean = true,
    selectedOptions: List<T>? = null,
    onOptionItemClick: (T) -> Unit,
    onSaveButtonClick: () -> Unit,
    onDismissRequest: () -> Unit
) {
    val lazyListState = rememberLazyListState()
    val componentTheme = LocalComponentTheme.current
    val optionListTheme = componentTheme.optionItemList

    if (isDialogVisible) {
        CustomDialog(
            modifier = Modifier
                .heightIn(max = 585.dp),
            onDismissRequest = onDismissRequest
        ) {
            Column {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .scrollbarWithLazyListState(
                            listState = lazyListState,
                            width = optionListTheme.lazyListScrollbarWidth,
                            scrollBarColor = optionListTheme.lazyListScrollbarColor,
                            topPadding = optionListTheme.lazyListScrollbarTopPadding,
                            bottomPadding = optionListTheme.lazyListScrollbarBottomPadding
                        ),
                    state = lazyListState
                ) {
                    itemsIndexed(
                        items = optionsList,
                        key = { _, item -> optionId(item) }
                    ) { index, item ->
                        OptionItem(
                            text = optionText(item),
                            isCheckIconVisible = isCheckIconsVisible,
                            leadingIcon = leadingIcon?.invoke(item),
                            leadingSlot = { leadingOptionSlot(item) },
                            isSelected = selectedOptions?.let { selectedOptions.contains(item) }
                                ?: false,
                            onItemClick = { onOptionItemClick(item) }
                        )

                        if (index < optionsList.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.height(1.dp),
                                color = optionListTheme.dividerColor
                            )
                        }
                    }
                }

                CustomButton(
                    modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(0.dp),
                    text = stringResource(Res.string.shared_save),
                    textStyle = CoreBoldTextStyle().copy(
                        fontSize = 16.sp,
                        color = Color.White
                    ),
                    onClick = onSaveButtonClick
                )
            }
        }
    }
}