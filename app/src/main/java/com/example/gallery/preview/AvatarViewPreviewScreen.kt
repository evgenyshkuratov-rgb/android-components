package com.example.gallery.preview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import androidx.compose.ui.viewinterop.AndroidView
import com.example.components.avatar.AvatarView
import com.example.components.designsystem.DSBrand
import com.example.components.designsystem.DSCornerRadius
import com.example.components.designsystem.DSIcon
import com.example.components.designsystem.DSTypography
import com.example.gallery.theme.toComposeTextStyle

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AvatarViewPreviewScreen(
    componentId: String,
    isDarkTheme: Boolean,
    onThemeChanged: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var selectedBrand by remember { mutableIntStateOf(0) }
    var selectedView by remember { mutableIntStateOf(0) }
    var selectedSizeIndex by remember { mutableIntStateOf(4) } // default SIZE_48
    var initialsText by remember { mutableStateOf("") }

    val brand = DSBrand.entries[selectedBrand]
    val scrollState = rememberScrollState()
    val imeVisible = WindowInsets.isImeVisible

    LaunchedEffect(imeVisible) {
        if (imeVisible) {
            snapshotFlow { scrollState.maxValue }
                .collect { scrollState.scrollTo(it) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            }
            .statusBarsPadding()
            .imePadding()
            .verticalScroll(scrollState)
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            val backIcon = remember { DSIcon.named(context, "back", 24f) as? BitmapDrawable }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Transparent)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                backIcon?.bitmap?.let { bmp ->
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = "Back",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Avatar",
                style = DSTypography.title5B.toComposeTextStyle(),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            AvatarCompactThemeToggle(isDarkTheme = isDarkTheme, onThemeChanged = onThemeChanged)
        }

        // Brand selector
        AvatarSegmentedControl(
            options = DSBrand.entries.map { it.displayName },
            selectedIndex = selectedBrand,
            onSelect = { selectedBrand = it }
        )

        // Preview container
        val brandCount = DSBrand.entries.size
        val bgColor = Color(brand.backgroundSecond(isDarkTheme))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(192.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(bgColor)
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
            val previewKey = AvatarPreviewKey(
                brand = selectedBrand,
                dark = isDarkTheme,
                view = selectedView,
                size = selectedSizeIndex,
                initials = initialsText
            )
            key(previewKey) {
                val tBrand = DSBrand.entries[previewKey.brand]
                val tViewType = AvatarView.AvatarViewType.entries[previewKey.view]
                val tSize = AvatarView.AvatarSize.entries[previewKey.size]
                val needsImage = tViewType == AvatarView.AvatarViewType.IMAGE
                val sampleBitmap = if (needsImage) {
                    remember { loadAvatarImage(context) }
                } else null

                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    AndroidView(
                        factory = { ctx ->
                            val avatar = AvatarView(ctx)
                            val colorScheme = tBrand.avatarColorScheme(previewKey.dark)
                            avatar.configure(
                                type = tViewType,
                                size = tSize,
                                text = previewKey.initials.ifEmpty { "AB" },
                                image = sampleBitmap,
                                colorScheme = colorScheme
                            )
                            avatar
                        },
                        modifier = Modifier.wrapContentSize()
                    )
                }
            }
        }

        // View control
        AvatarControlRow(label = "View") {
            AvatarSegmentedControl(
                options = listOf("Image", "Initials", "Bot", "Saved"),
                selectedIndex = selectedView,
                onSelect = { selectedView = it }
            )
        }

        // Size control
        AvatarControlRow(label = "Size") {
            AvatarSizeSlider(
                sizes = AvatarView.AvatarSize.entries,
                selectedIndex = selectedSizeIndex,
                onSelect = { selectedSizeIndex = it }
            )
        }

        // Initials text input (visible only for Initials view)
        if (selectedView == 1) {
            AvatarControlRow(label = "Initials") {
                AvatarTextInput(
                    value = initialsText,
                    onValueChange = { if (it.length <= 2) initialsText = it },
                    placeholder = "AB"
                )
            }
        }
    }
}

private data class AvatarPreviewKey(
    val brand: Int,
    val dark: Boolean,
    val view: Int,
    val size: Int,
    val initials: String
)

private fun loadAvatarImage(context: Context): Bitmap? {
    return try {
        context.assets.open("images/avatar.jpg").use { BitmapFactory.decodeStream(it) }
    } catch (_: Exception) {
        null
    }
}

@Composable
private fun AvatarCompactThemeToggle(isDarkTheme: Boolean, onThemeChanged: (Boolean) -> Unit) {
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
private fun AvatarControlRow(label: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = label, style = DSTypography.subhead4M.toComposeTextStyle(), color = MaterialTheme.colorScheme.onSurface)
        content()
    }
}

@Composable
private fun AvatarSegmentedControl(options: List<String>, selectedIndex: Int, onSelect: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(DSCornerRadius.inputField.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        options.forEachIndexed { index, option ->
            val isSelected = index == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent)
                    .clickable { onSelect(index) }
                    .padding(vertical = 8.dp),
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

@Composable
private fun AvatarSizeSlider(
    sizes: List<AvatarView.AvatarSize>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    val lastIndex = sizes.lastIndex
    val invertedValue = (lastIndex - selectedIndex).toFloat()

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${sizes[selectedIndex].dp}dp",
                style = DSTypography.subhead4M.toComposeTextStyle(),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${sizes.last().dp} – ${sizes.first().dp}",
                style = DSTypography.caption2R.toComposeTextStyle(),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Slider(
            value = invertedValue,
            onValueChange = { onSelect(lastIndex - it.roundToInt()) },
            valueRange = 0f..lastIndex.toFloat(),
            steps = lastIndex - 1,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}

@Composable
private fun AvatarTextInput(value: String, onValueChange: (String) -> Unit, placeholder: String) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val clearIcon = remember { DSIcon.named(context, "clear-field", 24f) as? BitmapDrawable }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        textStyle = DSTypography.subhead2R.toComposeTextStyle().copy(
            color = MaterialTheme.colorScheme.onSurface
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(DSCornerRadius.inputField.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(start = 14.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = DSTypography.subhead2R.toComposeTextStyle(),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    }
                    innerTextField()
                }
                if (value.isNotEmpty()) {
                    clearIcon?.bitmap?.let { bmp ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .clickable { onValueChange("") },
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                bitmap = bmp.asImageBitmap(),
                                contentDescription = "Clear",
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}
