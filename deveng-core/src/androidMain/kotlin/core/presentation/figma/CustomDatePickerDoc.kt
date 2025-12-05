package core.presentation.figma

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.figma.code.connect.Figma
import com.figma.code.connect.FigmaConnect
import com.figma.code.connect.FigmaProperty
import com.figma.code.connect.FigmaType
import core.presentation.component.datepicker.CustomDatePicker
import core.presentation.theme.CoreCustomBlackColor
import core.util.datetime.CustomSelectableDates
import core.util.datetime.TargetDates
import global.deveng.deveng_core.generated.resources.Res
import global.deveng.deveng_core.generated.resources.date_picker
import global.deveng.deveng_core.generated.resources.shared_ic_calendar
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@FigmaConnect(
    url = "https://www.figma.com/design/sJoAsKB4qqqrwvHRlowppo/Design-System?node-id=2-14&t=JntFNGouWhSDj0EP-0"
)
class CustomDatePickerDoc {
    // --- INTERNAL ENUMS MIRRORING FIGMA VARIANTS / PROPS ---

    enum class TargetDatesVariant { Past, Future }

    enum class SlotPresence { None, Leading, Trailing, Both }

    // --- FIGMA-PROPERTIES (MAPPED TO FIGMA COMPONENT PROPS) ---

    @FigmaProperty(FigmaType.Text, "Selected date")
    val selectedDateText: String? = null

    @FigmaProperty(FigmaType.Text, "Title")
    val title: String = "Select date"

    @FigmaProperty(FigmaType.Text, "Placeholder")
    val placeholderText: String = "-"

    @FigmaProperty(FigmaType.Text, "Error message")
    val errorMessage: String? = null

    @FigmaProperty(FigmaType.Enum, "Target dates")
    val targetDatesVariant: TargetDatesVariant = Figma.mapping(
        "Past" to TargetDatesVariant.Past,
        "Future" to TargetDatesVariant.Future
    )

    @FigmaProperty(FigmaType.Enum, "Slots")
    val slotPresence: SlotPresence = Figma.mapping(
        "None" to SlotPresence.None,
        "Leading" to SlotPresence.Leading,
        "Trailing" to SlotPresence.Trailing,
        "Both" to SlotPresence.Both
    )

    // --- DERIVED VALUES (NOT DIRECTLY FIGMA PROPS, BUT NEEDED FOR FULL API) ---

    // 1. modifier
    val modifier: Modifier = Modifier

    // 2. trailingIconModifier
    val trailingIconModifier: Modifier = Modifier

    // 3. selectedDate (parsed from selectedDateText if provided)
    val selectedDate: LocalDate?
        get() = selectedDateText?.let {
            try {
                LocalDate.parse(it)
            } catch (e: Exception) {
                null
            }
        }

    // 4. targetDates
    val targetDates: TargetDates
        get() = when (targetDatesVariant) {
            TargetDatesVariant.Past -> TargetDates.PAST
            TargetDatesVariant.Future -> TargetDates.FUTURE
        }

    // 5. slots
    val leadingSlot: @Composable (() -> Unit)?
        get() = when (slotPresence) {
            SlotPresence.Leading, SlotPresence.Both -> {
                {
                    Icon(
                        painter = painterResource(Res.drawable.shared_ic_calendar),
                        contentDescription = null
                    )
                }
            }
            else -> null
        }

    // 6. colors (null = use theme defaults)
    val trailingIconTint: Color? = null

    val trailingSlot: @Composable (() -> Unit)?
        get() = when (slotPresence) {
            SlotPresence.Trailing, SlotPresence.Both -> {
                {
                    Icon(
                        modifier = trailingIconModifier,
                        painter = painterResource(Res.drawable.shared_ic_calendar),
                        tint = trailingIconTint ?: CoreCustomBlackColor,
                        contentDescription = stringResource(Res.string.date_picker)
                    )
                }
            }
            else -> null
        }
    val dialogContainerColor: Color? = null
    val dialogContentColor: Color? = null
    val selectedDayContainerColor: Color? = null
    val selectedDayContentColor: Color? = null
    val selectedYearContainerColor: Color? = null
    val selectedYearContentColor: Color? = null
    val todayContentColor: Color? = null
    val todayDateBorderColor: Color? = null
    val confirmButtonTextColor: Color? = null
    val dismissButtonTextColor: Color? = null

    // 7. callback
    val onDateChange: (LocalDate) -> Unit = {}

    // --- THE COMPOSABLE SNIPPET (USES *ALL* PARAMETERS EXPLICITLY) ---

    @Composable
    fun Component() {
        val selectableDates = remember { CustomSelectableDates() }

        CustomDatePicker(
            modifier = modifier,
            trailingIconModifier = trailingIconModifier,
            selectedDate = selectedDate,
            onDateChange = onDateChange,
            targetDates = targetDates,
            title = title,
            placeholderText = placeholderText,
            selectedDateText = selectedDateText,
            errorMessage = errorMessage,
            selectableDates = selectableDates,
            trailingIconTint = trailingIconTint,
            dialogContainerColor = dialogContainerColor,
            dialogContentColor = dialogContentColor,
            selectedDayContainerColor = selectedDayContainerColor,
            selectedDayContentColor = selectedDayContentColor,
            selectedYearContainerColor = selectedYearContainerColor,
            selectedYearContentColor = selectedYearContentColor,
            todayContentColor = todayContentColor,
            todayDateBorderColor = todayDateBorderColor,
            confirmButtonTextColor = confirmButtonTextColor,
            dismissButtonTextColor = dismissButtonTextColor,
            leadingSlot = leadingSlot,
            trailingSlot = trailingSlot
        )
    }
}
