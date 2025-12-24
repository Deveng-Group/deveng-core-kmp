package global.deveng.core

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import core.presentation.component.CustomButton
import core.presentation.component.CustomDropDownMenu
import core.presentation.component.CustomHeader
import core.presentation.component.CustomIconButton
import core.presentation.component.LabeledSwitch
import core.presentation.component.PickerField
import core.presentation.component.RoundedSurface
import core.presentation.component.SearchField
import core.presentation.component.alertdialog.CustomAlertDialog
import core.presentation.component.datepicker.CustomDatePicker
import core.presentation.component.datepicker.CustomDateRangePicker
import core.presentation.component.json.JsonViewer
import core.presentation.component.labeledslot.Label
import core.presentation.component.labeledslot.LabeledSlot
import core.presentation.component.navigationmenu.MenuMode
import core.presentation.component.navigationmenu.NavigationMenu
import core.presentation.component.navigationmenu.NavigationMenuItem
import core.presentation.component.optionitemlist.OptionItemLazyListDialog
import core.presentation.component.optionitemlist.OptionItemList
import core.presentation.component.optionitemlist.OptionItemMultiSelectLazyListDialog
import core.presentation.component.progressindicatorbars.IndicatorType
import core.presentation.component.progressindicatorbars.ProgressIndicatorBars
import core.presentation.component.scrollbar.scrollbarWithLazyListState
import core.presentation.component.textfield.CustomTextField
import core.presentation.component.textfield.DateTimeVisualTransformation
import core.presentation.theme.AlertDialogTheme
import core.presentation.theme.AppTheme
import core.presentation.theme.ButtonTheme
import core.presentation.theme.ComponentTheme
import core.presentation.theme.CoreBoldTextStyle
import core.presentation.theme.CoreMediumTextStyle
import core.presentation.theme.CoreRegularTextStyle
import core.presentation.theme.CoreSemiBoldTextStyle
import core.presentation.theme.CustomTextFieldTheme
import core.presentation.theme.DatePickerTheme
import core.presentation.theme.DateRangePickerTheme
import core.presentation.theme.HeaderTheme
import core.presentation.theme.IconButtonTheme
import core.presentation.theme.JsonViewerTheme
import core.presentation.theme.LabeledSwitchTheme
import core.presentation.theme.NavigationMenuTheme
import core.presentation.theme.OptionItemListTheme
import core.presentation.theme.OptionItemTheme
import core.presentation.theme.ProgressIndicatorBarsTheme
import core.presentation.theme.ScrollbarWithLazyListStateTheme
import core.presentation.theme.ScrollbarWithScrollStateTheme
import core.presentation.theme.SearchFieldTheme
import core.presentation.theme.SurfaceTheme
import core.presentation.theme.TypographyTheme
import core.util.datetime.CustomSelectableDates
import core.util.datetime.TargetDates
import core.util.datetime.formatDateRange
import core.util.datetime.slashDateFormat
import deveng_core_kmp.sample.composeapp.generated.resources.Res
import deveng_core_kmp.sample.composeapp.generated.resources.ic_cyclone
import deveng_core_kmp.sample.composeapp.generated.resources.ic_dark_mode
import deveng_core_kmp.sample.composeapp.generated.resources.ic_rotate_right
import deveng_core_kmp.sample.composeapp.generated.resources.theme
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import org.jetbrains.compose.resources.stringResource
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
            defaultTextStyle = CoreSemiBoldTextStyle().copy(
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
            titleTextStyle = CoreBoldTextStyle().copy(fontSize = 20.sp),
            descriptionTextStyle = CoreRegularTextStyle().copy(fontSize = 16.sp),
            buttonTextStyle = CoreMediumTextStyle().copy(fontSize = 16.sp)
        ),
        // Custom surface theme
        surface = SurfaceTheme(
            defaultColor = Color(0xFFF9F9F9),
            defaultContentColor = Color(0xFF1A1C1C)
        ),
        iconButton = IconButtonTheme(
            buttonSize = 48.dp,
            backgroundColor = Color(0xFFFDECEA),
            iconTint = Color(0xFFD32F2F),
            shadowElevation = 4.dp
        ),
        labeledSwitch = LabeledSwitchTheme(
            labelTextStyle = CoreMediumTextStyle().copy(
                fontSize = 15.sp,
                color = Color(0xFF0F172A)
            ),
            checkedTrackColor = Color(0xFF1976D2),
            uncheckedTrackColor = Color(0xFFE0E7FF),
            uncheckedBorderColor = Color(0xFF1976D2),
            switchScale = 0.85f
        ),
        optionItem = OptionItemTheme(
            backgroundColor = Color.White,
            textStyle = CoreMediumTextStyle().copy(
                fontSize = 16.sp,
                color = Color(0xFF0F172A)
            ),
            boldLeadingTextStyle = CoreBoldTextStyle().copy(
                fontSize = 16.sp,
                color = Color(0xFFD32F2F),
                textDecoration = TextDecoration.Underline
            ),
            checkIconTint = Color(0xFFD32F2F)
        ),
        optionItemList = OptionItemListTheme(
            containerColor = Color(0xFFFFF3E0),
            dividerColor = Color(0xFFFFCC80),
            containerShape = RoundedCornerShape(24.dp),
            lazyListScrollbarColor = Color.Red,
            lazyListScrollbarWidth = 6.dp
        ),
        customTextField = CustomTextFieldTheme(
            containerShape = RoundedCornerShape(20.dp),
            borderStroke = BorderStroke(1.dp, Color(0xFFE0E7FF)),
            containerColor = Color.White,
            textStyle = CoreMediumTextStyle().copy(
                fontSize = 16.sp,
                color = Color(0xFF0F172A)
            ),
            hintTextStyle = CoreMediumTextStyle().copy(
                fontSize = 16.sp,
                color = Color(0xFF94A3B8)
            ),
            cursorColor = Color(0xFF1976D2)
        ),
        datePicker = DatePickerTheme(
            trailingIconTint = Color(0xFF1976D2),
            dialogContainerColor = Color(0xFF0F172A),
            dialogContentColor = Color.White,
            selectedDayContainerColor = Color.White,
            selectedDayContentColor = Color(0xFF0F172A),
            selectedYearContainerColor = Color.White,
            selectedYearContentColor = Color(0xFF0F172A)
        ),
        dateRangePicker = DateRangePickerTheme(
            trailingIconTint = Color(0xFF1976D2),
            dialogContainerColor = Color.White,
            titleContentColor = Color(0xFF1976D2),
            selectedDayContainerColor = Color(0xFF1976D2),
            selectedDayContentColor = Color.White,
            todayContentColor = Color(0xFF1976D2),
            todayDateBorderColor = Color(0xFF1976D2),
            selectedYearContainerColor = Color(0xFF1976D2),
            selectedYearContentColor = Color.White,
            confirmButtonTextColor = Color(0xFF1976D2),
            dismissButtonTextColor = Color(0xFF1976D2)
        ),
        scrollbarWithScrollState = ScrollbarWithScrollStateTheme(
            scrollBarColor = Color.Red,
            scrollBarWidth = 15.dp,
            alwaysShowScrollBar = true
        ),
        scrollbarWithLazyListState = ScrollbarWithLazyListStateTheme(
            scrollBarColor = Color.Red,
            scrollBarWidth = 15.dp,
            alwaysShowScrollBar = true
        ),
        header = HeaderTheme(
            containerPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            backgroundColor = Color(0xFFF5F5F5),
            leftIconTint = Color(0xFF1976D2),
            rightIconTint = Color(0xFF1976D2),
            leftIconBackgroundColor = Color(0xFFE3F2FD),
            rightIconBackgroundColor = Color(0xFFE3F2FD),
            iconButtonSize = 48.dp
        ),
        searchField = SearchFieldTheme(
            buttonSize = 42.dp,
            buttonShape = RoundedCornerShape(16.dp),
            buttonBackgroundColor = Color(0xFF1976D2),
            buttonIconTint = Color.White,
            buttonShadowElevation = 0.dp,
            defaultButtonIconDescription = "Search"
        ),
        progressIndicatorBars = ProgressIndicatorBarsTheme(
            filledIndicatorColor = Color(0xFF1976D2),
            defaultIndicatorColor = Color(0xFFE0E7FF),
            indicatorHeight = 8.dp,
            indicatorSpacing = 3.dp,
            indicatorCornerRadius = 3.dp
        ),
        jsonViewer = JsonViewerTheme(
            containerColor = Color(0xFFF9F9F9),
            buttonBackgroundColor = Color(0xFF1976D2),
            buttonTextColor = Color.White
        ),
        navigationMenu = NavigationMenuTheme(
            backgroundColor = Color(0xFF111827),
            sectionSeparatorColor = Color(0xFF1F2937),
            itemSelectedBackgroundColor = Color(0xFF111827),
            itemUnselectedBackgroundColor = Color(0xFF111827),
            verticalDividerColor = Color(0xFF374151)
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
    var showOptionDialog by remember { mutableStateOf(false) }
    var showMultiSelectDialog by remember { mutableStateOf(false) }
    val sampleOptions = remember {
        listOf(
            "Themed option A",
            "Themed option B",
            "Themed option C",
            "Themed option D"
        )
    }
    var selectedOption by remember { mutableStateOf(sampleOptions.first()) }
    var selectedOptions by remember { mutableStateOf(setOf<String>()) }
    var labeledSwitchEnabled by remember { mutableStateOf(true) }
    var labeledSwitchCustom by remember { mutableStateOf(false) }
    var textFieldValue by remember { mutableStateOf("") }
    var passwordValue by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var pickerSelection by remember { mutableStateOf<String?>(null) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedStartDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedEndDate by remember { mutableStateOf<LocalDate?>(null) }
    var searchText by remember { mutableStateOf("") }
    var amountValue by remember { mutableStateOf("125") }
    var borderOverrideValue by remember { mutableStateOf("") }
    var dateTimeValue by remember { mutableStateOf("") }
    var isJsonCopied by remember { mutableStateOf(false) }
    var currentPage by remember { mutableStateOf(0) }
    var isExpanded by remember { mutableStateOf(false) }
    val selectableDates = remember { CustomSelectableDates() }
    val selectableDatesPast = remember { CustomSelectableDates() }
    val selectableDatesFuture = remember { CustomSelectableDates() }
    val priorityOptions = remember {
        listOf("Critical", "High", "Medium", "Low")
    }
    val statusOptions = remember {
        listOf(
            "Open",
            "In Progress",
            "Resolved",
            "Resolved",
            "Resolved",
            "Resolved",
            "Resolved",
            "Resolved",
            "Resolved",
            "Resolved",
            "Resolved"
        )
    }
    var selectedPriority by remember { mutableStateOf<String?>(null) }
    var selectedStatus by remember { mutableStateOf<String?>(null) }
    val lazyListState = rememberLazyListState()

    val navigationMenuItems = listOf(
        NavigationMenuItem(
            screenRoute = "dashboard",
            icon = Res.drawable.ic_rotate_right,
            iconTint = Color.White,
            iconDescription = Res.string.theme,
            text = Res.string.theme,
            textColor = Color.White
        ),
        NavigationMenuItem(
            screenRoute = "logs",
            icon = Res.drawable.ic_dark_mode,
            iconTint = Color.White,
            iconDescription = Res.string.theme,
            text = Res.string.theme,
            textColor = Color.White
        ),
        NavigationMenuItem(
            screenRoute = "dashboard",
            icon = Res.drawable.ic_rotate_right,
            iconTint = Color.White,
            iconDescription = Res.string.theme,
            text = Res.string.theme,
            textColor = Color.White
        ),
        NavigationMenuItem(
            screenRoute = "logs",
            icon = Res.drawable.ic_dark_mode,
            iconTint = Color.White,
            iconDescription = Res.string.theme,
            text = Res.string.theme,
            textColor = Color.White
        )
    )

    Column {
        NavigationMenu(
            isExpanded = isExpanded,
            menuMode = MenuMode.Horizontal,
            itemList = navigationMenuItems,
            expandedLeadingSlot = {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Navigation",
                        style = CoreSemiBoldTextStyle().copy(
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Themed side menu using ComponentTheme",
                        style = CoreRegularTextStyle().copy(
                            fontSize = 12.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    )
                }
            },
            expandedTrailingSlot = {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "v1.0.0",
                        style = CoreRegularTextStyle().copy(
                            fontSize = 12.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    )
                }
            },
            collapsedLeadingSlot = {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "N",
                        style = CoreSemiBoldTextStyle().copy(
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    )
                }
            },
            collapsedTrailingSlot = {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "v1",
                        style = CoreRegularTextStyle().copy(
                            fontSize = 12.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    )
                }
            },
            onItemClick = { },
            isItemSelected = { true },
            itemText = { stringResource(it.text) },
            itemTextStyle = { CoreMediumTextStyle().copy() },
            itemIcon = { it.icon },
            itemIconTint = { it.iconTint },
            itemIconDescription = { stringResource(it.iconDescription) }
        )

        Row {
            NavigationMenu(
                isExpanded = isExpanded,
                menuMode = MenuMode.Vertical,
                itemList = navigationMenuItems,
                expandedLeadingSlot = {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Navigation",
                            style = CoreSemiBoldTextStyle().copy(
                                fontSize = 18.sp,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "Themed side menu using ComponentTheme",
                            style = CoreRegularTextStyle().copy(
                                fontSize = 12.sp,
                                color = Color(0xFF9CA3AF)
                            )
                        )
                    }
                },
                expandedTrailingSlot = {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "v1.0.0",
                            style = CoreRegularTextStyle().copy(
                                fontSize = 12.sp,
                                color = Color(0xFF9CA3AF)
                            )
                        )
                    }
                },
                collapsedLeadingSlot = {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "N",
                            style = CoreSemiBoldTextStyle().copy(
                                fontSize = 18.sp,
                                color = Color.White
                            )
                        )
                    }
                },
                collapsedTrailingSlot = {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "v1",
                            style = CoreRegularTextStyle().copy(
                                fontSize = 12.sp,
                                color = Color(0xFF9CA3AF)
                            )
                        )
                    }
                },
                onItemClick = { },
                isItemSelected = { true },
                itemText = { stringResource(it.text) },
                itemTextStyle = { CoreMediumTextStyle().copy() },
                itemIcon = { it.icon },
                itemIconTint = { it.iconTint },
                itemIconDescription = { stringResource(it.iconDescription) }
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .scrollbarWithLazyListState(lazyListState),
                state = lazyListState,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(16.dp),
            ) {
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "ComponentTheme Demo",
                            style = CoreBoldTextStyle().copy(fontSize = 24.sp),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        SectionTitle("CustomButton Examples")

                        CustomButton(
                            text = "Side Menu Opener and Closer",
                            onClick = { isExpanded = !isExpanded }
                        )

                        CustomButton(
                            text = "Themed Button",
                            onClick = { }
                        )

                        CustomButton(
                            text = "Overridden Color",
                            containerColor = Color(0xFFE91E63),
                            onClick = { }
                        )

                        CustomButton(
                            text = "Custom Typography",
                            textStyle = CoreBoldTextStyle().copy(fontSize = 20.sp),
                            onClick = { }
                        )

                        CustomButton(
                            text = "Disabled Button",
                            enabled = false,
                            onClick = { }
                        )

                        SectionTitle("RoundedSurface Examples")

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
                                    style = CoreMediumTextStyle().copy(fontSize = 16.sp)
                                )
                            }
                        }

                        RoundedSurface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            color = Color(0xFFE3F2FD),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Custom Color Surface",
                                    style = CoreSemiBoldTextStyle().copy(fontSize = 16.sp)
                                )
                            }
                        }

                        SectionTitle("LabeledSlot Examples")

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            LabeledSlot(
                                labels = listOf(
                                    Label(
                                        text = "NEW",
                                        alignment = Alignment.TopEnd,
                                        backgroundColor = Color(0xFFD32F2F),
                                        textColor = Color.White,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                ),
                                contentSlot = {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.LightGray),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Image",
                                            style = CoreMediumTextStyle().copy(fontSize = 14.sp)
                                        )
                                    }
                                }
                            )

                            LabeledSlot(
                                labels = listOf(
                                    Label(
                                        text = "SALE",
                                        alignment = Alignment.TopStart,
                                        backgroundColor = Color(0xFF1976D2),
                                        textColor = Color.White,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                ),
                                contentSlot = {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.LightGray),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Image",
                                            style = CoreMediumTextStyle().copy(fontSize = 14.sp)
                                        )
                                    }
                                }
                            )
                        }

                        LabeledSlot(
                            labels = listOf(
                                Label(
                                    text = "TOP",
                                    alignment = Alignment.TopStart,
                                    backgroundColor = Color(0xFF4CAF50),
                                    textColor = Color.White,
                                    shape = RoundedCornerShape(8.dp)
                                ),
                                Label(
                                    text = "HOT",
                                    alignment = Alignment.TopEnd,
                                    backgroundColor = Color(0xFFE91E63),
                                    textColor = Color.White,
                                    shape = RoundedCornerShape(8.dp)
                                )
                            ),
                            contentSlot = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.LightGray),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Multiple Labels",
                                        style = CoreMediumTextStyle().copy(fontSize = 14.sp)
                                    )
                                }
                            }
                        )

                        LabeledSlot(
                            modifier = Modifier.fillMaxWidth(),
                            labels = listOf(
                                Label(
                                    text = "CUSTOM",
                                    alignment = Alignment.BottomCenter,
                                    backgroundColor = Color(0xFF1976D2),
                                    textColor = Color.White,
                                    shape = RoundedCornerShape(12.dp)
                                )
                            ),
                            containerWidth = null,
                            containerHeight = 120.dp,
                            containerShape = RoundedCornerShape(20.dp),
                            contentSlot = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color(0xFFE3F2FD)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Custom Size & Shape",
                                        style = CoreSemiBoldTextStyle().copy(fontSize = 16.sp)
                                    )
                                }
                            }
                        )

                        SectionTitle("Typography Examples")

                        Text(
                            text = "Regular Text (400)",
                            style = CoreRegularTextStyle().copy(fontSize = 16.sp)
                        )

                        Text(
                            text = "Medium Text (500)",
                            style = CoreMediumTextStyle().copy(fontSize = 16.sp)
                        )

                        Text(
                            text = "SemiBold Text (600)",
                            style = CoreSemiBoldTextStyle().copy(fontSize = 16.sp)
                        )

                        Text(
                            text = "Bold Text (700)",
                            style = CoreBoldTextStyle().copy(fontSize = 16.sp)
                        )

                        Text(
                            text = "Custom Styled Text",
                            style = CoreSemiBoldTextStyle().copy(
                                fontSize = 18.sp,
                                color = Color(0xFF1976D2)
                            )
                        )

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

                        SectionTitle("Scrollbar Examples")

                        Text(
                            text = "Entire page now uses a LazyColumn with the red 15.dp scrollbar.",
                            style = CoreRegularTextStyle().copy(fontSize = 14.sp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        SectionTitle("OptionItemList Examples")

                        OptionItemList(
                            optionList = sampleOptions,
                            optionText = { it },
                            optionId = { it.hashCode() },
                            isCheckIconsVisible = true,
                            selectedOption = selectedOption,
                            onOptionItemClick = {
                                selectedOption = it
                            }
                        )

                        CustomButton(
                            text = "Show Option Dialog",
                            onClick = { showOptionDialog = true }
                        )

                        SectionTitle("LabeledSwitch Examples")

                        LabeledSwitch(
                            label = "Notifications",
                            isChecked = labeledSwitchEnabled,
                            onSwitchClick = { labeledSwitchEnabled = it }
                        )

                        LabeledSwitch(
                            label = "Custom Colored Switch",
                            isChecked = labeledSwitchCustom,
                            checkedTrackColor = Color(0xFFE91E63),
                            uncheckedThumbColor = Color(0xFFE91E63),
                            uncheckedTrackColor = Color.White,
                            switchScale = 1f,
                            onSwitchClick = { labeledSwitchCustom = it }
                        )

                        SectionTitle("CustomTextField Examples")

                        val shouldShowNameError =
                            textFieldValue.isNotEmpty() && textFieldValue.length < 3

                        CustomTextField(
                            title = "Name",
                            value = textFieldValue,
                            hint = "Enter at least 3 characters",
                            isTextCharCountVisible = true,
                            errorMessage = if (shouldShowNameError) "Minimum 3 characters" else null,
                            onValueChange = { textFieldValue = it }
                        )

                        CustomTextField(
                            title = "Password",
                            value = passwordValue,
                            hint = "Password",
                            keyboardType = KeyboardType.Password,
                            isPasswordVisible = isPasswordVisible,
                            onPasswordToggleClick = { isPasswordVisible = it },
                            onValueChange = { passwordValue = it }
                        )

                        CustomTextField(
                            title = "Inline Suffix Field",
                            value = amountValue,
                            hint = "Enter amount",
                            inlineSuffix = " kg",
                            suffixSlot = {
                                Text(
                                    text = "kg",
                                    style = CoreMediumTextStyle().copy(color = Color(0xFF0F172A))
                                )
                            },
                            titleTrailingSlot = {
                                Text(
                                    text = "Max 3 digits",
                                    style = CoreRegularTextStyle().copy(
                                        fontSize = 12.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                )
                            },
                            maxLength = 3,
                            onValueChange = { newValue ->
                                amountValue = newValue.filter { it.isDigit() }
                            }
                        )

                        CustomTextField(
                            title = "Border Overrides",
                            value = borderOverrideValue,
                            hint = "Custom focus colors",
                            focusedBorderColor = Color(0xFF1976D2),
                            unfocusedBorderColor = Color(0xFF94A3B8),
                            focusedBorderWidth = 2.dp,
                            unfocusedBorderWidth = 1.dp,
                            onValueChange = { borderOverrideValue = it }
                        )

                        CustomTextField(
                            title = "Date Time",
                            value = dateTimeValue,
                            hint = "DD-MM-YYYY HH:MM",
                            maxLength = 12,
                            keyboardType = KeyboardType.Number,
                            visualTransformation = DateTimeVisualTransformation(),
                            onValueChange = { newValue ->
                                dateTimeValue = newValue.filter { it.isDigit() }
                            }
                        )

                        SectionTitle("CustomDropDownMenu Examples")

                        CustomDropDownMenu(
                            title = "Priority",
                            hintMessage = "Select priority",
                            items = priorityOptions,
                            selectedItem = selectedPriority,
                            onItemSelected = { selectedPriority = it },
                            isScrollBarVisible = false
                        )

                        CustomDropDownMenu(
                            title = "Status",
                            hintMessage = "Pick status",
                            backgroundColor = Color(0xFF111827),
                            textColor = Color(0xFFF8FAFC),
                            unfocusedBorderColor = Color(0xFF475569),
                            focusedBorderColor = Color(0xFF38BDF8),
                            dividerColor = Color(0xFF1E293B),
                            scrollBarColor = Color(0xFF38BDF8),
                            isScrollBarVisible = true,
                            items = statusOptions,
                            selectedItem = selectedStatus,
                            onItemSelected = { selectedStatus = it }
                        )

                        SectionTitle("JsonViewer Examples")

                        val sampleJson = remember {
                            """
                    {
                        "id": 1,
                        "name": "John Doe",
                        "email": "john@example.com",
                        "age": 28,
                        "isActive": true,
                        "address": {
                            "street": "123 Main St",
                            "city": "New York",
                            "zipCode": "10001"
                        },
                        "tags": ["developer", "kotlin", "compose"]
                    }
                    """.trimIndent()
                        }

                        JsonViewer(
                            title = "User Data",
                            json = sampleJson,
                            copyText = "Copy",
                            copyIcon = Res.drawable.ic_cyclone,
                            copyIconDescription = "",
                            copiedText = "Copied",
                            copiedIcon = Res.drawable.ic_cyclone,
                            copiedIconDescription = "",
                            isJsonCopied = isJsonCopied,
                            onClickCopyJsonIcon = { isJsonCopied = true }
                        )

                        JsonViewer(
                            title = "Custom Styled JSON",
                            json = """{"status":"success","data":{"count":42}}""",
                            containerColor = Color(0xFFF5F5F5),
                            buttonColor = Color(0xFF1976D2),
                            buttonTextColor = Color.White,
                            copyText = "Copy JSON",
                            copyIcon = Res.drawable.ic_cyclone,
                            copyIconDescription = "",
                            copiedText = "Copied!",
                            copiedIcon = Res.drawable.ic_cyclone,
                            copiedIconDescription = "",
                            isJsonCopied = isJsonCopied,
                            onClickCopyJsonIcon = { isJsonCopied = true }
                        )

                        SectionTitle("SearchField Examples")

                        SearchField(
                            searchText = searchText,
                            onSearchTextChange = { searchText = it },
                            onSearchButtonClick = { },
                            searchBarHint = "Search...",
                            buttonIcon = Res.drawable.ic_cyclone,
                            buttonIconDescription = "Search"
                        )

                        SearchField(
                            searchText = searchText,
                            onSearchTextChange = { searchText = it },
                            onSearchButtonClick = { },
                            searchBarHint = "Search with button at start",
                            isButtonAtEnd = false,
                            buttonIcon = Res.drawable.ic_cyclone,
                            buttonIconDescription = "Search",
                            buttonBackgroundColor = Color(0xFFE91E63),
                            buttonIconTint = Color.White
                        )

                        SearchField(
                            searchText = searchText,
                            onSearchTextChange = { searchText = it },
                            onSearchButtonClick = { },
                            searchBarHint = "Custom styled search",
                            buttonIcon = Res.drawable.ic_cyclone,
                            buttonIconDescription = "Search",
                            buttonSize = 50.dp,
                            buttonShape = RoundedCornerShape(12.dp),
                            buttonBackgroundColor = Color(0xFF4CAF50),
                            textFieldShape = RoundedCornerShape(20.dp)
                        )

                        SectionTitle("PickerField Examples")

                        PickerField(
                            title = "Select Option",
                            text = pickerSelection,
                            hint = "Tap to choose",
                            onClick = {
                                pickerSelection = sampleOptions.random()
                            }
                        )

                        PickerField(
                            title = "Unavailable Field",
                            text = null,
                            hint = "Coming soon",
                            isEnabled = false,
                            errorMessage = "Currently unavailable",
                            onClick = {}
                        )

                        SectionTitle("CustomDatePicker Examples")

                        val selectedDateText = remember(selectedDate) {
                            selectedDate?.format(slashDateFormat)
                        }

                        CustomDatePicker(
                            title = "Select a future date",
                            selectedDate = selectedDate,
                            placeholderText = "Tap to choose",
                            targetDates = TargetDates.FUTURE,
                            selectedDateText = selectedDateText,
                            selectableDates = selectableDates,
                            onDateChange = { selectedDate = it }
                        )

                        SectionTitle("CustomDateRangePicker Examples")

                        val dateRangeText = remember(selectedStartDate, selectedEndDate) {
                            formatDateRange(selectedStartDate, selectedEndDate, slashDateFormat)
                        }

                        CustomDateRangePicker(
                            title = "Past Date Range",
                            rangeText = dateRangeText,
                            initialSelectedStartDate = selectedStartDate,
                            initialSelectedEndDate = selectedEndDate,
                            targetDates = TargetDates.PAST,
                            selectableDates = selectableDatesPast,
                            onRangeChange = { start, end ->
                                selectedStartDate = start
                                selectedEndDate = end
                            }
                        )

                        CustomDateRangePicker(
                            title = "Future Date Range",
                            rangeText = dateRangeText,
                            initialSelectedStartDate = selectedStartDate,
                            initialSelectedEndDate = selectedEndDate,
                            targetDates = TargetDates.FUTURE,
                            selectableDates = selectableDatesFuture,
                            enabledBorderWidth = 1.dp,
                            enabledBorderColor = Color(0xFF1976D2),
                            onRangeChange = { start, end ->
                                selectedStartDate = start
                                selectedEndDate = end
                            }
                        )

                        SectionTitle("OptionItem Multi-Select Dialog")

                        Text(
                            text = "Selected items: ${
                                selectedOptions.joinToString().ifBlank { "None" }
                            }",
                            style = CoreRegularTextStyle().copy(fontSize = 14.sp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        CustomButton(
                            text = "Show Multi-Select Dialog",
                            onClick = { showMultiSelectDialog = true }
                        )

                        SectionTitle("CustomHeader Examples")

                        CustomHeader(
                            onLeftIconClick = { },
                            centerIcon = Res.drawable.ic_cyclone
                        )

                        CustomHeader(
                            isRightIconButtonVisible = true,
                            rightIcon = Res.drawable.ic_cyclone,
                            rightIconDescription = "Right Action",
                            onLeftIconClick = { },
                            onRightIconClick = { }
                        )

                        CustomHeader(
                            backgroundColor = Color(0xFF1976D2),
                            leftIconTint = Color.White,
                            leftIconBackgroundColor = Color(0xFF0D47A1),
                            onLeftIconClick = { }
                        )

                        CustomHeader(
                            isLeftIconButtonVisible = false,
                            isRightIconButtonVisible = true,
                            rightIcon = Res.drawable.ic_cyclone,
                            rightIconDescription = "Menu",
                            onRightIconClick = { }
                        )

                        SectionTitle("CustomIconButton Examples")

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CustomIconButton(
                                icon = Res.drawable.ic_cyclone,
                                iconDescription = "Forward",
                                onClick = { }
                            )

                            CustomIconButton(
                                buttonSize = 60.dp,
                                backgroundColor = Color(0xFF1976D2),
                                iconTint = Color.White,
                                shadowElevation = 6.dp,
                                icon = Res.drawable.ic_cyclone,
                                iconDescription = "Primary Forward",
                                onClick = { }
                            )
                        }

                        Text(
                            text = "Scrollbar Example",
                            style = CoreRegularTextStyle().copy(fontSize = 14.sp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        SectionTitle("ProgressIndicatorBars Examples")

                        ProgressIndicatorBars(
                            pageCount = 5,
                            currentPage = currentPage,
                            indicatorType = IndicatorType.HIGH_LIGHT_CURRENT
                        )

                        ProgressIndicatorBars(
                            pageCount = 5,
                            currentPage = currentPage,
                            indicatorType = IndicatorType.HIGH_LIGHT_UNTIL_CURRENT
                        )

                        ProgressIndicatorBars(
                            pageCount = 3,
                            currentPage = currentPage,
                            indicatorType = IndicatorType.HIGH_LIGHT_CURRENT,
                            filledIndicatorColor = Color(0xFFE91E63),
                            defaultIndicatorColor = Color(0xFFFFCDD2),
                            indicatorHeight = 10.dp,
                            indicatorSpacing = 5.dp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CustomButton(
                                text = "Previous",
                                enabled = currentPage > 0,
                                onClick = { if (currentPage > 0) currentPage-- }
                            )
                            CustomButton(
                                text = "Next",
                                enabled = currentPage < 4,
                                onClick = { if (currentPage < 4) currentPage++ }
                            )
                        }
                    }
                }

                items(30) { index ->
                    RoundedSurface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Scrollable item #$index",
                            modifier = Modifier.padding(16.dp),
                            style = CoreMediumTextStyle().copy(fontSize = 16.sp)
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            OptionItemLazyListDialog(
                optionsList = sampleOptions,
                optionText = { it },
                optionId = { it.hashCode() },
                isDialogVisible = showOptionDialog,
                selectedOption = selectedOption,
                onOptionItemClick = {
                    selectedOption = it
                    showOptionDialog = false
                },
                onDismissRequest = { showOptionDialog = false }
            )

            OptionItemMultiSelectLazyListDialog(
                optionsList = sampleOptions,
                optionText = { it },
                optionId = { it.hashCode() },
                isDialogVisible = showMultiSelectDialog,
                selectedOptions = selectedOptions.toList(),
                onOptionItemClick = { item ->
                    selectedOptions = if (selectedOptions.contains(item)) {
                        selectedOptions - item
                    } else {
                        selectedOptions + item
                    }
                },
                onSaveButtonClick = { showMultiSelectDialog = false },
                onDismissRequest = { showMultiSelectDialog = false }
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = CoreSemiBoldTextStyle().copy(
            fontSize = 18.sp,
            color = Color(0xFF1976D2)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}
