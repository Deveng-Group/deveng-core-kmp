package core.presentation.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.DrawableResource

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
 * Theme for CustomIconButton component
 */
data class IconButtonTheme(
    val buttonSize: Dp = 54.dp,
    val backgroundColor: Color = PrimaryColor,
    val iconTint: Color = CustomBlackColor,
    val shadowElevation: Dp = 0.dp,
    val shape: Shape = CircleShape
)

/**
 * Theme for LabeledSwitch component
 */
data class LabeledSwitchTheme(
    val labelTextStyle: TextStyle = TextStyle(
        fontSize = 16.sp,
        color = CustomGrayHintColor
    ),
    val switchScale: Float = 0.75f,
    val checkedThumbColor: Color = Color.White,
    val checkedTrackColor: Color = PrimaryColor,
    val checkedBorderColor: Color = PrimaryColor,
    val uncheckedThumbColor: Color = PrimaryColor,
    val uncheckedTrackColor: Color = Color.White,
    val uncheckedBorderColor: Color = Color.White
)

/**
 * Theme for PickerField component
 */
data class PickerFieldTheme(
    val titleTextStyle: TextStyle = TextStyle(
        fontSize = 16.sp,
        color = CustomBlackColor
    ),
    val textStyle: TextStyle = TextStyle(
        fontSize = 16.sp,
        color = CustomBlackColor
    ),
    val hintTextStyle: TextStyle = TextStyle(
        fontSize = 16.sp,
        color = CustomGrayHintColor
    ),
    val errorTextStyle: TextStyle = TextStyle(
        fontSize = 12.sp,
        color = Color(0xFFD32F2F)
    ),
    val shape: CornerBasedShape = RoundedCornerShape(12.dp),
    val enabledBackgroundColor: Color = Color.White,
    val enabledBorderColor: Color = Color.Transparent,
    val enabledBorderWidth: Dp = 0.dp,
    val enabledTextColor: Color = CustomBlackColor,
    val hintTextColor: Color = CustomGrayHintColor,
    val disabledBackgroundColor: Color = Color.Transparent,
    val disabledBorderColor: Color = Color.White,
    val disabledTextColor: Color = Color.White
)

/**
 * Theme for CustomDropDownMenu component
 */
data class CustomDropDownMenuTheme(
    val titleColor: Color = Color.White,
    val backgroundColor: Color = CustomBlackColor,
    val textColor: Color = Color.White,
    val hintTextColor: Color = CustomGrayHintColor,
    val unfocusedBorderColor: Color = Color.Transparent,
    val focusedBorderColor: Color = CustomGrayColor,
    val dividerColor: Color = CustomGrayHintColor,
    val scrollBarColor: Color = CustomGrayColor,
    val scrollBarTrackColor: Color = CustomGrayHintColor,
    val isScrollBarVisible: Boolean = false,
    val shape: CornerBasedShape = RoundedCornerShape(12.dp),
    val fieldTextStyle: TextStyle = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    val menuItemTextStyle: TextStyle = TextStyle(
        fontSize = 14.sp
    )
)

/**
 * Theme for CustomTextField component
 */
data class CustomTextFieldTheme(
    val titleTextStyle: TextStyle = TextStyle(
        fontSize = 16.sp,
        color = CustomBlackColor
    ),
    val charCountTextStyle: TextStyle = TextStyle(
        fontSize = 16.sp,
        color = CustomGrayHintColor
    ),
    val textStyle: TextStyle = TextStyle(
        fontSize = 16.sp,
        color = CustomBlackColor
    ),
    val hintTextStyle: TextStyle = TextStyle(
        fontSize = 16.sp,
        color = CustomGrayHintColor
    ),
    val errorTextStyle: TextStyle = TextStyle(
        fontSize = 12.sp,
        color = Color(0xFFD32F2F)
    ),
    val containerShape: CornerBasedShape = RoundedCornerShape(12.dp),
    val borderStroke: BorderStroke = BorderStroke(0.dp, Color.Transparent),
    val containerColor: Color = Color.White,
    val disabledContainerColor: Color = Color.White,
    val textColor: Color = CustomBlackColor,
    val disabledTextColor: Color = CustomGrayHintColor,
    val readOnlyTextColor: Color = CustomGrayHintColor,
    val isBorderActive: Boolean = true
)

