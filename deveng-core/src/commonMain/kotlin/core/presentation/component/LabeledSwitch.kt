package core.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import core.presentation.theme.LocalComponentTheme

@Composable
fun LabeledSwitch(
    label: String,
    isChecked: Boolean,
    modifier: Modifier = Modifier,
    isLabelAtStart: Boolean = true,
    labelTextStyle: TextStyle? = null,
    checkedThumbColor: Color? = null,
    checkedTrackColor: Color? = null,
    checkedBorderColor: Color? = null,
    uncheckedThumbColor: Color? = null,
    uncheckedTrackColor: Color? = null,
    uncheckedBorderColor: Color? = null,
    switchScale: Float? = null,
    onSwitchClick: (Boolean) -> Unit
) {
    val componentTheme = LocalComponentTheme.current
    val labeledSwitchTheme = componentTheme.labeledSwitch

    val finalLabelTextStyle = labelTextStyle ?: labeledSwitchTheme.labelTextStyle
    val finalCheckedThumbColor = checkedThumbColor ?: labeledSwitchTheme.checkedThumbColor
    val finalCheckedTrackColor = checkedTrackColor ?: labeledSwitchTheme.checkedTrackColor
    val finalCheckedBorderColor = checkedBorderColor ?: labeledSwitchTheme.checkedBorderColor
    val finalUncheckedThumbColor = uncheckedThumbColor ?: labeledSwitchTheme.uncheckedThumbColor
    val finalUncheckedTrackColor = uncheckedTrackColor ?: labeledSwitchTheme.uncheckedTrackColor
    val finalUncheckedBorderColor = uncheckedBorderColor ?: labeledSwitchTheme.uncheckedBorderColor
    val finalScale = switchScale ?: labeledSwitchTheme.switchScale

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isLabelAtStart) {
            Arrangement.SpaceBetween
        } else {
            Arrangement.End
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = finalLabelTextStyle
        )

        Switch(
            modifier = Modifier.scale(finalScale),
            checked = isChecked,
            onCheckedChange = onSwitchClick,
            colors = SwitchDefaults.colors(
                checkedThumbColor = finalCheckedThumbColor,
                checkedTrackColor = finalCheckedTrackColor,
                checkedBorderColor = finalCheckedBorderColor,
                uncheckedThumbColor = finalUncheckedThumbColor,
                uncheckedTrackColor = finalUncheckedTrackColor,
                uncheckedBorderColor = finalUncheckedBorderColor
            )
        )
    }
}