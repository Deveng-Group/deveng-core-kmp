package core.presentation.component.sidemenu.collapsed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import core.presentation.theme.LocalComponentTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun SideMenuContentItemCollapsed(
    icon: DrawableResource,
    iconTint: Color,
    iconSize: Dp? = null,
    iconModifier: Modifier = Modifier,
    itemModifier: Modifier = Modifier,
    contentDescription: String,
    backgroundColor: Color,
    isSelected: Boolean = false,
    onSideMenuItemClick: () -> Unit
) {
    val sideMenuTheme = LocalComponentTheme.current.sideMenu
    val finalIconSize = iconSize ?: sideMenuTheme.collapsedItemIconSize

    Box(
        modifier = itemModifier
            .size(sideMenuTheme.collapsedItemSize)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(sideMenuTheme.collapsedItemCornerRadius)
            )
            .selectable(
                selected = isSelected,
                onClick = onSideMenuItemClick
            )
    ) {
        Icon(
            modifier = iconModifier
                .size(finalIconSize)
                .align(Alignment.Center),
            painter = painterResource(icon),
            contentDescription = contentDescription,
            tint = iconTint
        )
    }
}