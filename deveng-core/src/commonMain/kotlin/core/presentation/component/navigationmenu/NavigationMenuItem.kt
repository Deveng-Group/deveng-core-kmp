package core.presentation.component.navigationmenu

import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

data class NavigationMenuItem(
    val screenRoute: String,
    val icon: DrawableResource,
    val iconTint: Color,
    val iconDescription: StringResource,
    val text: StringResource,
    val textColor: Color
)