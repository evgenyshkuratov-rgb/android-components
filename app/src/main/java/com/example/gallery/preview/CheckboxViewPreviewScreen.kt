package com.example.gallery.preview

import android.graphics.drawable.BitmapDrawable
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.components.checkbox.CheckboxView
import com.example.components.designsystem.DSBrand
import com.example.components.designsystem.DSCornerRadius
import com.example.components.designsystem.DSIcon
import com.example.components.designsystem.DSTypography
import com.example.gallery.theme.toComposeTextStyle

@Composable
fun CheckboxViewPreviewScreen(componentId: String, isDarkTheme: Boolean, onThemeChanged: (Boolean) -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current
    var selectedShape by remember { mutableIntStateOf(0) }
    var selectedBrand by remember { mutableIntStateOf(0) }
    var isChecked by remember { mutableStateOf(false) }
    var showText by remember { mutableIntStateOf(0) }
    var isEnabled by remember { mutableIntStateOf(0) }

    val brand = DSBrand.entries[selectedBrand]
    val isDark = isDarkTheme

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).statusBarsPadding().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val backIcon = remember { DSIcon.named(context, "back", 24f) as? BitmapDrawable }
            backIcon?.bitmap?.let { bmp ->
                Image(
                    bitmap = bmp.asImageBitmap(),
                    contentDescription = "Back",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                    modifier = Modifier.size(24.dp).clickable { onBack() }
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = componentId.removeSuffix("View"), style = DSTypography.title5B.toComposeTextStyle(), color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
            CheckboxCompactThemeToggle(isDarkTheme = isDarkTheme, onThemeChanged = onThemeChanged)
        }

        CheckboxSegmentedControl(options = DSBrand.entries.map { it.displayName }, selectedIndex = selectedBrand, onSelect = { selectedBrand = it })

        val brandCount = DSBrand.entries.size
        val bgColor = Color(brand.backgroundSecond(isDark))
        val animatedBgColor by animateColorAsState(targetValue = bgColor, animationSpec = tween(300))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(animatedBgColor)
                .pointerInput(Unit) {
                    var totalDrag = 0f
                    val threshold = 50.dp.toPx()
                    detectHorizontalDragGestures(
                        onDragEnd = { totalDrag = 0f },
                        onDragCancel = { totalDrag = 0f },
                        onHorizontalDrag = { _, dragAmount ->
                            totalDrag += dragAmount
                            if (totalDrag > threshold) {
                                totalDrag = 0f
                                selectedBrand = (selectedBrand - 1 + brandCount) % brandCount
                            } else if (totalDrag < -threshold) {
                                totalDrag = 0f
                                selectedBrand = (selectedBrand + 1) % brandCount
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Crossfade(
                targetState = CheckboxPreviewKey(selectedShape, selectedBrand, isDarkTheme, isChecked, showText, isEnabled),
                animationSpec = tween(200)
            ) { target ->
                val tBrand = DSBrand.entries[target.brand]
                val tShape = CheckboxView.Shape.entries[target.shape]
                val tTextVisible = target.showText == 0
                val tEnabled = target.enabled == 0
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    AndroidView(
                        factory = { ctx ->
                            val checkbox = CheckboxView(ctx)
                            val colorScheme = tBrand.checkboxColorScheme(target.dark)
                            checkbox.configure(
                                text = "Label",
                                shape = tShape,
                                isChecked = target.checked,
                                isEnabled = tEnabled,
                                showText = tTextVisible,
                                colorScheme = colorScheme
                            )
                            checkbox.onCheckedChange = { checked ->
                                isChecked = checked
                            }
                            checkbox
                        },
                        modifier = Modifier.wrapContentSize()
                    )
                }
            }
        }

        CheckboxControlRow(label = "Shape") {
            CheckboxSegmentedControl(options = listOf("Square", "Circle"), selectedIndex = selectedShape, onSelect = { selectedShape = it })
        }
        CheckboxControlRow(label = "Show Text") {
            CheckboxSegmentedControl(options = listOf("On", "Off"), selectedIndex = showText, onSelect = { showText = it })
        }
        CheckboxControlRow(label = "Enabled") {
            CheckboxSegmentedControl(options = listOf("Yes", "No"), selectedIndex = isEnabled, onSelect = { isEnabled = it })
        }
    }
}

private data class CheckboxPreviewKey(val shape: Int, val brand: Int, val dark: Boolean, val checked: Boolean, val showText: Int, val enabled: Int)

@Composable
private fun CheckboxCompactThemeToggle(isDarkTheme: Boolean, onThemeChanged: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(DSCornerRadius.inputField.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        listOf(false to "Light", true to "Dark").forEach { (dark, label) ->
            val isSelected = isDarkTheme == dark
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent)
                    .clickable { onThemeChanged(dark) }
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = if (isSelected) DSTypography.subhead4M.toComposeTextStyle() else DSTypography.subhead2R.toComposeTextStyle(),
                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
private fun CheckboxControlRow(label: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = label, style = DSTypography.subhead4M.toComposeTextStyle(), color = MaterialTheme.colorScheme.onSurface)
        content()
    }
}

@Composable
private fun CheckboxSegmentedControl(options: List<String>, selectedIndex: Int, onSelect: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(DSCornerRadius.inputField.dp)).background(MaterialTheme.colorScheme.surfaceVariant).padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        options.forEachIndexed { index, option ->
            val isSelected = index == selectedIndex
            Box(
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent).clickable { onSelect(index) }.padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option,
                    style = if (isSelected) DSTypography.subhead4M.toComposeTextStyle() else DSTypography.subhead2R.toComposeTextStyle(),
                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}
