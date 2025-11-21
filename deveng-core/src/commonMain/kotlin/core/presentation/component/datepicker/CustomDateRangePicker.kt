package core.presentation.component.datepicker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import core.presentation.component.PickerField
import core.presentation.theme.LocalComponentTheme
import core.util.datetime.CustomSelectableDates
import core.util.datetime.TargetDates
import core.util.datetime.toEpochMillis
import global.deveng.deveng_core.generated.resources.Res
import global.deveng.deveng_core.generated.resources.date_picker
import global.deveng.deveng_core.generated.resources.shared_cancel
import global.deveng.deveng_core.generated.resources.shared_ic_calendar
import global.deveng.deveng_core.generated.resources.shared_ok
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun CustomDateRangePicker(
    modifier: Modifier = Modifier,
    title: String? = null,
    titleColor: Color? = null,
    enabledBorderWidth: Dp? = null,
    enabledBorderColor: Color? = null,
    enabledTextColor: Color? = null,
    rangeText: String,
    initialSelectedStartDate: LocalDate?,
    initialSelectedEndDate: LocalDate?,
    trailingIconModifier: Modifier = Modifier,
    trailingIconTint: Color? = null,
    errorMessage: String? = null,
    onRangeChange: (LocalDate?, LocalDate?) -> Unit,
    targetDates: TargetDates = TargetDates.FUTURE,
    selectableDates: CustomSelectableDates,
    dialogContainerColor: Color? = null,
    dialogContentColor: Color? = null,
    titleContentColor: Color? = null,
    headlineContentColor: Color? = null,
    headlineTextStyle: TextStyle? = null,
    weekdayContentColor: Color? = null,
    subheadContentColor: Color? = null,
    navigationContentColor: Color? = null,
    dayContentColor: Color? = null,
    selectedDayContainerColor: Color? = null,
    selectedDayContentColor: Color? = null,
    todayContentColor: Color? = null,
    todayDateBorderColor: Color? = null,
    yearContentColor: Color? = null,
    currentYearContentColor: Color? = null,
    selectedYearContainerColor: Color? = null,
    selectedYearContentColor: Color? = null,
    dividerColor: Color? = null,
    confirmButtonTextColor: Color? = null,
    dismissButtonTextColor: Color? = null
) {
    val componentTheme = LocalComponentTheme.current
    val dateRangePickerTheme = componentTheme.dateRangePicker
    val pickerFieldTheme = componentTheme.pickerField

    // PickerField theme values
    val finalTitleColor = titleColor ?: pickerFieldTheme.titleTextStyle.color
    val finalEnabledBorderWidth = enabledBorderWidth ?: pickerFieldTheme.enabledBorderWidth
    val finalEnabledBorderColor = enabledBorderColor ?: pickerFieldTheme.enabledBorderColor
    val finalEnabledTextColor = enabledTextColor ?: pickerFieldTheme.enabledTextColor
    val finalTrailingIconTint = trailingIconTint ?: dateRangePickerTheme.trailingIconTint

    // Dialog theme values
    val finalDialogContainerColor =
        dialogContainerColor ?: dateRangePickerTheme.dialogContainerColor
    val finalDialogContentColor = dialogContentColor ?: dateRangePickerTheme.dialogContentColor
    val finalTitleContentColor = titleContentColor ?: dateRangePickerTheme.titleContentColor
    val finalHeadlineContentColor =
        headlineContentColor ?: dateRangePickerTheme.headlineContentColor
    val finalHeadlineTextStyle = headlineTextStyle ?: dateRangePickerTheme.headlineTextStyle.copy(
        color = finalHeadlineContentColor
    )
    val finalWeekdayContentColor = weekdayContentColor ?: dateRangePickerTheme.weekdayContentColor
    val finalSubheadContentColor = subheadContentColor ?: dateRangePickerTheme.subheadContentColor
    val finalNavigationContentColor =
        navigationContentColor ?: dateRangePickerTheme.navigationContentColor
    val finalDayContentColor = dayContentColor ?: dateRangePickerTheme.dayContentColor
    val finalSelectedDayContainerColor =
        selectedDayContainerColor ?: dateRangePickerTheme.selectedDayContainerColor
    val finalSelectedDayContentColor =
        selectedDayContentColor ?: dateRangePickerTheme.selectedDayContentColor
    val finalTodayContentColor = todayContentColor ?: dateRangePickerTheme.todayContentColor
    val finalTodayDateBorderColor =
        todayDateBorderColor ?: dateRangePickerTheme.todayDateBorderColor
    val finalYearContentColor = yearContentColor ?: dateRangePickerTheme.yearContentColor
    val finalCurrentYearContentColor =
        currentYearContentColor ?: dateRangePickerTheme.currentYearContentColor
    val finalSelectedYearContainerColor =
        selectedYearContainerColor ?: dateRangePickerTheme.selectedYearContainerColor
    val finalSelectedYearContentColor =
        selectedYearContentColor ?: dateRangePickerTheme.selectedYearContentColor
    val finalDividerColor = dividerColor ?: dateRangePickerTheme.dividerColor
    val finalConfirmButtonTextColor =
        confirmButtonTextColor ?: dateRangePickerTheme.confirmButtonTextColor
    val finalDismissButtonTextColor =
        dismissButtonTextColor ?: dateRangePickerTheme.dismissButtonTextColor

    var showDialog by remember { mutableStateOf(false) }

    val localTimeZone = TimeZone.currentSystemDefault()

    LaunchedEffect(targetDates) {
        selectableDates.setTargetDates(targetDates)
    }

    if (showDialog) {
        val state = rememberDateRangePickerState(
            initialSelectedStartDateMillis = initialSelectedStartDate?.toEpochMillis(),
            initialSelectedEndDateMillis = initialSelectedEndDate?.toEpochMillis(),
            selectableDates = selectableDates
        )

        val startDate = state.selectedStartDateMillis?.let {
            Instant.fromEpochMilliseconds(it).toLocalDateTime(localTimeZone).date
        }

        val endDate = state.selectedEndDateMillis?.let {
            Instant.fromEpochMilliseconds(it).toLocalDateTime(localTimeZone).date
        }

        val pickerFormatter = remember {
            DatePickerDefaults.dateFormatter(
                selectedDateSkeleton = "EMMMd",
                selectedDateDescriptionSkeleton = "yMMMMEEEEd"
            )
        }

        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        onRangeChange(startDate, endDate)
                        showDialog = false
                    }
                ) {
                    Text(
                        text = stringResource(Res.string.shared_ok),
                        color = finalConfirmButtonTextColor
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(
                        text = stringResource(Res.string.shared_cancel),
                        color = finalDismissButtonTextColor
                    )
                }
            },
            colors = DatePickerDefaults.colors(containerColor = finalDialogContainerColor)
        ) {
            DateRangePicker(
                state = state,
                title = null,
                dateFormatter = pickerFormatter,
                headline = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ProvideTextStyle(finalHeadlineTextStyle) {
                            DateRangePickerDefaults.DateRangePickerHeadline(
                                selectedStartDateMillis = state.selectedStartDateMillis,
                                selectedEndDateMillis = state.selectedEndDateMillis,
                                displayMode = state.displayMode,
                                dateFormatter = pickerFormatter
                            )
                        }
                    }
                },
                colors = DatePickerDefaults.colors(
                    containerColor = finalDialogContainerColor,
                    titleContentColor = finalTitleContentColor,
                    headlineContentColor = finalHeadlineContentColor,
                    weekdayContentColor = finalWeekdayContentColor,
                    subheadContentColor = finalSubheadContentColor,
                    navigationContentColor = finalNavigationContentColor,
                    dayContentColor = finalDayContentColor,
                    selectedDayContentColor = finalSelectedDayContentColor,
                    selectedDayContainerColor = finalSelectedDayContainerColor,
                    todayContentColor = finalTodayContentColor,
                    todayDateBorderColor = finalTodayDateBorderColor,
                    yearContentColor = finalYearContentColor,
                    currentYearContentColor = finalCurrentYearContentColor,
                    selectedYearContentColor = finalSelectedYearContentColor,
                    selectedYearContainerColor = finalSelectedYearContainerColor,
                    dividerColor = finalDividerColor
                )
            )
        }
    }

    PickerField(
        modifier = modifier,
        title = title,
        titleColor = finalTitleColor,
        text = rangeText,
        enabledTextColor = finalEnabledTextColor,
        enabledBorderColor = finalEnabledBorderColor,
        enabledBorderWidth = finalEnabledBorderWidth,
        hint = "-",
        trailingSlot = {
            Icon(
                modifier = trailingIconModifier,
                painter = painterResource(Res.drawable.shared_ic_calendar),
                tint = finalTrailingIconTint,
                contentDescription = stringResource(Res.string.date_picker)
            )
        },
        onClick = { showDialog = true },
        errorMessage = errorMessage
    )
}
