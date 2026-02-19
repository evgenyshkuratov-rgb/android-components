package com.example.gallery.preview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
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
    var selectedBrand by rememberSaveable { mutableIntStateOf(0) }
    var selectedView by rememberSaveable { mutableIntStateOf(0) }
    var selectedSizeIndex by rememberSaveable { mutableIntStateOf(3) } // default SIZE_56
    var initialsText by rememberSaveable { mutableStateOf("") }
    var selectedImageIndex by rememberSaveable { mutableIntStateOf(0) }
    var selectedGradientIndex by rememberSaveable { mutableIntStateOf(0) }
    var selectedIconVariant by rememberSaveable { mutableIntStateOf(0) } // 0=bookmark, 1=crown

    val avatarImages = remember { loadAllAvatarImages(context) }
    val avatarCount = avatarImages.size
    val gradientCount = remember(selectedBrand, isDarkTheme) {
        DSBrand.entries[selectedBrand].avatarGradientPairs(isDarkTheme).size
    }

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
        var isPreviewPressed by remember { mutableStateOf(false) }
        val previewScale by animateFloatAsState(
            targetValue = if (isPreviewPressed) 0.9f else 1f,
            animationSpec = spring(dampingRatio = 0.5f, stiffness = 800f)
        )
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
                }
                .pointerInput(selectedView, avatarCount, gradientCount) {
                    detectTapGestures(
                        onPress = {
                            isPreviewPressed = true
                            tryAwaitRelease()
                            isPreviewPressed = false
                        },
                        onTap = {
                            when (selectedView) {
                                0 -> if (avatarCount > 0) {
                                    selectedImageIndex = (selectedImageIndex + 1) % avatarCount
                                }
                                1 -> if (gradientCount > 0) {
                                    selectedGradientIndex = (selectedGradientIndex + 1) % gradientCount
                                }
                                3 -> selectedIconVariant = (selectedIconVariant + 1) % 2
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
                initials = initialsText,
                imageIndex = selectedImageIndex,
                gradientIndex = selectedGradientIndex,
                iconVariant = selectedIconVariant
            )
            key(previewKey) {
                val tBrand = DSBrand.entries[previewKey.brand]
                val tViewType = AvatarView.AvatarViewType.entries[previewKey.view]
                val tSize = AvatarView.AvatarSize.entries[previewKey.size]
                val needsImage = tViewType == AvatarView.AvatarViewType.IMAGE
                val sampleBitmap = if (needsImage && avatarImages.isNotEmpty()) {
                    avatarImages[previewKey.imageIndex % avatarImages.size]
                } else null

                // Determine icon name and gradient for SAVED/Icon mode
                val isCrownVariant = tViewType == AvatarView.AvatarViewType.SAVED && previewKey.iconVariant == 1
                val iconName = if (isCrownVariant) "crown" else "bookmark"

                Box(
                    modifier = Modifier.fillMaxSize().scale(previewScale),
                    contentAlignment = Alignment.Center
                ) {
                    AndroidView(
                        factory = { ctx ->
                            val avatar = AvatarView(ctx)
                            val baseScheme = tBrand.avatarColorScheme(previewKey.dark)
                            val gradientPairs = tBrand.avatarGradientPairs(previewKey.dark)
                            val selectedPair = gradientPairs[previewKey.gradientIndex % gradientPairs.size]
                            val colorScheme = if (isCrownVariant) {
                                val badgeColor = tBrand.accentColor(previewKey.dark)
                                baseScheme.copy(
                                    savedGradientTop = badgeColor,
                                    savedGradientBottom = badgeColor
                                )
                            } else {
                                baseScheme.copy(
                                    initialsGradientTop = selectedPair.top,
                                    initialsGradientBottom = selectedPair.bottom
                                )
                            }
                            avatar.configure(
                                type = tViewType,
                                size = tSize,
                                text = previewKey.initials.ifEmpty { "AB" },
                                image = sampleBitmap,
                                iconName = iconName,
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
                options = listOf("Image", "Initials", "Bot", "Icon"),
                selectedIndex = selectedView,
                onSelect = { selectedView = it }
            )
        }

        // Size control
        val sizes = AvatarView.AvatarSize.entries
        AvatarControlRow(label = "Size \u00B7 ${sizes[selectedSizeIndex].dp}dp") {
            PreviewSlider(
                values = sizes.map { "${it.dp}" },
                selectedIndex = selectedSizeIndex,
                onSelect = { selectedSizeIndex = it },
                accentColor = Color(brand.accentColor(isDarkTheme)),
                startLabel = "${sizes.last().dp}",
                endLabel = "${sizes.first().dp}"
            )
        }

        // Initials text input (visible only for Initials view)
        if (selectedView == 1) {
            AvatarControlRow(label = "Initials") {
                AvatarTextInput(
                    value = initialsText,
                    onValueChange = { if (it.length <= 2) initialsText = it },
                    placeholder = "AB",
                    accentColor = Color(brand.accentColor(isDarkTheme))
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
    val initials: String,
    val imageIndex: Int,
    val gradientIndex: Int,
    val iconVariant: Int
)

private val avatarFiles = listOf(
    "images/avatar_01.png",
    "images/avatar_02.png",
    "images/avatar_03.png",
    "images/avatar_04.png",
    "images/avatar_05.jpg",
    "images/avatar_06.png",
    "images/avatar_07.png",
    "images/avatar_08.png",
    "images/avatar_09.png"
)

private fun loadAllAvatarImages(context: Context): List<Bitmap> {
    return avatarFiles.mapNotNull { path ->
        try {
            context.assets.open(path).use { BitmapFactory.decodeStream(it) }
        } catch (_: Exception) {
            null
        }
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
private fun AvatarTextInput(value: String, onValueChange: (String) -> Unit, placeholder: String, accentColor: Color) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val clearIcon = remember { DSIcon.named(context, "clear-field", 24f) as? BitmapDrawable }
    val selectionColors = TextSelectionColors(
        handleColor = accentColor,
        backgroundColor = accentColor.copy(alpha = 0.4f)
    )

    CompositionLocalProvider(LocalTextSelectionColors provides selectionColors) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        textStyle = DSTypography.subhead2R.toComposeTextStyle().copy(
            color = MaterialTheme.colorScheme.onSurface
        ),
        cursorBrush = SolidColor(accentColor),
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
}
