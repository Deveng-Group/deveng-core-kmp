package core.presentation.component.sidemenu.horizontal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import core.presentation.component.Slot
import core.presentation.theme.LocalComponentTheme
import org.jetbrains.compose.resources.DrawableResource

@Composable
fun <T> SideMenuContentHorizontal(
    sideMenuHeight: Dp,
    sideMenuItemSelectedBackgroundColor: Color? = null,
    sideMenuItemUnselectedBackgroundColor: Color? = null,
    sideMenuHeader: Slot,
    sideMenuFooter: Slot,
    horizontalDividerColor: Color? = null,
    sideMenuItemList: List<T>,
    isSideMenuItemSelected: (T) -> Boolean,
    sideMenuItemText: @Composable (T) -> String,
    sideMenuItemTextStyle: @Composable (T) -> TextStyle,
    sideMenuItemIcon: (T) -> DrawableResource,
    sideMenuItemIconTint: (T) -> Color,
    sideMenuItemIconDescription: @Composable (T) -> String,
    onSideMenuItemClick: (T) -> Unit
) {
    val sideMenuTheme = LocalComponentTheme.current.sideMenu
    val finalItemSelectedBackgroundColor =
        sideMenuItemSelectedBackgroundColor ?: sideMenuTheme.sideMenuItemSelectedBackgroundColor
    val finalItemUnselectedBackgroundColor =
        sideMenuItemUnselectedBackgroundColor ?: sideMenuTheme.sideMenuItemUnselectedBackgroundColor
    val finalDividerColor =
        horizontalDividerColor ?: sideMenuTheme.sideMenuSectionSeparatorColor

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(sideMenuHeight)
            .padding(
                horizontal = sideMenuTheme.expandedHorizontalPadding,
                vertical = sideMenuTheme.expandedVerticalPadding
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(sideMenuTheme.expandedHeaderItemSpacing)
    ) {
        sideMenuHeader()

        VerticalDivider(
            modifier = Modifier
                .height(sideMenuTheme.verticalDividerThickness * 2),
            thickness = sideMenuTheme.verticalDividerThickness,
            color = finalDividerColor
        )

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(sideMenuTheme.expandedItemsSpacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            sideMenuItemList.forEach { item ->
                val isSelected = isSideMenuItemSelected(item)
                SideMenuContentItemHorizontal(
                    text = sideMenuItemText(item),
                    textStyle = sideMenuItemTextStyle(item),
                    icon = sideMenuItemIcon(item),
                    iconTint = sideMenuItemIconTint(item),
                    iconDescription = sideMenuItemIconDescription(item),
                    isSelected = isSelected,
                    backgroundColor = if (isSelected) finalItemSelectedBackgroundColor else finalItemUnselectedBackgroundColor,
                    onSideMenuItemClick = {
                        onSideMenuItemClick(item)
                    }
                )
            }
        }

        VerticalDivider(
            modifier = Modifier
                .height(sideMenuTheme.verticalDividerThickness * 2),
            thickness = sideMenuTheme.verticalDividerThickness,
            color = finalDividerColor
        )

        sideMenuFooter()
    }
}

