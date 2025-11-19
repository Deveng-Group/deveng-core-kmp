package core.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import core.presentation.theme.LocalComponentTheme
import core.presentation.theme.SemiBoldTextStyle
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CustomButton(
    modifier: Modifier = Modifier
        .height(50.dp)
        .width(200.dp),
    text: String? = null,
    textStyle: TextStyle? = null,
    textModifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: CornerBasedShape = RoundedCornerShape(8.dp),
    containerColor: Color? = null,
    disabledContainerColor: Color? = null,
    disabledContentColor: Color? = null,
    contentColor: Color? = null,
    elevation: ButtonElevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp),
    trailingIconModifier: Modifier = Modifier,
    trailingIcon: DrawableResource? = null,
    trailingIconTint: Color = Color.Black,
    trailingIconContentDescription: String? = null,
    leadingIconModifier: Modifier = Modifier,
    leadingIcon: DrawableResource? = null,
    leadingIconTint: Color = Color.Black,
    leadingIconContentDescription: String? = null,
    contentArrangement: Arrangement.Horizontal = Arrangement.Center,
    onClick: () -> Unit
) {
    val componentTheme = LocalComponentTheme.current
    val buttonTheme = componentTheme.button
    
    val finalTextStyle = textStyle ?: SemiBoldTextStyle().copy(
        fontSize = buttonTheme.defaultTextStyle.fontSize,
        textAlign = buttonTheme.defaultTextStyle.textAlign
    )
    val finalContainerColor = containerColor ?: buttonTheme.containerColor
    val finalDisabledContainerColor = disabledContainerColor ?: buttonTheme.disabledContainerColor
    val finalContentColor = contentColor ?: buttonTheme.contentColor
    val finalDisabledContentColor = disabledContentColor ?: buttonTheme.disabledContentColor
    Button(
        modifier = modifier,
        enabled = enabled,
        colors = ButtonColors(
            containerColor = finalContainerColor,
            contentColor = finalContentColor,
            disabledContentColor = finalDisabledContentColor,
            disabledContainerColor = finalDisabledContainerColor
        ),
        elevation = elevation,
        shape = shape,
        onClick = onClick
    ) {
        Row(
            horizontalArrangement = contentArrangement,
            verticalAlignment = Alignment.CenterVertically
        ) {
            leadingIcon?.let {
                Icon(
                    modifier = leadingIconModifier,
                    painter = painterResource(leadingIcon),
                    contentDescription = leadingIconContentDescription,
                    tint = leadingIconTint
                )

                Spacer(modifier = Modifier.width(15.dp))
            }

            if (text != null) {
                Text(
                    modifier = textModifier,
                    text = text,
                    style = finalTextStyle
                )
            }

            trailingIcon?.let {
                Spacer(modifier = Modifier.width(18.dp))

                Icon(
                    modifier = trailingIconModifier,
                    painter = painterResource(trailingIcon),
                    contentDescription = trailingIconContentDescription,
                    tint = trailingIconTint
                )
            }
        }
    }
}

@Preview
@Composable
fun CustomButtonPreview() {
    CustomButton(
        text = "Custom Button",
        onClick = {}
    )
}