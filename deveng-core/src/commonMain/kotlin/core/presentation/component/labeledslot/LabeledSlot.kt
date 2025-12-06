package core.presentation.component.labeledslot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import core.presentation.component.RoundedSurface
import core.presentation.theme.AppTheme
import core.presentation.theme.CoreBoldTextStyle
import core.presentation.theme.LocalComponentTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * A container component that displays content with optional overlay labels positioned at various alignments.
 * Labels are displayed as badges/overlays on top of the content, useful for displaying status badges, badges, etc.
 *
 * @param modifier Modifier to be applied to the container.
 * @param labels List of Label objects to display as overlays. Each label has text, colors, position, and shape.
 * @param containerWidth Width of the container. If null, uses theme default.
 * @param containerHeight Height of the container. If null, uses theme default.
 * @param containerShape Shape of the container. If null, uses theme default.
 * @param contentSlot Composable content displayed in the container (typically an image or view).
 */
@Composable
fun LabeledSlot(
    modifier: Modifier = Modifier,
    labels: List<Label> = emptyList(),
    containerWidth: Dp? = null,
    containerHeight: Dp? = null,
    containerShape: CornerBasedShape? = null,
    contentSlot: @Composable () -> Unit
) {
    val componentTheme = LocalComponentTheme.current
    val labeledImageBoxTheme = componentTheme.labeledImageBox
    val finalContainerWidth = containerWidth ?: labeledImageBoxTheme.containerWidth
    val finalContainerHeight = containerHeight ?: labeledImageBoxTheme.containerHeight
    val finalContainerShape = containerShape ?: labeledImageBoxTheme.containerShape

    RoundedSurface(
        modifier = modifier
            .height(finalContainerHeight)
            .width(finalContainerWidth),
        shape = finalContainerShape
    ) {
        Box {
            contentSlot()

            labels.forEach { label ->
                Box(
                    modifier = Modifier
                        .align(label.alignment)
                        .background(
                            color = label.backgroundColor,
                            shape = label.shape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        modifier = Modifier
                            .padding(5.dp),
                        text = label.text,
                        style = CoreBoldTextStyle().copy(
                            color = label.textColor,
                            fontSize = labeledImageBoxTheme.labelFontSize
                        )
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun LabeledSlotPreview() {
    AppTheme {
        LabeledSlot(
            labels = listOf(
                Label(
                    text = "NEW",
                    alignment = Alignment.TopEnd,
                    backgroundColor = Color.Red,
                    textColor = Color.White,
                    shape = RoundedCornerShape(8.dp)
                )
            ),
            contentSlot = {
                Box(
                    modifier = Modifier
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Image Content")
                }
            }
        )
    }
}