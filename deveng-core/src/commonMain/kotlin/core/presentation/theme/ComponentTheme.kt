package core.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp

/**
 * Theme colors for CustomButton component
 */
data class ButtonTheme(
    val containerColor: Color = SecondaryColor,
    val contentColor: Color = OnPrimaryColor,
    val disabledContainerColor: Color = SecondaryColor.copy(alpha = 0.4f),
    val disabledContentColor: Color = OnPrimaryColor.copy(alpha = 0.4f),
    val defaultTextStyle: TextStyle = TextStyle(
        fontSize = 18.sp,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
    )
)

/**
 * Theme colors for CustomAlertDialog component
 */
data class AlertDialogTheme(
    val headerColor: Color = Color.White,
    val bodyColor: Color = Color.White,
    val titleColor: Color = CustomBlackColor,
    val descriptionColor: Color = CustomBlackColor,
    val dividerColor: Color = CustomDividerColor,
    val positiveButtonColor: Color = Color.White,
    val positiveButtonTextColor: Color = CustomBlackColor,
    val negativeButtonColor: Color = Color.White,
    val negativeButtonTextColor: Color = CustomBlackColor,
    val iconColor: Color = AlertDialogIconColor,
    val titleTextStyle: TextStyle = TextStyle(fontSize = 16.sp),
    val descriptionTextStyle: TextStyle = TextStyle(fontSize = 16.sp),
    val buttonTextStyle: TextStyle = TextStyle(fontSize = 16.sp)
)

/**
 * Theme colors for RoundedSurface component
 */
data class SurfaceTheme(
    val defaultColor: Color = Color.White,
    val defaultContentColor: Color = Color.Black
)

/**
 * Theme colors for CustomDialogHeader component
 */
data class DialogHeaderTheme(
    val titleColor: Color = CustomBlackColor,
    val iconTint: Color? = null,
    val titleTextStyle: TextStyle = TextStyle(fontSize = 16.sp)
)

/**
 * Typography theme for customizing font family across all components.
 * 
 * @param fontFamily The default font family to use. If null, uses Urbanist font family.
 *                   Library users can provide their own FontFamily here.
 */
data class TypographyTheme(
    val fontFamily: FontFamily? = null
)

/**
 * Complete component theme containing all component-specific themes.
 * 
 * Usage example:
 * ```
 * val customTheme = ComponentTheme(
 *     typography = TypographyTheme(
 *         fontFamily = FontFamily.SansSerif // Use system font
 *     ),
 *     button = ButtonTheme(
 *         containerColor = Color.Blue,
 *         contentColor = Color.White,
 *         defaultTextStyle = TextStyle(fontSize = 20.sp)
 *     ),
 *     alertDialog = AlertDialogTheme(
 *         headerColor = Color.LightGray,
 *         titleColor = Color.Black
 *     )
 * )
 * 
 * AppTheme(componentTheme = customTheme) {
 *     // Your app content
 * }
 * ```
 */
data class ComponentTheme(
    val typography: TypographyTheme = TypographyTheme(),
    val button: ButtonTheme = ButtonTheme(),
    val alertDialog: AlertDialogTheme = AlertDialogTheme(),
    val surface: SurfaceTheme = SurfaceTheme(),
    val dialogHeader: DialogHeaderTheme = DialogHeaderTheme()
)

/**
 * Default component theme using library's default colors.
 * This is used when no custom ComponentTheme is provided.
 */
val DefaultComponentTheme = ComponentTheme()

/**
 * CompositionLocal for ComponentTheme.
 * 
 * Library users can override component colors and typography by:
 * 1. Creating a custom ComponentTheme
 * 2. Passing it to AppTheme: `AppTheme(componentTheme = myCustomTheme) { ... }`
 * 
 * Components will automatically use the theme values unless explicitly overridden
 * via component parameters.
 */
val LocalComponentTheme = compositionLocalOf { DefaultComponentTheme }

