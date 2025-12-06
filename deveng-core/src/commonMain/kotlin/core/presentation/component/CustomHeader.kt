package core.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import core.presentation.theme.AppTheme
import core.presentation.theme.LocalComponentTheme
import global.deveng.deveng_core.generated.resources.Res
import global.deveng.deveng_core.generated.resources.shared_content_desc_header_center_icon
import global.deveng.deveng_core.generated.resources.shared_content_desc_icon_left
import global.deveng.deveng_core.generated.resources.shared_content_desc_icon_right
import global.deveng.deveng_core.generated.resources.shared_ic_angle_right
import global.deveng.deveng_core.generated.resources.shared_ic_arrow_left
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * A customizable header component with left, right, and center icon support.
 * Commonly used for navigation headers in mobile applications.
 *
 * @param modifier Modifier to be applied to the header container.
 * @param leftIcon Drawable resource for the left icon button. Default is arrow left icon.
 * @param rightIcon Optional drawable resource for the right icon button.
 * @param centerIcon Optional drawable resource for the center icon/logo.
 * @param leftIconDescription Content description for accessibility for the left icon.
 * @param rightIconDescription Content description for accessibility for the right icon.
 * @param centerIconDescription Content description for accessibility for the center icon.
 * @param isLeftIconButtonVisible Whether the left icon button is visible. Default is true.
 * @param isRightIconButtonVisible Whether the right icon button is visible. Default is true if rightIcon is not null.
 * @param isCenterIconVisible Whether the center icon is visible. Default is true.
 * @param containerPadding Padding values for the header container. If null, uses theme default.
 * @param backgroundColor Background color of the header. If null, uses theme default.
 * @param leftIconTint Color tint for the left icon. If null, uses theme default.
 * @param rightIconTint Color tint for the right icon. If null, uses theme default.
 * @param leftIconBackgroundColor Background color of the left icon button. If null, uses theme default.
 * @param rightIconBackgroundColor Background color of the right icon button. If null, uses theme default.
 * @param iconButtonSize Size of the icon buttons. If null, uses theme default.
 * @param onLeftIconClick Callback invoked when the left icon is clicked.
 * @param onRightIconClick Callback invoked when the right icon is clicked.
 */
@Composable
fun CustomHeader(
    modifier: Modifier = Modifier,
    leftIcon: DrawableResource = Res.drawable.shared_ic_arrow_left,
    rightIcon: DrawableResource? = null,
    centerIcon: DrawableResource? = null,
    leftIconDescription: String = stringResource(Res.string.shared_content_desc_icon_left),
    rightIconDescription: String = stringResource(Res.string.shared_content_desc_icon_right),
    centerIconDescription: String = stringResource(Res.string.shared_content_desc_header_center_icon),
    isLeftIconButtonVisible: Boolean = true,
    isRightIconButtonVisible: Boolean = rightIcon != null,
    isCenterIconVisible: Boolean = true,
    containerPadding: PaddingValues? = null,
    backgroundColor: Color? = null,
    leftIconTint: Color? = null,
    rightIconTint: Color? = null,
    leftIconBackgroundColor: Color? = null,
    rightIconBackgroundColor: Color? = null,
    iconButtonSize: Dp? = null,
    onLeftIconClick: () -> Unit = {},
    onRightIconClick: () -> Unit = {}
) {
    val componentTheme = LocalComponentTheme.current
    val headerTheme = componentTheme.header

    val finalPadding = containerPadding ?: headerTheme.containerPadding
    val finalBackgroundColor = backgroundColor ?: headerTheme.backgroundColor
    val finalLeftTint = leftIconTint ?: headerTheme.leftIconTint
    val finalRightTint = rightIconTint ?: headerTheme.rightIconTint
    val finalLeftBackground = leftIconBackgroundColor ?: headerTheme.leftIconBackgroundColor
    val finalRightBackground = rightIconBackgroundColor ?: headerTheme.rightIconBackgroundColor
    val finalIconButtonSize = iconButtonSize ?: headerTheme.iconButtonSize

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(finalBackgroundColor)
            .padding(finalPadding),
        contentAlignment = Alignment.CenterStart
    ) {
        if (isLeftIconButtonVisible) {
            CustomIconButton(
                modifier = Modifier.align(Alignment.CenterStart),
                iconModifier = Modifier,
                buttonSize = finalIconButtonSize,
                backgroundColor = finalLeftBackground,
                icon = leftIcon,
                iconDescription = leftIconDescription,
                iconTint = finalLeftTint,
                onClick = onLeftIconClick
            )
        }

        val resolvedCenterIcon = centerIcon ?: headerTheme.icon
        if (isCenterIconVisible && resolvedCenterIcon != null) {
            androidx.compose.foundation.Image(
                modifier = Modifier
                    .width(126.dp)
                    .height(45.dp)
                    .align(Alignment.Center),
                painter = painterResource(resolvedCenterIcon),
                contentDescription = centerIconDescription
            )
        }

        if (isRightIconButtonVisible && rightIcon != null) {
            CustomIconButton(
                modifier = Modifier.align(Alignment.CenterEnd),
                iconModifier = Modifier,
                buttonSize = finalIconButtonSize,
                backgroundColor = finalRightBackground,
                icon = rightIcon,
                iconDescription = rightIconDescription,
                iconTint = finalRightTint,
                onClick = onRightIconClick
            )
        }
    }
}

@Preview
@Composable
fun CustomHeaderNoRightIconPreview() {
    AppTheme {
        CustomHeader(onLeftIconClick = {})
    }
}

@Preview
@Composable
fun CustomHeaderPreview() {
    AppTheme {
        CustomHeader(
            leftIcon = Res.drawable.shared_ic_arrow_left,
            leftIconDescription = stringResource(Res.string.shared_content_desc_icon_left),
            onLeftIconClick = {},
            onRightIconClick = {},
            rightIcon = Res.drawable.shared_ic_angle_right,
            rightIconDescription = stringResource(Res.string.shared_content_desc_icon_left),
            centerIcon = Res.drawable.shared_ic_angle_right
        )
    }
}