package com.example.gallery.catalog

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.example.components.designsystem.DSCornerRadius
import com.example.components.designsystem.DSIcon
import com.example.components.designsystem.DSSpacing
import com.example.components.designsystem.DSTypography
import com.example.gallery.theme.toComposeTextStyle
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private data class ComponentEntry(val id: String, val name: String, val description: String)

private val components = listOf(
    ComponentEntry("AttachedMediaView", "Attached Media", "File and media attachments with error states and file type icons"),
    ComponentEntry("CheckboxView", "Checkbox", "Checkbox with square and circle shapes, label text, and enabled/disabled states"),
    ComponentEntry("ChipsView", "Chips", "Filter chips with Default, Active, and Avatar states")
)

@Composable
fun CatalogScreen(
    isDarkTheme: Boolean,
    onThemeChanged: (Boolean) -> Unit,
    onComponentClick: (String) -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var searchQuery by remember { mutableStateOf("") }
    val filteredComponents = remember(searchQuery) {
        if (searchQuery.isBlank()) components
        else components.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }
    val statusText = remember { buildStatusText(context) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .pointerInput(filteredComponents.isEmpty()) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                    if (filteredComponents.isEmpty()) {
                        searchQuery = ""
                    }
                })
            },
        contentPadding = PaddingValues(bottom = 48.dp)
    ) {
        item {
            Spacer(modifier = Modifier.statusBarsPadding().height(28.dp))
        }
        item {
            ThemeToggleRow(
                isDarkTheme = isDarkTheme,
                onThemeChanged = onThemeChanged,
                modifier = Modifier.padding(horizontal = DSSpacing.horizontalPadding.dp)
            )
        }
        item { Spacer(modifier = Modifier.height(20.dp)) }
        item {
            Text(
                text = "Components Library",
                style = DSTypography.title1B.toComposeTextStyle(),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = DSSpacing.horizontalPadding.dp)
            )
        }
        item { Spacer(modifier = Modifier.height(6.dp)) }
        item {
            Text(
                text = statusText,
                style = DSTypography.subhead3R.toComposeTextStyle(),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                modifier = Modifier.padding(horizontal = DSSpacing.horizontalPadding.dp)
            )
        }
        item { Spacer(modifier = Modifier.height(DSSpacing.verticalSection.dp)) }
        item {
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                modifier = Modifier.padding(horizontal = DSSpacing.horizontalPadding.dp)
            )
        }
        item { Spacer(modifier = Modifier.height(28.dp)) }
        items(filteredComponents) { component ->
            ComponentCard(
                name = component.name,
                description = component.description,
                onClick = { onComponentClick(component.id) },
                modifier = Modifier.padding(
                    horizontal = DSSpacing.horizontalPadding.dp,
                    vertical = (DSSpacing.listItemSpacing / 2).dp
                )
            )
        }
    }
}

@Composable
private fun ThemeToggleRow(isDarkTheme: Boolean, onThemeChanged: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val logoIcon = remember { DSIcon.coloredNamed(context, "frisbee-logo", 44f) as? BitmapDrawable }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        logoIcon?.bitmap?.let { bmp ->
            Image(
                bitmap = bmp.asImageBitmap(),
                contentDescription = "Logo",
                modifier = Modifier.height(44.dp)
            )
        } ?: Spacer(modifier = Modifier.height(44.dp))

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
}

private fun buildStatusText(context: Context): String {
    try {
        val json = context.assets.open("design-system-counts.json").bufferedReader().use {
            JSONObject(it.readText())
        }

        val parts = mutableListOf<String>()

        val componentCount = json.optInt("components", components.size)
        val componentSuffix = if (componentCount == 1) "" else "s"
        var componentText = "$componentCount Component$componentSuffix"
        shortRelativeTime(json.optString("components_updated", ""))?.let {
            componentText += " ($it)"
        }
        parts.add(componentText)

        val iconCount = json.optInt("icons", 0)
        var iconText = "$iconCount Icons"
        shortRelativeTime(json.optString("icons_updated", ""))?.let {
            iconText += " ($it)"
        }
        parts.add(iconText)

        val colorCount = json.optInt("colors", 0)
        var colorText = "$colorCount Colors"
        shortRelativeTime(json.optString("colors_updated", ""))?.let {
            colorText += " ($it)"
        }
        parts.add(colorText)

        return parts.joinToString("  \u00B7  ")
    } catch (_: Exception) {
        val iconCount = try {
            context.assets.list("icons")?.count { it.endsWith(".svg") } ?: 0
        } catch (_: Exception) { 0 }
        return "${components.size} Component \u00B7 $iconCount Icons \u00B7 157 Colors"
    }
}

private fun shortRelativeTime(isoString: String): String? {
    if (isoString.isBlank()) return null
    return try {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.US)
        format.timeZone = TimeZone.getTimeZone("UTC")
        val date = format.parse(isoString) ?: return null
        val seconds = ((Date().time - date.time) / 1000).toInt()
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        when {
            days > 0 -> "${days}d"
            hours > 0 -> "${hours}h"
            minutes > 0 -> "${minutes}m"
            else -> "now"
        }
    } catch (_: Exception) { null }
}

@Composable
private fun SearchBar(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val searchIcon = remember { DSIcon.named(context, "search", 20f) as? BitmapDrawable }
    val clearIcon = remember { DSIcon.named(context, "clear-field", 24f) as? BitmapDrawable }

    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        singleLine = true,
        textStyle = DSTypography.body1R.toComposeTextStyle().copy(
            color = MaterialTheme.colorScheme.onSurface
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(DSCornerRadius.inputField.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(start = 14.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                searchIcon?.bitmap?.let { bmp ->
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = "Search",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                    if (query.isEmpty()) {
                        Text(
                            text = "Search components\u2026",
                            style = DSTypography.body1R.toComposeTextStyle(),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    }
                    innerTextField()
                }
                if (query.isNotEmpty()) {
                    clearIcon?.bitmap?.let { bmp ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .clickable { onQueryChange("") },
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
        modifier = modifier
    )
}

@Composable
private fun ComponentCard(name: String, description: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val chevronIcon = remember { DSIcon.named(context, "arrow-right-s", 20f) as? BitmapDrawable }
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 800f)
    )
    val cardAlpha by animateFloatAsState(
        targetValue = if (isPressed) 0.7f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 800f)
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .alpha(cardAlpha)
            .clip(RoundedCornerShape(DSCornerRadius.card.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { onClick() }
                )
            }
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = DSTypography.subtitle1M.toComposeTextStyle(),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = DSTypography.subhead2R.toComposeTextStyle(),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                maxLines = 2
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        chevronIcon?.bitmap?.let { bmp ->
            Image(
                bitmap = bmp.asImageBitmap(),
                contentDescription = "Open",
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
