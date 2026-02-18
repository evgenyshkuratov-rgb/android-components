package com.example.gallery.preview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import com.example.components.attachedmedia.AttachedMediaView
import com.example.components.designsystem.DSBrand
import com.example.components.designsystem.DSCornerRadius
import com.example.components.designsystem.DSIcon
import com.example.components.designsystem.DSTypography
import com.example.gallery.theme.toComposeTextStyle

@Composable
fun AttachedMediaViewPreviewScreen(
    componentId: String,
    isDarkTheme: Boolean,
    onThemeChanged: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedBrand by remember { mutableIntStateOf(0) }
    var selectedType by remember { mutableIntStateOf(0) }
    var selectedError by remember { mutableIntStateOf(0) }
    var selectedFileType by remember { mutableIntStateOf(0) }

    val brand = DSBrand.entries[selectedBrand]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
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
                text = "Attached Media",
                style = DSTypography.title5B.toComposeTextStyle(),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            AttachedMediaCompactThemeToggle(isDarkTheme = isDarkTheme, onThemeChanged = onThemeChanged)
        }

        // Brand selector
        AttachedMediaSegmentedControl(
            options = DSBrand.entries.map { it.displayName },
            selectedIndex = selectedBrand,
            onSelect = { selectedBrand = it }
        )

        // Preview container
        val brandCount = DSBrand.entries.size
        val bgColor = Color(brand.backgroundBase(isDarkTheme))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
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
            val previewKey = AttachedMediaPreviewKey(
                brand = selectedBrand,
                dark = isDarkTheme,
                type = selectedType,
                fileType = selectedFileType,
                error = selectedError
            )
            key(previewKey) {
                val tBrand = DSBrand.entries[previewKey.brand]
                val tType = AttachedMediaView.MediaType.entries[previewKey.type]
                val tFileType = AttachedMediaView.FileType.entries[previewKey.fileType]
                val tIsError = previewKey.error == 1
                val needsThumbnail = tType == AttachedMediaView.MediaType.MEDIA ||
                        (tType == AttachedMediaView.MediaType.FILE && (tFileType == AttachedMediaView.FileType.IMAGE || tFileType == AttachedMediaView.FileType.VIDEO))
                val sampleBitmap = if (needsThumbnail) {
                    remember { loadSampleImage(context) }
                } else null

                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    AndroidView(
                        factory = { ctx ->
                            val view = AttachedMediaView(ctx)
                            val colorScheme = tBrand.attachedMediaColorScheme(previewKey.dark)
                            view.configure(
                                type = tType,
                                fileType = tFileType,
                                fileName = when (tFileType) {
                                    AttachedMediaView.FileType.FILE -> "document.pdf"
                                    AttachedMediaView.FileType.AUDIO -> "recording.mp3"
                                    AttachedMediaView.FileType.IMAGE -> "photo.jpg"
                                    AttachedMediaView.FileType.VIDEO -> "video.mp4"
                                },
                                fileSize = "2.4 MB",
                                errorText = "Upload error",
                                isError = tIsError,
                                thumbnailImage = sampleBitmap,
                                mediaDuration = "1:23",
                                showBadge = tType == AttachedMediaView.MediaType.MEDIA && !tIsError,
                                colorScheme = colorScheme
                            )
                            view
                        },
                        modifier = Modifier.wrapContentSize()
                    )
                }
            }
        }

        // Controls
        AttachedMediaControlRow(label = "Type") {
            AttachedMediaSegmentedControl(
                options = listOf("File", "Media"),
                selectedIndex = selectedType,
                onSelect = { selectedType = it }
            )
        }

        AttachedMediaControlRow(label = "Error") {
            AttachedMediaSegmentedControl(
                options = listOf("No", "Yes"),
                selectedIndex = selectedError,
                onSelect = { selectedError = it }
            )
        }

        if (selectedType == 0) {
            AttachedMediaControlRow(label = "File Type") {
                AttachedMediaSegmentedControl(
                    options = listOf("File", "Audio", "Image", "Video"),
                    selectedIndex = selectedFileType,
                    onSelect = { selectedFileType = it }
                )
            }
        }
    }
}

private data class AttachedMediaPreviewKey(
    val brand: Int,
    val dark: Boolean,
    val type: Int,
    val fileType: Int,
    val error: Int
)

private fun loadSampleImage(context: Context): Bitmap? {
    return try {
        context.assets.open("images/sample.jpg").use { BitmapFactory.decodeStream(it) }
    } catch (_: Exception) {
        null
    }
}

@Composable
private fun AttachedMediaCompactThemeToggle(isDarkTheme: Boolean, onThemeChanged: (Boolean) -> Unit) {
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
private fun AttachedMediaControlRow(label: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = label, style = DSTypography.subhead4M.toComposeTextStyle(), color = MaterialTheme.colorScheme.onSurface)
        content()
    }
}

@Composable
private fun AttachedMediaSegmentedControl(options: List<String>, selectedIndex: Int, onSelect: (Int) -> Unit) {
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
