package core.presentation.component.optionitemlist

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import core.presentation.component.alertdialog.CustomDialog
import core.presentation.component.scrollbar.scrollbarWithLazyListState
import core.presentation.theme.LocalComponentTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun <T> OptionItemLazyListDialog(
    optionsList: List<T>,
    optionText: (T) -> String,
    optionId: (T) -> Int,
    leadingIcon: ((T) -> DrawableResource)? = null,
    leadingOptionSlot: @Composable (T) -> Unit = {},
    isDialogVisible: Boolean,
    isCheckIconsVisible: Boolean = true,
    selectedOption: T? = null,
    onOptionItemClick: (T) -> Unit,
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
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
                        isSelected = selectedOption?.let { optionId(it) == optionId(item) }
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
        }
    }
}

@Preview
@Composable
fun OptionItemLazyListDialogPreview() {
    OptionItemLazyListDialog(
        optionsList = listOf(
            "item0",
            "item1",
            "item2",
            "item3item4item5item6item7item8item9item10item11item12item13item14"
        ),
        optionText = { option -> option },
        optionId = { option -> option.hashCode() },
        isDialogVisible = true,
        onOptionItemClick = {},
        onDismissRequest = {}
    )
}