/**
 * Theme for CustomDatePicker component
 */
data class DatePickerTheme(
    val trailingIconTint: Color = CustomBlackColor,
    val dialogContainerColor: Color = SecondaryColor,
    val dialogContentColor: Color = Color.White,
    val selectedDayContainerColor: Color = Color.White,
    val selectedDayContentColor: Color = SecondaryColor,
    val selectedYearContainerColor: Color = Color.White,
    val selectedYearContentColor: Color = SecondaryColor,
    val todayContentColor: Color = Color.White,
    val todayDateBorderColor: Color = Color.White,
    val confirmButtonTextColor: Color = Color.White,
    val dismissButtonTextColor: Color = Color.White
)

/**
 * Theme for CustomDateRangePicker component
 */
data class DateRangePickerTheme(
    val trailingIconTint: Color = CustomBlackColor,
    val dialogContainerColor: Color = Color.White,
    val dialogContentColor: Color = CustomBlackColor,
    val titleContentColor: Color = PrimaryColor,
    val headlineContentColor: Color = CustomBlackColor,
    val headlineTextStyle: TextStyle = TextStyle(fontSize = 23.sp),
    val weekdayContentColor: Color = CustomBlackColor,
    val subheadContentColor: Color = CustomBlackColor,
    val navigationContentColor: Color = CustomBlackColor,
    val dayContentColor: Color = CustomBlackColor,
    val selectedDayContainerColor: Color = PrimaryColor,
    val selectedDayContentColor: Color = Color.White,
    val todayContentColor: Color = PrimaryColor,
    val todayDateBorderColor: Color = PrimaryColor,
    val yearContentColor: Color = CustomBlackColor,
    val currentYearContentColor: Color = CustomBlackColor,
    val selectedYearContainerColor: Color = PrimaryColor,
    val selectedYearContentColor: Color = Color.White,
    val dividerColor: Color = CustomGrayColor,
    val confirmButtonTextColor: Color = PrimaryColor,
    val dismissButtonTextColor: Color = PrimaryColor
)

/**
 * Theme for CustomHeader component
 */
data class HeaderTheme(
    val containerPadding: PaddingValues = PaddingValues(horizontal = 13.dp, vertical = 12.dp),
    val backgroundColor: Color = Color.Transparent,
    val leftIconTint: Color = Color.White,
    val rightIconTint: Color = Color.White,
    val leftIconBackgroundColor: Color = PrimaryColor,
    val rightIconBackgroundColor: Color = PrimaryColor,
    val iconButtonSize: Dp = 54.dp,
    val icon: DrawableResource? = null,
    val iconContentDescription: String? = null,
    val iconTint: Color = CustomBlackColor,
    val iconSize: Dp = 24.dp
)

/**
 * Theme for OptionItem row
 */
data class OptionItemTheme(
    val backgroundColor: Color = Color.White,
    val horizontalPadding: Dp = 20.dp,
    val rowHeight: Dp = 50.dp,
    val textStyle: TextStyle = TextStyle(
        fontSize = 16.sp,
        color = CustomBlackColor
    ),
    val boldLeadingTextStyle: TextStyle = TextStyle(
        fontSize = 16.sp,
        color = CustomBlackColor,
        textDecoration = TextDecoration.Underline
    ),
    val leadingIconTint: Color = CustomBlackColor,
    val checkIconTint: Color = SecondaryColor,
    val checkIconBackgroundColor: Color = Color.White
)

/**
 * Theme for OptionItem list containers & dividers
 */
data class OptionItemListTheme(
    val containerColor: Color = Color.White,
    val dividerColor: Color = CustomGrayHintColor,
    val containerShape: Shape = RoundedCornerShape(18.dp),
    val lazyListScrollbarColor: Color = CustomGrayHintColor,
    val lazyListScrollbarWidth: Dp = 3.dp,
    val lazyListScrollbarTopPadding: Dp = 15.dp,
    val lazyListScrollbarBottomPadding: Dp = 15.dp
)

/**
 * Theme for ScrollbarWithScrollState component
 */
