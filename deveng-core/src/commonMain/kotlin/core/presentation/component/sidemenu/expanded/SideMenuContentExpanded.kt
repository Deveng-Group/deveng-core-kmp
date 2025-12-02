package core.presentation.component.sidemenu.expanded

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import core.presentation.component.Slot
import core.presentation.theme.LocalComponentTheme
import org.jetbrains.compose.resources.DrawableResource

@Composable
fun <T> SideMenuContentExpanded(
    sideMenuWidth: Dp,
    sideMenuItemBackgroundColor: Color? = null,
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
    val finalItemBackgroundColor =
        sideMenuItemBackgroundColor ?: sideMenuTheme.sideMenuItemBackgroundColor
    val finalDividerColor =
        horizontalDividerColor ?: sideMenuTheme.sideMenuSectionSeparatorColor

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(sideMenuWidth)
            .padding(
                horizontal = sideMenuTheme.expandedHorizontalPadding,
                vertical = sideMenuTheme.expandedVerticalPadding
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(sideMenuTheme.expandedHeaderItemSpacing)
    ) {
        sideMenuHeader()

        HorizontalDivider(
            thickness = 1.dp,
            color = finalDividerColor
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(sideMenuTheme.expandedItemsSpacing)
        ) {
            sideMenuItemList.forEach { item ->
                SideMenuContentItemExpanded(
                    text = sideMenuItemText(item),
                    textStyle = sideMenuItemTextStyle(item),
                    icon = sideMenuItemIcon(item),
                    iconTint = sideMenuItemIconTint(item),
                    iconDescription = sideMenuItemIconDescription(item),
                    isSelected = isSideMenuItemSelected(item),
                    backgroundColor = finalItemBackgroundColor,
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