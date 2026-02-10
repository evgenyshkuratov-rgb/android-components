package com.example.gallery.preview

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.components.chips.ChipsView
import com.example.components.designsystem.DSBrand
import com.example.components.designsystem.DSCornerRadius
import com.example.components.designsystem.DSIcon
import com.example.components.designsystem.DSTypography
import com.example.gallery.theme.toComposeTextStyle

@Composable
fun ChipsViewPreviewScreen(componentId: String, onBack: () -> Unit) {
    val context = LocalContext.current
    var selectedState by remember { mutableIntStateOf(0) }
    var selectedSize by remember { mutableIntStateOf(0) }
    var selectedBrand by remember { mutableIntStateOf(0) }
    var selectedTheme by remember { mutableIntStateOf(0) }

    val chipState = ChipsView.ChipState.entries[selectedState]
    val chipSize = ChipsView.ChipSize.entries[selectedSize]
    val brand = DSBrand.entries[selectedBrand]
    val isDark = selectedTheme == 1

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
            Text(text = componentId, style = DSTypography.title5B.toComposeTextStyle(), color = MaterialTheme.colorScheme.onSurface)
        }
        SegmentedControl(options = DSBrand.entries.map { it.displayName }, selectedIndex = selectedBrand, onSelect = { selectedBrand = it })
        val bgColor = Color(brand.backgroundSecond(isDark))
        Box(
            modifier = Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(16.dp)).background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            key(selectedState, selectedSize, selectedBrand, selectedTheme) {
                AndroidView(
                    factory = { ctx ->
                        val chip = ChipsView(ctx)
                        val colorScheme = brand.chipsColorScheme(isDark)
                        when (chipState) {
                            ChipsView.ChipState.DEFAULT, ChipsView.ChipState.ACTIVE -> {
                                val icon = DSIcon.named(ctx, "user-2", 20f)
                                chip.configure(text = "Filter option", icon = icon, state = chipState, size = chipSize, colorScheme = colorScheme)
                            }
                            ChipsView.ChipState.AVATAR -> {
                                val closeIcon = DSIcon.named(ctx, "close-s", 24f)
                                val avatar = createPlaceholderAvatar(ctx, chipSize.avatarDp, brand, isDark)
                                chip.configureAvatar(name = "\u0418\u043C\u044F", avatarImage = avatar, closeIcon = closeIcon, size = chipSize, colorScheme = colorScheme)
                            }
                        }
                        chip
                    },
                    modifier = Modifier.wrapContentSize()
                )
            }
        }
        DropdownSelector(label = "State", options = listOf("Default", "Active", "Avatar"), selectedIndex = selectedState, onSelect = { selectedState = it })
        DropdownSelector(label = "Size", options = listOf("32dp", "40dp"), selectedIndex = selectedSize, onSelect = { selectedSize = it })
        ControlRow(label = "Theme") { SegmentedControl(options = listOf("Light", "Dark"), selectedIndex = selectedTheme, onSelect = { selectedTheme = it }) }
    }
}

@Composable
private fun DropdownSelector(label: String, options: List<String>, selectedIndex: Int, onSelect: (Int) -> Unit) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val chevronIcon = remember { DSIcon.named(context, "select-down", 20f) as? BitmapDrawable }
    var expanded by remember { mutableStateOf(false) }
    var triggerWidth by remember { mutableStateOf(0) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = label, style = DSTypography.subhead4M.toComposeTextStyle(), color = MaterialTheme.colorScheme.onSurface)
        Box {
            Row(
                modifier = Modifier.fillMaxWidth().onSizeChanged { triggerWidth = it.width }.clip(RoundedCornerShape(DSCornerRadius.inputField.dp)).background(MaterialTheme.colorScheme.surfaceVariant).clickable { expanded = true }.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = options[selectedIndex],
                    style = DSTypography.body1R.toComposeTextStyle(),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                chevronIcon?.bitmap?.let { bmp ->
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.width(with(density) { triggerWidth.toDp() })
            ) {
                options.forEachIndexed { index, option ->
                    DropdownMenuItem(
                        text = { Text(text = option, style = DSTypography.body1R.toComposeTextStyle()) },
                        onClick = { onSelect(index); expanded = false }
                    )
                }
            }
        }
    }
}

@Composable
private fun ControlRow(label: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = label, style = DSTypography.subhead4M.toComposeTextStyle(), color = MaterialTheme.colorScheme.onSurface)
        content()
    }
}

@Composable
private fun SegmentedControl(options: List<String>, selectedIndex: Int, onSelect: (Int) -> Unit) {
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

private fun createPlaceholderAvatar(context: android.content.Context, sizeDp: Int, brand: DSBrand, isDark: Boolean): Bitmap {
    val density = context.resources.displayMetrics.density
    val sizePx = (sizeDp * density).toInt()
    val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    bgPaint.color = brand.backgroundSecond(isDark)
    canvas.drawCircle(sizePx / 2f, sizePx / 2f, sizePx / 2f, bgPaint)
    val iconPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    iconPaint.color = brand.basicColor50(isDark)
    iconPaint.textSize = sizePx * 0.4f
    iconPaint.textAlign = Paint.Align.CENTER
    canvas.drawText("\u263A", sizePx / 2f, sizePx / 2f + sizePx * 0.15f, iconPaint)
    return bitmap
}
