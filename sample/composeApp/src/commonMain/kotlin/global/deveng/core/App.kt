package global.deveng.core

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import global.deveng.core.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview


@Preview
@Composable
internal fun App() = AppTheme {


    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {

            }
        ) {
            Text("Initialize SDK")
        }

        Button(
            onClick = {


            }
        ) {
            Text("Test SDK")
        }


    }
}
