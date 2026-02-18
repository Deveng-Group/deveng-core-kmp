package core.presentation.component.navigationmenu

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
import core.presentation.component.navigationmenu.collapsed.NavigationMenuContentCollapsed
import core.presentation.component.navigationmenu.expanded.NavigationMenuContentExpanded
import core.presentation.component.navigationmenu.horizontal.NavigationMenuContentHorizontal
import core.presentation.theme.LocalComponentTheme
import org.jetbrains.compose.resources.DrawableResource

/**
 * A customizable navigation menu component with expand/collapse animation support.
 * Displays different content when expanded (with text and icons) vs collapsed (icons only).
 * Includes animated transitions between expanded and collapsed states.
 * Supports both vertical navigation menu and horizontal header modes.
 *
 * @param isExpanded Whether the navigation menu is in expanded state (true) or collapsed (false).
 *                          In horizontal mode, this parameter is ignored and menu is always expanded.
 * @param menuMode Display mode: [MenuMode.Vertical] for navigation menu with expand/collapse,
 *                 [MenuMode.Horizontal] for fixed header (always expanded). Defaults to [MenuMode.Vertical].
 * @param menuAlignment Alignment: [MenuAlignment.Start] for left/start, [MenuAlignment.End] for right/end.
 *                     Defaults to [MenuAlignment.Start].
 * @param modifier Modifier to be applied to the navigation menu container.
 * @param expandedWidth Width of the navigation menu when expanded in vertical mode (MenuMode.Vertical).
 *                              If null, uses theme default. Does not affect horizontal mode.
 * @param collapsedWidth Width of the navigation menu when collapsed. If null, uses theme default.
 * @param horizontalHeight Height of the navigation menu when in horizontal/header mode (MenuMode.Horizontal).
 *                                  If null, uses theme default horizontalHeight.
 *                                  Only affects horizontal mode. Vertical mode uses expandedWidth for width.
 * @param backgroundColor Background color of the navigation menu. If null, uses theme default.
 * @param shape Shape of the navigation menu container. If provided, overrides mode-specific shapes.
 *                      If null, uses mode-specific shapes (horizontal/vertical) or theme default.
 * @param horizontalShape Shape for horizontal header mode. If null, uses default header shape.
 * @param verticalShape Shape for vertical navigation menu mode. If null, uses default navigation menu shape.
 * @param verticalDividerColor Color of the vertical divider. If null, uses theme default.
 * @param verticalDividerThickness Thickness of the vertical divider. If null, uses theme default.
 * @param sectionSeparatorColor Color of horizontal dividers separating menu sections. If null, uses theme default.
 * @param isHorizontalIconVisible Whether to show icons in horizontal/header mode. Defaults to true.
 * @param expandedLeadingSlot Composable leading content displayed when menu is expanded.
 * @param expandedTrailingSlot Composable trailing content displayed when menu is expanded in vertical mode.
 * @param collapsedLeadingSlot Composable leading content displayed when menu is collapsed.
 * @param collapsedTrailingSlot Composable trailing content displayed when menu is collapsed.
 * @param horizontalItemSelectedBackgroundColor Background color of selected menu items in horizontal mode. If null, uses theme default.
 * @param verticalItemSelectedBackgroundColor Background color of selected menu items in vertical mode. If null, uses theme default.
 * @param itemList List of items of type T to display as menu items.
 * @param isItemSelected Function that returns whether an item is currently selected.
 * @param itemText Composable function that returns the text to display for each menu item.
 * @param itemTextStyle Composable function that returns the text style for each menu item.
 * @param itemIcon Function that returns the icon drawable resource for each menu item. Null means no icon (e.g. text-only in expanded mode).
 * @param itemIconTint Function that returns the icon tint color for each menu item. Used only when itemIcon is non-null.
 * @param itemIconDescription Composable function that returns the accessibility description for the icon. Null when there is no icon.
 * @param onItemClick Callback invoked when a menu item is clicked, receives the clicked item.
 */
