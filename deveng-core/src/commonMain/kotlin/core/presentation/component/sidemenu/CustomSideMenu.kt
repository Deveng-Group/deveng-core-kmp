package core.presentation.component.sidemenu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import core.presentation.component.Slot
import core.presentation.component.sidemenu.collapsed.SideMenuContentCollapsed
import core.presentation.component.sidemenu.expanded.SideMenuContentExpanded
import core.presentation.theme.LocalComponentTheme
import org.jetbrains.compose.resources.DrawableResource

/**
 * A customizable side menu component with expand/collapse animation support.
 * Displays different content when expanded (with text and icons) vs collapsed (icons only).
 * Includes animated transitions between expanded and collapsed states.
 *
 * @param isSideMenuExpanded Whether the side menu is in expanded state (true) or collapsed (false).
 * @param sideMenuModifier Modifier to be applied to the side menu container.
 * @param sideMenuExpandedWidth Width of the side menu when expanded. If null, uses theme default.
 * @param sideMenuCollapsedWidth Width of the side menu when collapsed. If null, uses theme default.
 * @param sideMenuBackgroundColor Background color of the side menu. If null, uses theme default.
 * @param sideMenuShape Shape of the side menu container. If null, uses theme default.
 * @param verticalDividerColor Color of the vertical divider on the right edge. If null, uses theme default.
 * @param verticalDividerThickness Thickness of the vertical divider. If null, uses theme default.
 * @param sideMenuSectionSeparatorColor Color of horizontal dividers separating menu sections. If null, uses theme default.
 * @param sideMenuExpandedHeader Composable header content displayed when menu is expanded.
 * @param sideMenuExpandedFooter Composable footer content displayed when menu is expanded.
 * @param sideMenuCollapsedHeader Composable header content displayed when menu is collapsed.
 * @param sideMenuCollapsedFooter Composable footer content displayed when menu is collapsed.
 * @param sideMenuItemBackgroundColor Background color of menu items. If null, uses theme default.
 * @param sideMenuItemList List of items of type T to display as menu items.
 * @param isSideMenuItemSelected Function that returns whether an item is currently selected.
 * @param sideMenuItemText Composable function that returns the text to display for each menu item.
 * @param sideMenuItemTextStyle Composable function that returns the text style for each menu item.
 * @param sideMenuItemIcon Function that returns the icon drawable resource for each menu item.
 * @param sideMenuItemIconTint Function that returns the icon tint color for each menu item.
 * @param sideMenuItemIconDescription Composable function that returns the accessibility description for each menu item icon.
 * @param onSideMenuItemClick Callback invoked when a menu item is clicked, receives the clicked item.
 */
@Composable
fun <T> CustomSideMenu(
    isSideMenuExpanded: Boolean,
    sideMenuModifier: Modifier = Modifier,
    sideMenuExpandedWidth: Dp? = null,
    sideMenuCollapsedWidth: Dp? = null,
    sideMenuBackgroundColor: Color? = null,
    sideMenuShape: Shape? = null,
    verticalDividerColor: Color? = null,
    verticalDividerThickness: Dp? = null,
    sideMenuSectionSeparatorColor: Color? = null,
    sideMenuExpandedHeader: Slot,
    sideMenuExpandedFooter: Slot,
    sideMenuCollapsedHeader: Slot,
    sideMenuCollapsedFooter: Slot,
    sideMenuItemBackgroundColor: Color? = null,
    sideMenuItemList: List<T>,
    isSideMenuItemSelected: (T) -> Boolean,
    sideMenuItemText: @Composable (T) -> String,
    sideMenuItemTextStyle: @Composable (T) -> TextStyle,
    sideMenuItemIcon: (T) -> DrawableResource,
    sideMenuItemIconTint: (T) -> Color,
    sideMenuItemIconDescription: @Composable (T) -> String,
    onSideMenuItemClick: (T) -> Unit,
) {
    val componentTheme = LocalComponentTheme.current
    val sideMenuTheme = componentTheme.sideMenu

    val finalExpandedWidth = sideMenuExpandedWidth ?: sideMenuTheme.sideMenuExpandedWidth
    val finalCollapsedWidth = sideMenuCollapsedWidth ?: sideMenuTheme.sideMenuCollapsedWidth
    val finalBackgroundColor = sideMenuBackgroundColor ?: sideMenuTheme.sideMenuBackgroundColor
    val finalShape = sideMenuShape ?: sideMenuTheme.sideMenuShape
    val finalVerticalDividerColor = verticalDividerColor ?: sideMenuTheme.verticalDividerColor
    val finalVerticalDividerThickness =
        verticalDividerThickness ?: sideMenuTheme.verticalDividerThickness
    val finalSectionSeparatorColor =
        sideMenuSectionSeparatorColor ?: sideMenuTheme.sideMenuSectionSeparatorColor
    val finalItemBackgroundColor =
        sideMenuItemBackgroundColor ?: sideMenuTheme.sideMenuItemBackgroundColor

    Box(
        modifier = sideMenuModifier
            .fillMaxHeight()
            .background(
                color = finalBackgroundColor,
                shape = finalShape
            )
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        awaitPointerEvent()
                    }
                }
            }
    ) {
        VerticalDivider(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(
                    top = sideMenuTheme.verticalDividerTopBottomPadding,
                    bottom = sideMenuTheme.verticalDividerTopBottomPadding
                ),
            thickness = finalVerticalDividerThickness,
            color = finalVerticalDividerColor
        )

        AnimatedVisibility(
            visible = isSideMenuExpanded,
            enter = fadeIn() + expandHorizontally(expandFrom = Alignment.Start),
            exit = fadeOut() + shrinkHorizontally(shrinkTowards = Alignment.Start)
        ) {
            SideMenuContentExpanded(
                sideMenuWidth = finalExpandedWidth,
                sideMenuItemList = sideMenuItemList,
                sideMenuItemBackgroundColor = finalItemBackgroundColor,
                sideMenuHeader = sideMenuExpandedHeader,
                sideMenuFooter = sideMenuExpandedFooter,
                horizontalDividerColor = finalSectionSeparatorColor,
                onSideMenuItemClick = onSideMenuItemClick,
                isSideMenuItemSelected = isSideMenuItemSelected,
                sideMenuItemText = sideMenuItemText,
                sideMenuItemTextStyle = sideMenuItemTextStyle,
                sideMenuItemIcon = sideMenuItemIcon,
                sideMenuItemIconTint = sideMenuItemIconTint,
                sideMenuItemIconDescription = sideMenuItemIconDescription
            )
        }

        AnimatedVisibility(
            visible = !isSideMenuExpanded,
            enter = fadeIn() + expandHorizontally(expandFrom = Alignment.Start),
            exit = fadeOut() + shrinkHorizontally(shrinkTowards = Alignment.Start)
        ) {
            SideMenuContentCollapsed(
                sideMenuWidth = finalCollapsedWidth,
                sideMenuItemList = sideMenuItemList,
                sideMenuItemBackgroundColor = finalItemBackgroundColor,
                horizontalDividerColor = finalSectionSeparatorColor,
                sideMenuHeader = sideMenuCollapsedHeader,
                sideMenuFooter = sideMenuCollapsedFooter,
                isSideMenuItemSelected = isSideMenuItemSelected,
                sideMenuItemIcon = sideMenuItemIcon,
                sideMenuItemIconTint = sideMenuItemIconTint,
                sideMenuItemIconDescription = sideMenuItemIconDescription,
                onSideMenuItemClick = onSideMenuItemClick
            )
        }
    }
}