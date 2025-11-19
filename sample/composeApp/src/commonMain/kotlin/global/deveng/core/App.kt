package global.deveng.core

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import core.presentation.component.CustomButton
import core.presentation.component.RoundedSurface
import core.presentation.component.alertdialog.CustomAlertDialog
import core.presentation.theme.*
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
internal fun App() {
    // Step 1: Create custom ComponentTheme
    val customTheme = ComponentTheme(
        // Custom font family (using system font as example)
        typography = TypographyTheme(
            fontFamily = FontFamily.SansSerif
        ),
        // Custom button theme
        button = ButtonTheme(
            containerColor = Color(0xFF1976D2),
            contentColor = Color.White,
            disabledContainerColor = Color(0xFF1976D2).copy(alpha = 0.4f),
            disabledContentColor = Color.White.copy(alpha = 0.4f),
            defaultTextStyle = SemiBoldTextStyle().copy(
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
        ),
        // Custom alert dialog theme
        alertDialog = AlertDialogTheme(
            headerColor = Color(0xFFF5F5F5),
            bodyColor = Color.White,
            titleColor = Color(0xFF1A1C1C),
            descriptionColor = Color(0xFF404941),
            dividerColor = Color(0xFFD9D9D9),
            positiveButtonColor = Color(0xFF1976D2),
            positiveButtonTextColor = Color.White,
            negativeButtonColor = Color.White,
            negativeButtonTextColor = Color(0xFF1A1C1C),
            iconColor = Color(0xFF374151),
            titleTextStyle = BoldTextStyle().copy(fontSize = 20.sp),
            descriptionTextStyle = RegularTextStyle().copy(fontSize = 16.sp),
            buttonTextStyle = MediumTextStyle().copy(fontSize = 16.sp)
        ),
        // Custom surface theme
        surface = SurfaceTheme(
            defaultColor = Color(0xFFF9F9F9),
            defaultContentColor = Color(0xFF1A1C1C)
        )
    )

    // Step 2: Apply theme using library's AppTheme
    AppTheme(componentTheme = customTheme) {
        ThemingDemo()
    }
}

@Composable
private fun ThemingDemo() {
    var showDialog by remember { mutableStateOf(false) }
    var showDefaultDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        Text(
            text = "ComponentTheme Demo",
            style = BoldTextStyle().copy(fontSize = 24.sp),
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Section 1: CustomButton Examples
        SectionTitle("CustomButton Examples")
        
        // Default themed button
        CustomButton(
            text = "Themed Button",
            onClick = { }
        )

        // Override theme at component level
        CustomButton(
            text = "Overridden Color",
            containerColor = Color(0xFFE91E63), // Overrides theme
            onClick = { }
        )

        // Custom typography
        CustomButton(
            text = "Custom Typography",
            textStyle = BoldTextStyle().copy(fontSize = 20.sp),
            onClick = { }
        )

        // Disabled button
        CustomButton(
            text = "Disabled Button",
            enabled = false,
            onClick = { }
        )

        // Section 2: RoundedSurface Examples
        SectionTitle("RoundedSurface Examples")
        
        // Default themed surface
        RoundedSurface(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Themed Surface",
                    style = MediumTextStyle().copy(fontSize = 16.sp)
                )
            }
        }

        // Override surface color
        RoundedSurface(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            color = Color(0xFFE3F2FD), // Overrides theme
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Custom Color Surface",
                    style = SemiBoldTextStyle().copy(fontSize = 16.sp)
                )
            }
        }

        // Section 3: Typography Examples
        SectionTitle("Typography Examples")
        
        Text(
            text = "Regular Text (400)",
            style = RegularTextStyle().copy(fontSize = 16.sp)
        )
        
        Text(
            text = "Medium Text (500)",
            style = MediumTextStyle().copy(fontSize = 16.sp)
        )
        
        Text(
            text = "SemiBold Text (600)",
            style = SemiBoldTextStyle().copy(fontSize = 16.sp)
        )
        
        Text(
            text = "Bold Text (700)",
            style = BoldTextStyle().copy(fontSize = 16.sp)
        )

        // Custom typography with color
        Text(
            text = "Custom Styled Text",
            style = SemiBoldTextStyle().copy(
                fontSize = 18.sp,
                color = Color(0xFF1976D2)
            )
        )

        // Section 4: AlertDialog Examples
        SectionTitle("CustomAlertDialog Examples")
        
        CustomButton(
            text = "Show Themed Dialog",
            onClick = { showDialog = true }
        )

        CustomButton(
            text = "Show Default Dialog",
            containerColor = Color(0xFF4CAF50),
            onClick = { showDefaultDialog = true }
        )

        // Themed AlertDialog
        CustomAlertDialog(
            isDialogVisible = showDialog,
            title = "Themed Dialog",
            description = "This dialog uses colors and typography from ComponentTheme. All text uses the custom font family.",
            positiveButtonText = "OK",
            negativeButtonText = "Cancel",
            onPositiveButtonClick = { showDialog = false },
            onNegativeButtonClick = { showDialog = false },
            onDismissRequest = { showDialog = false }
        )

        // Default AlertDialog (overrides theme)
        CustomAlertDialog(
            isDialogVisible = showDefaultDialog,
            title = "Default Dialog",
            description = "This dialog overrides theme colors at component level.",
            titleColor = Color(0xFF4CAF50),
            descriptionColor = Color(0xFF666666),
            positiveButtonText = "Confirm",
            positiveButtonColor = Color(0xFF4CAF50),
            positiveButtonTextColor = Color.White,
            negativeButtonText = "Cancel",
            onPositiveButtonClick = { showDefaultDialog = false },
            onNegativeButtonClick = { showDefaultDialog = false },
            onDismissRequest = { showDefaultDialog = false }
        )

        // Spacer at bottom
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = SemiBoldTextStyle().copy(
            fontSize = 18.sp,
            color = Color(0xFF1976D2)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}
