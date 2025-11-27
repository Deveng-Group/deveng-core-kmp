package core.util.datetime

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
class CustomSelectableDates : SelectableDates {
    private var targetDates = TargetDates.FUTURE

    private val oldestDateTime = LocalDateTime(
        year = 1900,
        month = 1,
        day = 1,
        hour = 0,
        minute = 0
    )

    fun setTargetDates(targetDates: TargetDates) {
        this.targetDates = targetDates
    }

    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        val timeZone = TimeZone.currentSystemDefault()
        val todayDate = Clock.System.now().toLocalDateTime(timeZone).date
        val localDate = Instant.fromEpochMilliseconds(utcTimeMillis)
            .toLocalDateTime(timeZone).date

        val isTargetDate = when (targetDates) {
            TargetDates.PAST -> oldestDateTime.date <= localDate && localDate <= todayDate
            TargetDates.FUTURE -> localDate >= todayDate
        }

        return isTargetDate
    }

    override fun isSelectableYear(year: Int): Boolean {
        val timeZone = TimeZone.currentSystemDefault()
        val currentYear = Clock.System.now().toLocalDateTime(timeZone).year

        val isTargetYear = when (targetDates) {
            TargetDates.PAST -> oldestDateTime.year <= year && year <= currentYear
            TargetDates.FUTURE -> year >= currentYear
        }

        return isTargetYear
    }
}