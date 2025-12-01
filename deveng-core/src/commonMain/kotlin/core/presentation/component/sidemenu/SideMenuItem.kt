package core.presentation.component.sidemenu

import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

data class SideMenuItem(
    val screenRoute: String,
    val icon: DrawableResource,
    val iconTint: Color,
    val iconDescription: StringResource,
    val text: StringResource,
    val textColor: Color
)