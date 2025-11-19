package core.presentation.component.alertdialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import core.presentation.theme.AppTheme
import core.presentation.theme.LocalComponentTheme
import core.presentation.theme.SemiBoldTextStyle
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CustomDialogHeader(
    title: String?,
    icon: DrawableResource? = null,
    iconTint: Color? = null,
    iconDescription: String? = null,
    modifier: Modifier = Modifier,
    style: TextStyle? = null,
    texColor: Color? = null
) {
    val componentTheme = LocalComponentTheme.current
    val dialogHeaderTheme = componentTheme.dialogHeader
    
    val finalStyle = style ?: SemiBoldTextStyle().copy(
        fontSize = dialogHeaderTheme.titleTextStyle.fontSize
    )
    val finalTextColor = texColor ?: dialogHeaderTheme.titleColor
    val finalIconTint = iconTint ?: dialogHeaderTheme.iconTint
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        icon?.let {
            finalIconTint?.let {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = iconDescription,
                    tint = finalIconTint
                )
            }
        }

        title?.let {
            Text(
                text = title,
                style = finalStyle,
                color = finalTextColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
fun CustomDialogHeaderPreview() {
    AppTheme {
        CustomDialogHeader(
            title = "Header",
            texColor = Color.White
        )
    }
}