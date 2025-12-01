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