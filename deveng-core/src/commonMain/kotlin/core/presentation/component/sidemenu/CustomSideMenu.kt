package core.presentation.component.sidemenu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import core.presentation.component.Slot
import core.presentation.component.sidemenu.collapsed.SideMenuContentCollapsed
import core.presentation.component.sidemenu.expanded.SideMenuContentExpanded
import core.presentation.component.sidemenu.horizontal.SideMenuContentHorizontal
import core.presentation.theme.LocalComponentTheme
import org.jetbrains.compose.resources.DrawableResource

/**
 * A customizable side menu component with expand/collapse animation support.
 * Displays different content when expanded (with text and icons) vs collapsed (icons only).
 * Includes animated transitions between expanded and collapsed states.
 * Supports both vertical side menu and horizontal header modes.
 *
 * @param isSideMenuExpanded Whether the side menu is in expanded state (true) or collapsed (false).
 *                          In horizontal mode, this parameter is ignored and menu is always expanded.
 * @param menuMode Display mode: [MenuMode.Vertical] for side menu with expand/collapse,
 *                 [MenuMode.Horizontal] for fixed header (always expanded). Defaults to [MenuMode.Vertical].
 * @param menuAlignment Alignment: [MenuAlignment.Start] for left/start, [MenuAlignment.End] for right/end.
 *                     Defaults to [MenuAlignment.Start].
 * @param sideMenuModifier Modifier to be applied to the side menu container.
 * @param sideMenuExpandedWidth Width of the side menu when expanded. If null, uses theme default.
 *                              In horizontal mode, this is used as height.
 * @param sideMenuCollapsedWidth Width of the side menu when collapsed. If null, uses theme default.
 * @param sideMenuBackgroundColor Background color of the side menu. If null, uses theme default.
 * @param sideMenuShape Shape of the side menu container. If provided, overrides mode-specific shapes.
 *                      If null, uses mode-specific shapes (horizontal/vertical) or theme default.
 * @param sideMenuHorizontalShape Shape for horizontal header mode. If null, uses default header shape.
 * @param sideMenuVerticalShape Shape for vertical side menu mode. If null, uses default side menu shape.
 * @param verticalDividerColor Color of the vertical divider. If null, uses theme default.
 * @param verticalDividerThickness Thickness of the vertical divider. If null, uses theme default.
 * @param sideMenuSectionSeparatorColor Color of horizontal dividers separating menu sections. If null, uses theme default.
 * @param sideMenuExpandedHeader Composable header content displayed when menu is expanded.
 * @param sideMenuExpandedFooter Composable footer content displayed when menu is expanded.
 * @param sideMenuCollapsedHeader Composable header content displayed when menu is collapsed.
 * @param sideMenuCollapsedFooter Composable footer content displayed when menu is collapsed.
 * @param sideMenuItemSelectedBackgroundColor Background color of selected menu items. If null, uses theme default.
 * @param sideMenuItemUnselectedBackgroundColor Background color of unselected menu items. If null, uses theme default.
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
    menuMode: MenuMode = MenuMode.Vertical,
    menuAlignment: MenuAlignment = MenuAlignment.Start,
    sideMenuModifier: Modifier = Modifier,
    sideMenuExpandedWidth: Dp? = null,
    sideMenuCollapsedWidth: Dp? = null,
    sideMenuBackgroundColor: Color? = null,
    sideMenuShape: Shape? = null,
    sideMenuHorizontalShape: Shape? = null,
    sideMenuVerticalShape: Shape? = null,
    verticalDividerColor: Color? = null,
    verticalDividerThickness: Dp? = null,
    sideMenuSectionSeparatorColor: Color? = null,
    sideMenuExpandedHeader: Slot,
    sideMenuExpandedFooter: Slot,
    sideMenuCollapsedHeader: Slot,
    sideMenuCollapsedFooter: Slot,
    sideMenuItemSelectedBackgroundColor: Color? = null,
    sideMenuItemUnselectedBackgroundColor: Color? = null,
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

    val finalShape = sideMenuShape ?: when (menuMode) {
        MenuMode.Horizontal -> {
            sideMenuHorizontalShape ?: RoundedCornerShape(
                topStart = 0.dp,
                topEnd = 0.dp,
                bottomStart = 30.dp,
                bottomEnd = 30.dp
            )
        }
        MenuMode.Vertical -> {
            sideMenuVerticalShape ?: RoundedCornerShape(
                topStart = 0.dp,
                topEnd = 30.dp,
                bottomStart = 0.dp,
                bottomEnd = 30.dp
            )
        }
    }
    val finalVerticalDividerColor = verticalDividerColor ?: sideMenuTheme.verticalDividerColor
    val finalVerticalDividerThickness =
        verticalDividerThickness ?: sideMenuTheme.verticalDividerThickness
    val finalSectionSeparatorColor =
        sideMenuSectionSeparatorColor ?: sideMenuTheme.sideMenuSectionSeparatorColor
    val finalItemSelectedBackgroundColor =
        sideMenuItemSelectedBackgroundColor ?: sideMenuTheme.sideMenuItemSelectedBackgroundColor
    val finalItemUnselectedBackgroundColor =
        sideMenuItemUnselectedBackgroundColor ?: sideMenuTheme.sideMenuItemUnselectedBackgroundColor

    val finalIsExpanded = if (menuMode == MenuMode.Horizontal) true else isSideMenuExpanded

    var expandFrom = Alignment.End
    var shrinkTowards = Alignment.End
    var dividerAlignment = Alignment.CenterStart

    if (menuAlignment == MenuAlignment.Start) {
        expandFrom = Alignment.Start
        shrinkTowards = Alignment.Start
        dividerAlignment = Alignment.CenterEnd
    }

    Box(
        modifier = sideMenuModifier
            .then(
                if (menuMode == MenuMode.Horizontal) {
                    Modifier.fillMaxWidth()
                } else {
                    Modifier.fillMaxHeight()
                }
            )
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
        if (menuMode == MenuMode.Vertical) {
            VerticalDivider(
                modifier = Modifier
                    .align(dividerAlignment)
                    .padding(
                        top = sideMenuTheme.verticalDividerTopBottomPadding,
                        bottom = sideMenuTheme.verticalDividerTopBottomPadding
                    ),
                thickness = finalVerticalDividerThickness,
                color = finalVerticalDividerColor
            )
        }

        if (menuMode == MenuMode.Horizontal) {
            SideMenuContentHorizontal(
                sideMenuHeight = finalExpandedWidth,
                sideMenuItemList = sideMenuItemList,
                sideMenuItemSelectedBackgroundColor = finalItemSelectedBackgroundColor,
                sideMenuItemUnselectedBackgroundColor = finalItemUnselectedBackgroundColor,
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
        } else {
            AnimatedVisibility(
                visible = finalIsExpanded,
                enter = fadeIn() + expandHorizontally(expandFrom = expandFrom),
                exit = fadeOut() + shrinkHorizontally(shrinkTowards = shrinkTowards)
            ) {
                SideMenuContentExpanded(
                    sideMenuWidth = finalExpandedWidth,
                    sideMenuItemList = sideMenuItemList,
                    sideMenuItemSelectedBackgroundColor = finalItemSelectedBackgroundColor,
                    sideMenuItemUnselectedBackgroundColor = finalItemUnselectedBackgroundColor,
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
                visible = !finalIsExpanded,
                enter = fadeIn() + expandHorizontally(expandFrom = expandFrom),
                exit = fadeOut() + shrinkHorizontally(shrinkTowards = shrinkTowards)
            ) {
                SideMenuContentCollapsed(
                    sideMenuWidth = finalCollapsedWidth,
                    sideMenuItemList = sideMenuItemList,
                    sideMenuItemSelectedBackgroundColor = finalItemSelectedBackgroundColor,
                    sideMenuItemUnselectedBackgroundColor = finalItemUnselectedBackgroundColor,
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
}