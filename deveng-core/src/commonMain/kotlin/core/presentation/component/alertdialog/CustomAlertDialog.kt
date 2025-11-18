package core.presentation.component.alertdialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import core.presentation.component.CustomButton
import core.presentation.component.Slot
import core.presentation.theme.AppTheme
import core.presentation.theme.BoldTextStyle
import core.presentation.theme.CustomBlackColor
import core.presentation.theme.CustomDividerColor
import core.presentation.theme.MediumTextStyle
import global.deveng.deveng_core.generated.resources.Res
import global.deveng.deveng_core.generated.resources.shared_ic_arrow_left
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CustomAlertDialog(
    contentModifier: Modifier = Modifier,
    dialogModifier: Modifier = Modifier,
    headerModifier: Modifier = Modifier,
    buttonContainerModifier: Modifier = Modifier.height(50.dp),
    positiveButtonModifier: Modifier = Modifier.height(50.dp),
    negativeButtonModifier: Modifier = Modifier.height(50.dp),
    isDialogVisible: Boolean,
    headerIcon: DrawableResource? = null,
    iconDescription: String? = null,
    iconTint: Color? = null,
    title: String? = null,
    titleColor: Color = CustomBlackColor,
    headerColor: Color = Color.White,
    description: String? = null,
    descriptionColor: Color = CustomBlackColor,
    bodyColor: Color = Color.White,
    positiveButtonText: String? = null,
    positiveButtonColor: Color = Color.White,
    positiveButtonTextColor: Color = CustomBlackColor,
    onPositiveButtonClick: () -> Unit = {},
    negativeButtonText: String? = null,
    negativeButtonColor: Color = Color.White,
    negativeButtonTextColor: Color = CustomBlackColor,
    onNegativeButtonClick: () -> Unit = {},
    onDismissRequest: () -> Unit = {},
    content: Slot? = null
) {
    if (isDialogVisible) {
        CustomDialog(
            modifier = dialogModifier,
            header = {
                CustomDialogHeader(
                    modifier = headerModifier
                        .background(color = headerColor)
                        .padding(top = 15.dp),
                    title = title,
                    texColor = titleColor,
                    icon = headerIcon,
                    iconDescription = iconDescription,
                    iconTint = iconTint
                )
            },
            onDismissRequest = {
                onDismissRequest()
            }
        ) {
            Column(
                modifier = contentModifier
                    .background(color = bodyColor)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (description != null) {
                    Text(
                        modifier = Modifier
                            .align(alignment = Alignment.CenterHorizontally)
                            .padding(
                                vertical = 15.dp,
                                horizontal = 30.dp
                            ),
                        text = description,
                        style = MediumTextStyle().copy(fontSize = 16.sp),
                        textAlign = TextAlign.Center,
                        color = descriptionColor
                    )
                }

                if (content != null) {
                    content()
                }

                if (negativeButtonText != null || positiveButtonText != null) {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = CustomDividerColor
                    )
                }

                Row(
                    modifier = buttonContainerModifier,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (negativeButtonText != null) {
                        CustomButton(
                            modifier = negativeButtonModifier.weight(1f),
                            text = negativeButtonText,
                            textStyle = BoldTextStyle().copy(
                                fontSize = 16.sp,
                                color = negativeButtonTextColor
                            ),
                            containerColor = negativeButtonColor,
                            elevation = ButtonDefaults.buttonElevation(0.dp),
                            shape = RoundedCornerShape(0.dp),
                            onClick = { onNegativeButtonClick() }
                        )
                    }

                    if (negativeButtonText != null && positiveButtonText != null) {
                        VerticalDivider(
                            thickness = 1.dp,
                            color = CustomDividerColor
                        )
                    }

                    if (positiveButtonText != null) {
                        CustomButton(
                            modifier = positiveButtonModifier.weight(1f),
                            text = positiveButtonText,
                            textStyle = BoldTextStyle().copy(
                                fontSize = 16.sp,
                                color = positiveButtonTextColor
                            ),
                            containerColor = positiveButtonColor,
                            elevation = ButtonDefaults.buttonElevation(0.dp),
                            shape = RoundedCornerShape(0.dp),
                            onClick = { onPositiveButtonClick() }
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun CustomAlertDialogErrorPreview() {
    AppTheme {
        CustomAlertDialog(
            isDialogVisible = true,
            title = "Error",
            description = "Error description",
            onDismissRequest = {},
            content = {
                Icon(
                    modifier = Modifier.size(62.dp),
                    painter = painterResource(Res.drawable.shared_ic_arrow_left),
                    contentDescription = "Icon",
                    tint = Color.Black
                )
            }
        )
    }
}

@Preview
@Composable
fun CustomAlertDialogSuccessPreview() {
    AppTheme {
        CustomAlertDialog(
            isDialogVisible = true,
            title = "Title",
            description = "Description, description, description, description. ",
            positiveButtonText = "Confirm",
            negativeButtonText = "Cancel",
            content = {
                Icon(
                    painter = painterResource(Res.drawable.shared_ic_arrow_left),
                    contentDescription = "Icon",
                    tint = Color.Cyan,
                    modifier = Modifier.size(62.dp)
                )
            }
        )
    }
}