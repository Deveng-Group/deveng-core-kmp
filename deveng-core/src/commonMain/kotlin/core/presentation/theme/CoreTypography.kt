package core.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import global.deveng.deveng_core.generated.resources.Res
import global.deveng.deveng_core.generated.resources.urbanistbold
import global.deveng.deveng_core.generated.resources.urbanistmedium
import global.deveng.deveng_core.generated.resources.urbanistregular
import global.deveng.deveng_core.generated.resources.urbanistsemibold
import org.jetbrains.compose.resources.Font

val CORE_REGULAR_FONT_WEIGHT = FontWeight(400)
val CORE_MEDIUM_FONT_WEIGHT = FontWeight(500)
val CORE_SEMI_BOLD_FONT_WEIGHT = FontWeight(600)
val CORE_BOLD_FONT_WEIGHT = FontWeight(700)

@Composable
private fun UrbanistTextFont() = FontFamily(
    Font(resource = Res.font.urbanistregular, weight = CORE_REGULAR_FONT_WEIGHT),
    Font(resource = Res.font.urbanistmedium, weight = CORE_MEDIUM_FONT_WEIGHT),
    Font(resource = Res.font.urbanistsemibold, weight = CORE_SEMI_BOLD_FONT_WEIGHT),
    Font(resource = Res.font.urbanistbold, weight = CORE_BOLD_FONT_WEIGHT)
)

/**
 * Returns a regular weight (400) text style.
 * Uses the font family from ComponentTheme if provided, otherwise uses Urbanist.
 */
@Composable
fun RegularTextStyle(): TextStyle {
    val componentTheme = LocalComponentTheme.current
    val fontFamily = componentTheme.typography.fontFamily ?: UrbanistTextFont()
    return TextStyle(
        fontFamily = fontFamily,
        fontWeight = CORE_REGULAR_FONT_WEIGHT
    )
}

/**
 * Returns a medium weight (500) text style.
 * Uses the font family from ComponentTheme if provided, otherwise uses Urbanist.
 */
@Composable
fun MediumTextStyle(): TextStyle {
    val componentTheme = LocalComponentTheme.current
    val fontFamily = componentTheme.typography.fontFamily ?: UrbanistTextFont()
    return TextStyle(
        fontFamily = fontFamily,
        fontWeight = CORE_MEDIUM_FONT_WEIGHT
    )
}

/**
 * Returns a semi-bold weight (600) text style.
 * Uses the font family from ComponentTheme if provided, otherwise uses Urbanist.
 */
@Composable
fun SemiBoldTextStyle(): TextStyle {
    val componentTheme = LocalComponentTheme.current
    val fontFamily = componentTheme.typography.fontFamily ?: UrbanistTextFont()
    return TextStyle(
        fontFamily = fontFamily,
        fontWeight = CORE_SEMI_BOLD_FONT_WEIGHT
    )
}

/**
 * Returns a bold weight (700) text style.
 * Uses the font family from ComponentTheme if provided, otherwise uses Urbanist.
 */
@Composable
fun BoldTextStyle(): TextStyle {
    val componentTheme = LocalComponentTheme.current
    val fontFamily = componentTheme.typography.fontFamily ?: UrbanistTextFont()
    return TextStyle(
        fontFamily = fontFamily,
        fontWeight = CORE_BOLD_FONT_WEIGHT
    )
}