data class ScrollbarWithScrollStateTheme(
    val alwaysShowScrollBar: Boolean = false,
    val isScrollBarTrackVisible: Boolean = true,
    val scrollBarTrackColor: Color = Color.Gray,
    val scrollBarColor: Color = CustomGrayHintColor,
    val scrollBarWidth: Dp = 5.dp,
    val scrollBarCornerRadius: Float = 4f,
    val scrollBarEndPadding: Float = 12f,
    val scrollBarTopPadding: Dp = 0.dp,
    val scrollBarBottomPadding: Dp = 0.dp
)

/**
 * Theme for ScrollbarWithLazyListState component
 */
data class ScrollbarWithLazyListStateTheme(
    val alwaysShowScrollBar: Boolean = false,
    val isScrollBarTrackVisible: Boolean = true,
    val scrollBarTrackColor: Color = Color.Gray,
    val scrollBarColor: Color = PrimaryColor,
    val scrollBarWidth: Dp = 5.dp,
    val scrollBarCornerRadius: Float = 4f,
    val scrollBarEndPadding: Float = 12f,
    val scrollBarTopPadding: Dp = 0.dp,
    val scrollBarBottomPadding: Dp = 0.dp
)

/**
 * Theme for SearchField component
 */
data class SearchFieldTheme(
    val buttonSize: Dp = 42.dp,
    val buttonShape: Shape = RoundedCornerShape(16.dp),
    val buttonBackgroundColor: Color = PrimaryColor,
    val buttonIconTint: Color = Color.White,
    val buttonShadowElevation: Dp = 0.dp,
    val defaultButtonIconDescription: String = "Search"
)

/**
 * Theme for ProgressIndicatorBars component
 */
data class ProgressIndicatorBarsTheme(
    val filledIndicatorColor: Color = PrimaryColor,
    val defaultIndicatorColor: Color = CustomGrayHintColor,
    val indicatorHeight: Dp = 8.dp,
    val indicatorSpacing: Dp = 3.dp,
    val indicatorCornerRadius: Dp = 3.dp
)

/**
 * Theme for JsonViewer component
 */
data class JsonViewerTheme(
    val titleTextStyle: TextStyle = TextStyle(
        fontSize = 16.sp,
        color = CustomBlackColor
    ),
    val containerColor: Color = Color.White,
    val containerShape: CornerBasedShape = RoundedCornerShape(16.dp),
    val containerPadding: Dp = 20.dp,
    val jsonTextStyle: TextStyle = TextStyle(
        fontSize = 14.sp,
        color = CustomBlackColor
    ),
    val buttonBackgroundColor: Color = PrimaryColor,
    val buttonTextColor: Color = Color.White,
    val buttonTextStyle: TextStyle = TextStyle(
        fontSize = 12.sp
    ),
    val buttonShape: CornerBasedShape = RoundedCornerShape(12.dp),
    val buttonHeight: Dp = 40.dp,
    val buttonIconSize: Dp = 15.dp,
    val copyIconTint: Color = Color.White,
    val copiedIconTint: Color = Color.White
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
    val dialogHeader: DialogHeaderTheme = DialogHeaderTheme(),
    val iconButton: IconButtonTheme = IconButtonTheme(),
    val labeledSwitch: LabeledSwitchTheme = LabeledSwitchTheme(),
    val pickerField: PickerFieldTheme = PickerFieldTheme(),
    val customDropDownMenu: CustomDropDownMenuTheme = CustomDropDownMenuTheme(),
    val customTextField: CustomTextFieldTheme = CustomTextFieldTheme(),
    val datePicker: DatePickerTheme = DatePickerTheme(),
    val dateRangePicker: DateRangePickerTheme = DateRangePickerTheme(),
    val header: HeaderTheme = HeaderTheme(),
    val searchField: SearchFieldTheme = SearchFieldTheme(),
    val progressIndicatorBars: ProgressIndicatorBarsTheme = ProgressIndicatorBarsTheme(),
    val optionItem: OptionItemTheme = OptionItemTheme(),
    val optionItemList: OptionItemListTheme = OptionItemListTheme(),
    val scrollbarWithScrollState: ScrollbarWithScrollStateTheme = ScrollbarWithScrollStateTheme(),
    val scrollbarWithLazyListState: ScrollbarWithLazyListStateTheme = ScrollbarWithLazyListStateTheme(),
    val jsonViewer: JsonViewerTheme = JsonViewerTheme()
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

