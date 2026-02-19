package com.example.gallery.preview

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.components.designsystem.DSTypography
import com.example.gallery.theme.toComposeTextStyle
import kotlin.math.roundToInt

/**
 * Reusable slider for selecting from an ordered list of values.
 * Shows start/end labels above the slider, selected value is shown externally via label param.
 *
 * @param values list of display strings (ordered from largest to smallest, matching enum order)
 * @param selectedIndex currently selected index in [values]
 * @param onSelect called with new index when user drags the slider
 * @param accentColor brand accent color for thumb and active track
 * @param startLabel label shown above slider on the left (smallest value)
 * @param endLabel label shown above slider on the right (largest value)
 */
@Composable
fun PreviewSlider(
    values: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    accentColor: Color,
    startLabel: String,
    endLabel: String
) {
    val lastIndex = values.lastIndex
    val invertedValue = (lastIndex - selectedIndex).toFloat()

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = startLabel,
                style = DSTypography.caption2R.toComposeTextStyle(),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
            Text(
                text = endLabel,
                style = DSTypography.caption2R.toComposeTextStyle(),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        }
        Slider(
            value = invertedValue,
            onValueChange = { onSelect(lastIndex - it.roundToInt()) },
            valueRange = 0f..lastIndex.toFloat(),
            steps = lastIndex - 1,
            colors = SliderDefaults.colors(
                thumbColor = accentColor,
                activeTrackColor = accentColor,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}
