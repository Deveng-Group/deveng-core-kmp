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
import core.presentation.theme.LocalComponentTheme
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
    titleColor: Color? = null,
    headerColor: Color? = null,
    description: String? = null,
    descriptionColor: Color? = null,
    bodyColor: Color? = null,
    positiveButtonText: String? = null,
    positiveButtonColor: Color? = null,
    positiveButtonTextColor: Color? = null,
    onPositiveButtonClick: () -> Unit = {},
    negativeButtonText: String? = null,
    negativeButtonColor: Color? = null,
    negativeButtonTextColor: Color? = null,
    onNegativeButtonClick: () -> Unit = {},
    onDismissRequest: () -> Unit = {},
    content: Slot? = null
) {
    val componentTheme = LocalComponentTheme.current
    val alertDialogTheme = componentTheme.alertDialog
    
    val finalHeaderColor = headerColor ?: alertDialogTheme.headerColor
    val finalBodyColor = bodyColor ?: alertDialogTheme.bodyColor
    val finalTitleColor = titleColor ?: alertDialogTheme.titleColor
    val finalDescriptionColor = descriptionColor ?: alertDialogTheme.descriptionColor
    val finalDividerColor = alertDialogTheme.dividerColor
    val finalPositiveButtonColor = positiveButtonColor ?: alertDialogTheme.positiveButtonColor
    val finalPositiveButtonTextColor = positiveButtonTextColor ?: alertDialogTheme.positiveButtonTextColor
    val finalNegativeButtonColor = negativeButtonColor ?: alertDialogTheme.negativeButtonColor
    val finalNegativeButtonTextColor = negativeButtonTextColor ?: alertDialogTheme.negativeButtonTextColor
    val finalIconTint = iconTint ?: alertDialogTheme.iconColor
    if (isDialogVisible) {
        CustomDialog(
            modifier = dialogModifier,
            header = {
                CustomDialogHeader(
                    modifier = headerModifier
                        .background(color = finalHeaderColor)
                        .padding(top = 15.dp),
                    title = title,
                    texColor = finalTitleColor,
                    icon = headerIcon,
                    iconDescription = iconDescription,
                    iconTint = finalIconTint
                )
            },
            onDismissRequest = {
                onDismissRequest()
            }
        ) {
            Column(
                modifier = contentModifier
                    .background(color = finalBodyColor)
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
                        style = MediumTextStyle().copy(
                            fontSize = alertDialogTheme.descriptionTextStyle.fontSize,
                            textAlign = TextAlign.Center
                        ),
                        textAlign = TextAlign.Center,
                        color = finalDescriptionColor
                    )
                }

                if (content != null) {
                    content()
                }

                if (negativeButtonText != null || positiveButtonText != null) {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = finalDividerColor
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
                                fontSize = alertDialogTheme.buttonTextStyle.fontSize,
                                color = finalNegativeButtonTextColor
                            ),
                            containerColor = finalNegativeButtonColor,
                            elevation = ButtonDefaults.buttonElevation(0.dp),
                            shape = RoundedCornerShape(0.dp),
                            onClick = { onNegativeButtonClick() }
                        )
                    }

                    if (negativeButtonText != null && positiveButtonText != null) {
                        VerticalDivider(
                            thickness = 1.dp,
                            color = finalDividerColor
                        )
                    }

                    if (positiveButtonText != null) {
                        CustomButton(
                            modifier = positiveButtonModifier.weight(1f),
                            text = positiveButtonText,
                            textStyle = BoldTextStyle().copy(
                                fontSize = alertDialogTheme.buttonTextStyle.fontSize,
                                color = finalPositiveButtonTextColor
                            ),
                            containerColor = finalPositiveButtonColor,
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