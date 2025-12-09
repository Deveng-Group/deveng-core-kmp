package core.presentation.component.sidemenu.collapsed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import core.presentation.component.Slot
import core.presentation.theme.LocalComponentTheme
import org.jetbrains.compose.resources.DrawableResource

@Composable
fun <T> SideMenuContentCollapsed(
    sideMenuWidth: Dp,
    sideMenuItemSelectedBackgroundColor: Color? = null,
    sideMenuItemUnselectedBackgroundColor: Color? = null,
    horizontalDividerColor: Color? = null,
    sideMenuHeader: Slot,
    sideMenuFooter: Slot,
    sideMenuItemList: List<T>,
    isSideMenuItemSelected: (T) -> Boolean,
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

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(sideMenuWidth)
            .padding(
                horizontal = sideMenuTheme.collapsedHorizontalPadding,
                vertical = sideMenuTheme.collapsedVerticalPadding
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(sideMenuTheme.collapsedHeaderItemSpacing)
    ) {
        sideMenuHeader()

        HorizontalDivider(
            thickness = 1.dp,
            color = finalDividerColor
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(sideMenuTheme.collapsedItemsSpacing)
        ) {
            sideMenuItemList.forEach { item ->
                val isSelected = isSideMenuItemSelected(item)
                SideMenuContentItemCollapsed(
                    icon = sideMenuItemIcon(item),
                    iconTint = sideMenuItemIconTint(item),
                    contentDescription = sideMenuItemIconDescription(item),
                    backgroundColor = if (isSelected) finalItemSelectedBackgroundColor else finalItemUnselectedBackgroundColor,
                    isSelected = isSelected,
                    onSideMenuItemClick = {
                        onSideMenuItemClick(item)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        HorizontalDivider(
            thickness = 1.dp,
            color = finalDividerColor
        )

        sideMenuFooter()
    }
}