@Composable
fun <T> NavigationMenu(
    isExpanded: Boolean,
    menuMode: MenuMode = MenuMode.Vertical,
    menuAlignment: MenuAlignment = MenuAlignment.Start,
    modifier: Modifier = Modifier,
    expandedWidth: Dp? = null,
    collapsedWidth: Dp? = null,
    horizontalHeight: Dp? = null,
    backgroundColor: Color? = null,
    shape: Shape? = null,
    horizontalShape: Shape? = null,
    verticalShape: Shape? = null,
    verticalDividerColor: Color? = null,
    verticalDividerThickness: Dp? = null,
    sectionSeparatorColor: Color? = null,
    isHorizontalIconVisible: Boolean = true,
    expandedLeadingSlot: Slot? = null,
    expandedTrailingSlot: Slot? = null,
    collapsedLeadingSlot: Slot? = null,
    collapsedTrailingSlot: Slot? = null,
    itemUnselectedBackgroundColor: Color? = null,
    horizontalItemSelectedBackgroundColor: Color? = null,
    verticalItemSelectedBackgroundColor: Color? = null,
    itemList: List<T>,
    isItemSelected: (T) -> Boolean,
    itemText: @Composable (T) -> String,
    itemTextStyle: @Composable (T) -> TextStyle,
    itemIcon: (T) -> DrawableResource?,
    itemIconTint: (T) -> Color?,
    itemIconDescription: @Composable (T) -> String?,
    onItemClick: (T) -> Unit,
) {
    val componentTheme = LocalComponentTheme.current
    val navigationMenuTheme = componentTheme.navigationMenu

    val finalExpandedWidth =
        expandedWidth ?: navigationMenuTheme.expandedWidth
    val finalCollapsedWidth =
        collapsedWidth ?: navigationMenuTheme.collapsedWidth
    val finalHorizontalHeight =
        horizontalHeight ?: navigationMenuTheme.horizontalHeight
    val finalBackgroundColor =
        backgroundColor ?: navigationMenuTheme.backgroundColor

    val finalShape = shape ?: when (menuMode) {
        MenuMode.Horizontal -> {
            horizontalShape ?: RoundedCornerShape(
                topStart = 0.dp,
                topEnd = 0.dp,
                bottomStart = 30.dp,
                bottomEnd = 30.dp
            )
        }

        MenuMode.Vertical -> {
            verticalShape ?: RoundedCornerShape(
                topStart = 0.dp,
                topEnd = 30.dp,
                bottomStart = 0.dp,
                bottomEnd = 30.dp
            )
        }
    }
    val finalVerticalDividerColor = verticalDividerColor ?: navigationMenuTheme.verticalDividerColor
    val finalVerticalDividerThickness =
        verticalDividerThickness ?: navigationMenuTheme.verticalDividerThickness
    val finalSectionSeparatorColor =
        sectionSeparatorColor
            ?: navigationMenuTheme.sectionSeparatorColor
    val finalHorizontalItemSelectedBackgroundColor =
        horizontalItemSelectedBackgroundColor
            ?: navigationMenuTheme.horizontalItemSelectedBackgroundColor
    val finalVerticalItemSelectedBackgroundColor =
        verticalItemSelectedBackgroundColor
            ?: navigationMenuTheme.verticalItemSelectedBackgroundColor
    val finalItemUnselectedBackgroundColor =
        itemUnselectedBackgroundColor
            ?: navigationMenuTheme.itemUnselectedBackgroundColor

    val finalIsExpanded = if (menuMode == MenuMode.Horizontal) true else isExpanded

    var expandFrom = Alignment.End
    var shrinkTowards = Alignment.End
    var dividerAlignment = Alignment.CenterStart

    if (menuAlignment == MenuAlignment.Start) {
        expandFrom = Alignment.Start
        shrinkTowards = Alignment.Start
        dividerAlignment = Alignment.CenterEnd
    }

    Box(
        modifier = modifier
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
                        top = navigationMenuTheme.verticalDividerTopBottomPadding,
                        bottom = navigationMenuTheme.verticalDividerTopBottomPadding
                    ),
                thickness = finalVerticalDividerThickness,
                color = finalVerticalDividerColor
            )
        }

        if (menuMode == MenuMode.Horizontal) {
            NavigationMenuContentHorizontal(
                height = finalHorizontalHeight,
                itemList = itemList,
                itemSelectedBackgroundColor = finalHorizontalItemSelectedBackgroundColor,
                itemUnselectedBackgroundColor = finalItemUnselectedBackgroundColor,
                leadingSlot = expandedLeadingSlot,
                trailingSlot = expandedTrailingSlot,
                horizontalDividerColor = finalSectionSeparatorColor,
                isIconVisible = isHorizontalIconVisible,
                onItemClick = onItemClick,
                isItemSelected = isItemSelected,
                itemText = itemText,
                itemTextStyle = itemTextStyle,
                itemIcon = itemIcon,
                itemIconTint = itemIconTint,
                itemIconDescription = itemIconDescription
            )
        }
        if (menuMode == MenuMode.Vertical) {
            AnimatedVisibility(
                visible = finalIsExpanded,
                enter = fadeIn() + expandHorizontally(expandFrom = expandFrom),
                exit = fadeOut() + shrinkHorizontally(shrinkTowards = shrinkTowards)
            ) {
                NavigationMenuContentExpanded(
                    width = finalExpandedWidth,
                    itemList = itemList,
                    itemSelectedBackgroundColor = finalVerticalItemSelectedBackgroundColor,
                    itemUnselectedBackgroundColor = finalItemUnselectedBackgroundColor,
                    leadingSlot = expandedLeadingSlot,
                    trailingSlot = expandedTrailingSlot,
                    horizontalDividerColor = finalSectionSeparatorColor,
                    onItemClick = onItemClick,
                    isItemSelected = isItemSelected,
                    itemText = itemText,
                    itemTextStyle = itemTextStyle,
                    itemIcon = itemIcon,
                    itemIconTint = itemIconTint,
                    itemIconDescription = itemIconDescription
                )
            }

            AnimatedVisibility(
                visible = !finalIsExpanded,
                enter = fadeIn() + expandHorizontally(expandFrom = expandFrom),
                exit = fadeOut() + shrinkHorizontally(shrinkTowards = shrinkTowards)
            ) {
                NavigationMenuContentCollapsed(
                    width = finalCollapsedWidth,
                    itemList = itemList,
                    itemSelectedBackgroundColor = finalVerticalItemSelectedBackgroundColor,
                    itemUnselectedBackgroundColor = finalItemUnselectedBackgroundColor,
                    horizontalDividerColor = finalSectionSeparatorColor,
                    leadingSlot = collapsedLeadingSlot,
                    trailingSlot = collapsedTrailingSlot,
                    isItemSelected = isItemSelected,
                    itemIcon = itemIcon,
                    itemIconTint = itemIconTint,
                    itemIconDescription = itemIconDescription,
                    onItemClick = onItemClick
                )
            }
        }
    }
}