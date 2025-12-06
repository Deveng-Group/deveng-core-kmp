package core.presentation.component.json

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import core.presentation.component.PickerField
import core.presentation.theme.AppTheme
import core.presentation.theme.LocalComponentTheme
import core.util.EMPTY
import core.util.calculateTextWidthAsDp
import global.deveng.deveng_core.generated.resources.shared_ic_checked_circle
import global.deveng.deveng_core.generated.resources.shared_ic_send
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * A component for displaying and copying JSON text with formatting.
 * Includes a title, formatted JSON display container, and a copy button that changes state when clicked.
 *
 * @param title Title text displayed above the JSON viewer.
 * @param json The JSON string to display and format.
 * @param containerColor Background color of the JSON display container. If null, uses theme default.
 * @param buttonTextColor Text color of the copy button. If null, uses theme default.
 * @param buttonColor Background color of the copy button. If null, uses theme default.
 * @param copyText Text displayed on the copy button when JSON is not copied.
 * @param copyIcon Drawable resource for the icon displayed when JSON is not copied.
 * @param copyIconTint Color tint for the copy icon. If null, uses theme default.
 * @param copyIconDescription Content description for accessibility for the copy icon.
 * @param copiedText Text displayed on the button when JSON has been copied.
 * @param copiedIcon Drawable resource for the icon displayed when JSON has been copied.
 * @param copiedIconTint Color tint for the copied icon. If null, uses theme default.
 * @param copiedIconDescription Content description for accessibility for the copied icon.
 * @param isJsonCopied Whether the JSON has been copied (affects button text and icon).
 * @param onClickCopyJsonIcon Callback invoked when the copy button is clicked, receives the JSON string.
 * @param titleTextStyle Text style for the title. If null, uses theme default.
 * @param jsonTextStyle Text style for the formatted JSON text. If null, uses theme default.
 * @param containerShape Shape of the JSON display container. If null, uses theme default.
 * @param containerPadding Padding inside the JSON display container. If null, uses theme default.
 * @param buttonShape Shape of the copy button. If null, uses theme default.
 * @param buttonHeight Height of the copy button. If null, uses theme default.
 * @param buttonIconSize Size of the button icon. If null, uses theme default.
 */
@Composable
fun JsonViewer(
    title: String,
    json: String,
    containerColor: Color? = null,
    buttonTextColor: Color? = null,
    buttonColor: Color? = null,
    copyText: String,
    copyIcon: DrawableResource,
    copyIconTint: Color? = null,
    copyIconDescription: String,
    copiedText: String,
    copiedIcon: DrawableResource,
    copiedIconTint: Color? = null,
    copiedIconDescription: String,
    isJsonCopied: Boolean,
    onClickCopyJsonIcon: (String) -> Unit,
    titleTextStyle: TextStyle? = null,
    jsonTextStyle: TextStyle? = null,
    containerShape: CornerBasedShape? = null,
    containerPadding: Dp? = null,
    buttonShape: CornerBasedShape? = null,
    buttonHeight: Dp? = null,
    buttonIconSize: Dp? = null
) {
    val componentTheme = LocalComponentTheme.current
    val jsonViewerTheme = componentTheme.jsonViewer

    val finalTitleTextStyle = titleTextStyle ?: jsonViewerTheme.titleTextStyle
    val finalJsonTextStyle = jsonTextStyle ?: jsonViewerTheme.jsonTextStyle
    val finalContainerColor = containerColor ?: jsonViewerTheme.containerColor
    val finalContainerShape = containerShape ?: jsonViewerTheme.containerShape
    val finalContainerPadding = containerPadding ?: jsonViewerTheme.containerPadding
    val finalButtonBackgroundColor = buttonColor ?: jsonViewerTheme.buttonBackgroundColor
    val finalButtonTextColor = buttonTextColor ?: jsonViewerTheme.buttonTextColor
    val finalButtonTextStyle = jsonViewerTheme.buttonTextStyle
    val finalButtonShape = buttonShape ?: jsonViewerTheme.buttonShape
    val finalButtonHeight = buttonHeight ?: jsonViewerTheme.buttonHeight
    val finalButtonIconSize = buttonIconSize ?: jsonViewerTheme.buttonIconSize
    val finalCopyIconTint = copyIconTint ?: jsonViewerTheme.copyIconTint
    val finalCopiedIconTint = copiedIconTint ?: jsonViewerTheme.copiedIconTint

    val buttonSize = calculateTextWidthAsDp(
        text = if (isJsonCopied) copiedText else copyText,
        style = finalButtonTextStyle
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = finalTitleTextStyle
            )

            Spacer(modifier = Modifier.weight(1f))

            PickerField(
                modifier = Modifier
                    .width(buttonSize + 70.dp)
                    .height(finalButtonHeight),
                shape = finalButtonShape,
                enabledBackGroundColor = finalButtonBackgroundColor,
                text = if (isJsonCopied) copiedText else copyText,
                textStyle = finalButtonTextStyle,
                enabledTextColor = finalButtonTextColor,
                trailingSlot = null,
                leadingSlot = {
                    Icon(
                        modifier = Modifier.size(finalButtonIconSize),
                        painter = if (isJsonCopied)
                            painterResource(copiedIcon) else
                            painterResource(copyIcon),
                        contentDescription = if (isJsonCopied)
                            copiedIconDescription else
                            copyIconDescription,
                        tint = if (isJsonCopied)
                            finalCopiedIconTint else
                            finalCopyIconTint
                    )
                },
                hint = String.EMPTY,
                onClick = { onClickCopyJsonIcon(json) }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = finalContainerColor,
                    shape = finalContainerShape
                )
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                modifier = Modifier.padding(finalContainerPadding),
                text = formatJson(json),
                style = finalJsonTextStyle
            )
        }
    }
}

@Preview
@Composable
fun JsonViewerPreview() {
    AppTheme {
        JsonViewer(
            title = "Input Json",
            json = """{"id":1,"name":"Ahmet Yılmaz","email":"ahmet@example.com","age":28,"isActive":true}""",
            copyText = "Copy",
            copyIcon = global.deveng.deveng_core.generated.resources.Res.drawable.shared_ic_send,
            copyIconDescription = "",
            copiedText = "Copied",
            copiedIcon = global.deveng.deveng_core.generated.resources.Res.drawable.shared_ic_checked_circle,
            copiedIconDescription = "",
            isJsonCopied = false,
            onClickCopyJsonIcon = {}
        )
    }
}