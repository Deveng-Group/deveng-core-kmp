package core.presentation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import core.presentation.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun RoundedSurface(
    modifier: Modifier = Modifier,
    borderStroke: BorderStroke = BorderStroke(0.dp, Color.Transparent),
    color: Color = Color.White,
    shape: Shape = MaterialTheme.shapes.medium,
    contentColor: Color = contentColorFor(color),
    content: Slot
) {
    Surface(
        modifier = modifier,
        color = color,
        contentColor = contentColor,
        shape = shape,
        border = borderStroke
    ) {
        content()
    }
}

@Preview
@Composable
fun RoundedSurfacePreview() {
    AppTheme {
        RoundedSurface(
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.primary,
            content = {}
        )
    }
}