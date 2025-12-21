package core.util.datetime

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.MonthNames.Companion.ENGLISH_ABBREVIATED
import kotlinx.datetime.format.MonthNames.Companion.ENGLISH_FULL
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.number

fun formatDateTime(date: LocalDateTime, isDaily: Boolean = false): String {
    return if (isDaily) date.format(hourMinuteFormat) else date.format(dotDateFormat)
}

val slashDateFormat = LocalDate.Format {
    day(padding = Padding.ZERO)
    char('/')
    monthNumber(padding = Padding.ZERO)
    char('/')
    year()
}

val dotDateFormat = LocalDateTime.Format {
    day(padding = Padding.ZERO)
    char('.')
    monthNumber(padding = Padding.ZERO)
    char('.')
    year()
}

val dotLocalDateFormat = LocalDate.Format {
    day(padding = Padding.ZERO)
    char('.')
    monthNumber(padding = Padding.ZERO)
    char('.')
    year()
}

private val TURKISH_DAYS = listOf(
    "Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma", "Cumartesi", "Pazar"
)

private val TURKISH_MONTH_ABBREVIATIONS = listOf(
    "Oca", "Şub", "Mar", "Nis", "May", "Haz",
    "Tem", "Ağu", "Eyl", "Eki", "Kas", "Ara"
)

private val TURKISH_MONTH = listOf(
    "Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran",
    "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"
)

fun getMonthAbbreviationDateFormat(systemLanguage: String): DateTimeFormat<LocalDate> {
    val monthNames = when (systemLanguage) {
        "tr" -> TURKISH_MONTH_ABBREVIATIONS
        else -> ENGLISH_ABBREVIATED.names
    }

    return LocalDate.Format {
        day(padding = Padding.ZERO)
        char(' ')
        monthName(names = MonthNames(names = monthNames))
        char(' ')
        year()
    }
}

fun getMonthDateFormat(systemLanguage: String): DateTimeFormat<LocalDate> {
    val monthNames = when (systemLanguage) {
        "tr" -> TURKISH_MONTH
        else -> ENGLISH_FULL.names
    }

    return LocalDate.Format {
        day(padding = Padding.ZERO)
        char(' ')
        monthName(names = MonthNames(names = monthNames))
    }
}

fun getMonthNames(systemLanguage: String): List<String> {
    return when (systemLanguage) {
        "tr" -> TURKISH_MONTH
        else -> ENGLISH_FULL.names
    }
}

fun getDayName(date: LocalDate, systemLanguage: String): String {
    val dayIndex = date.dayOfWeek.ordinal

    return when (systemLanguage) {
        "tr" -> TURKISH_DAYS[dayIndex]
        else -> {
            date.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }
        }
    }
}

val hourMinuteFormat = LocalDateTime.Format {
    hour(padding = Padding.ZERO)
    char(':')
    minute(padding = Padding.ZERO)
}

fun formatDateToString(date: LocalDate): String {
    val day = date.day.toString().padStart(2, '0')
    val month = date.month.number.toString().padStart(2, '0')
    val year = date.year.toString()
    return "$day$month$year"
}

/**
 *
 * Sample:
 * - dateTime = 2025-12-21T14:30
 * - systemLanguage = "tr"
 *   → "21 Aralık"
 *
 * - dateTime = 2025-12-21T14:30
 * - systemLanguage = "en"
 *   → "21 December"
 */
fun formatToDayMonth(
    dateTime: LocalDateTime,
    systemLanguage: String
): String {
    val formatter = getMonthDateFormat(systemLanguage)
    return dateTime.date.format(formatter)
}

fun formatDateRange(
    startDate: LocalDate?,
    endDate: LocalDate?,
    format: DateTimeFormat<LocalDate>
): String = buildString {
    val startDateFormatted = startDate?.format(format)
    val endDateFormatted = endDate?.format(format)
    if (startDateFormatted != null) append(startDateFormatted)
    if (startDateFormatted != null && endDateFormatted != null) append(" - ")
    if (endDateFormatted != null) append(endDateFormatted)
}