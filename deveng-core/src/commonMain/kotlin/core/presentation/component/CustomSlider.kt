package core.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import core.presentation.theme.AppTheme
import core.presentation.theme.LocalComponentTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * A highly customizable slider component designed for consistent value selection across the application.
 * It supports titles, dynamic value formatting, optional range labels (min/max), error messages,
 * and custom slots for leading and trailing elements (e.g., icons).
 *
 * @param sliderModifier Modifier to be applied directly to the internal [Slider] component.
 * @param containerModifier Modifier to be applied to the outermost container of the component.
 * @param title Optional title text displayed above the slider.
 * @param titleTextStyle Text style for the title. If null, uses the component theme default.
 * @param currentValueTextStyle Text style for the currently selected value. If null, uses the component theme default.
 * @param errorMessageTextStyle Text style for the error message. If null, uses the component theme default.
 * @param rangeLabelTextStyle Text style for the min and max range labels. If null, uses the component theme default.
 * @param minValue The minimum value of the slider range. Default is 0f.
 * @param maxValue The maximum value of the slider range. Default is 100f.
 * @param value The current value of the slider.
 * @param valueFormatter A lambda to format the displayed value (e.g., appending units like "%" or "kg"). Defaults to integer string conversion.
 * @param steps If greater than 0, specifies the amount of discrete values, evenly distributed between min and max. Default is 0 (smooth continuous sliding).
 * @param errorMessage Optional error message displayed below the slider.
 * @param sliderColors The colors used to resolve the track, thumb, and tick marks. Default is [SliderDefaults.colors].
 * @param leadingSlot Optional composable slot displayed to the left of the slider (e.g., a volume down icon).
 * @param trailingSlot Optional composable slot displayed to the right of the slider (e.g., a volume up icon).
 * @param isEnabled Whether the slider is enabled for user interaction. Default is true.
 * @param isRangeLabelsVisible Whether to display the minimum and maximum values at the edges of the slider. Default is false.
 * @param sliderRowSpacing Horizontal spacing between the slider and its leading/trailing slots. If null, uses the component theme default.
 * @param errorMessageTopSpacing Spacing between the slider row and the error message. If null, uses the component theme default.
 * @param onValueChange Callback invoked immediately and continuously as the slider is dragged.
 * @param onValueChangeFinished Optional callback invoked when the user stops interacting with the slider (e.g., releases drag). Useful for triggering API calls.
 */

@Composable
fun CustomSlider(
    sliderModifier: Modifier = Modifier,
    containerModifier: Modifier = Modifier,
    title: String? = null,
    titleTextStyle: TextStyle? = null,
    currentValueTextStyle: TextStyle? = null,
    errorMessageTextStyle: TextStyle? = null,
    rangeLabelTextStyle: TextStyle? = null,
    minValue: Float = 0f,
    maxValue: Float = 100f,
    value: Float,
    valueFormatter: (Float) -> String = { it.toInt().toString() },
    steps: Int = 0,
    errorMessage: String? = null,
    sliderColors: SliderColors = SliderDefaults.colors(),
    leadingSlot: Slot? = null,
    trailingSlot: Slot? = null,
    isEnabled: Boolean = true,
    isRangeLabelsVisible: Boolean = false,
    sliderRowSpacing: Dp? = null,
    errorMessageTopSpacing: Dp? = null,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: ((Float) -> Unit)? = null
) {
    val componentTheme = LocalComponentTheme.current
    val sliderTheme = componentTheme.customSlider

    val finalTitleTextStyle = titleTextStyle ?: sliderTheme.titleTextStyle
    val finalCurrentValueTextStyle = currentValueTextStyle ?: sliderTheme.currentValueTextStyle
    val finalErrorMessageTextStyle = errorMessageTextStyle ?: sliderTheme.errorMessageTextStyle
    val finalRangeLabelTextStyle = rangeLabelTextStyle ?: sliderTheme.rangeLabelTextStyle
    val finalSliderRowSpacing = sliderRowSpacing ?: sliderTheme.sliderRowSpacing
    val finalErrorMessageTopSpacing = errorMessageTopSpacing ?: sliderTheme.errorMessageTopSpacing

    var initialValue by remember(value) { mutableStateOf(value) }

    Column(
        modifier = containerModifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            title?.let {
                Text(
                    text = it,
                    style = finalTitleTextStyle
                )
            }

            Text(
                text = valueFormatter(initialValue),
                style = finalCurrentValueTextStyle
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(finalSliderRowSpacing)
        ) {
            if (leadingSlot != null) {
                leadingSlot()
            }

            Slider(
                modifier = sliderModifier.weight(1f),
                value = initialValue,
                valueRange = minValue..maxValue,
                steps = steps,
                enabled = isEnabled,
                colors = sliderColors,
                onValueChange = {
                    initialValue = it
                    onValueChange(it)
                },
                onValueChangeFinished = {
                    onValueChangeFinished?.invoke(initialValue)
                }
            )

            if (trailingSlot != null) {
                trailingSlot()
            }
        }

        if (isRangeLabelsVisible) {
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = valueFormatter(minValue),
                    style = finalRangeLabelTextStyle
                )

                Text(
                    text = valueFormatter(maxValue),
                    style = finalRangeLabelTextStyle
                )
            }
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(finalErrorMessageTopSpacing))

            Text(
                text = it,
                style = finalErrorMessageTextStyle
            )
        }
    }
}

@Preview
@Composable
fun CustomSliderPreview() {
    AppTheme {
        CustomSlider(
            title = "Volume",
            value = 50f,
            onValueChange = {}
        )
    }
}