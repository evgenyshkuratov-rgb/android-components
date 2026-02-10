package com.example.gallery.catalog

import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.designsystem.DSIcon
import com.example.components.R as ComponentsR

private val RobotoFamily = FontFamily(
    Font(ComponentsR.font.roboto, FontWeight.Normal),
    Font(ComponentsR.font.roboto, FontWeight.Medium),
    Font(ComponentsR.font.roboto, FontWeight.Bold),
)

private data class ComponentEntry(val id: String, val name: String, val description: String)

private val components = listOf(
    ComponentEntry("ChipsView", "ChipsView", "Filter chips with Default, Active, and Avatar states")
)

@Composable
fun CatalogScreen(onComponentClick: (String) -> Unit) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    val filteredComponents = remember(searchQuery) {
        if (searchQuery.isBlank()) components
        else components.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }
    val iconCount = remember {
        try {
            context.assets.list("icons")?.count { it.endsWith(".svg") } ?: 0
        } catch (_: Exception) { 0 }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 48.dp)
    ) {
        item { Spacer(modifier = Modifier.height(28.dp).statusBarsPadding()) }
        item {
            Text(
                text = "Components Library",
                style = TextStyle(fontFamily = RobotoFamily, fontWeight = FontWeight.Bold, fontSize = 32.sp, lineHeight = 40.sp, letterSpacing = 0.11.sp),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        item { Spacer(modifier = Modifier.height(6.dp)) }
        item {
            Text(
                text = "${components.size} Component · $iconCount Icons · 157 Colors · Updated ${java.text.SimpleDateFormat("d MMM yyyy", java.util.Locale.US).format(java.util.Date())}",
                style = TextStyle(fontFamily = RobotoFamily, fontWeight = FontWeight.Normal, fontSize = 13.sp, lineHeight = 16.sp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { SearchBar(query = searchQuery, onQueryChange = { searchQuery = it }, modifier = Modifier.padding(horizontal = 16.dp)) }
        item { Spacer(modifier = Modifier.height(28.dp)) }
        items(filteredComponents) { component ->
            ComponentCard(name = component.name, description = component.description, onClick = { onComponentClick(component.id) }, modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp))
        }
    }
}

@Composable
private fun SearchBar(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val searchIcon = remember { DSIcon.named(context, "search", 20f) as? BitmapDrawable }

    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        singleLine = true,
        textStyle = TextStyle(fontFamily = RobotoFamily, fontWeight = FontWeight.Normal, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier.fillMaxWidth().height(48.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceVariant).padding(horizontal = 14.dp),
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
                        Text(text = "Search components\u2026", style = TextStyle(fontFamily = RobotoFamily, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)))
                    }
                    innerTextField()
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
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = if (isPressed) 0.95f else 1f

    Row(
        modifier = modifier.fillMaxWidth().scale(scale).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surfaceVariant).clickable(interactionSource = interactionSource, indication = null) { onClick() }.padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, style = TextStyle(fontFamily = RobotoFamily, fontWeight = FontWeight.Medium, fontSize = 18.sp, lineHeight = 24.sp), color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = description, style = TextStyle(fontFamily = RobotoFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), maxLines = 2)
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
