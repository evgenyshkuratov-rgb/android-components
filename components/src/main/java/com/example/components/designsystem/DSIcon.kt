package com.example.components.designsystem

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable

object DSIcon {

    fun named(context: Context, name: String, sizeDp: Float = 24f): Drawable? {
        val svgString = loadSvgFromAssets(context, "icons/$name.svg") ?: return null
        val density = context.resources.displayMetrics.density
        val sizePx = (sizeDp * density).toInt()
        val viewBox = parseViewBox(svgString) ?: RectF(0f, 0f, 24f, 24f)
        val paths = parsePaths(svgString)
        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val scaleX = sizePx / viewBox.width()
        val scaleY = sizePx / viewBox.height()
        canvas.translate(-viewBox.left * scaleX, -viewBox.top * scaleY)
        canvas.scale(scaleX, scaleY)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL
        for (path in paths) { canvas.drawPath(path, paint) }
        return BitmapDrawable(context.resources, bitmap)
    }

    fun coloredNamed(context: Context, name: String, heightDp: Float): Drawable? {
        val svgString = loadSvgFromAssets(context, "icons/$name.svg") ?: return null
        val density = context.resources.displayMetrics.density
        val heightPx = (heightDp * density).toInt()
        val viewBox = parseViewBox(svgString) ?: RectF(0f, 0f, 24f, 24f)
        val coloredPaths = parseColoredPaths(svgString)
        val aspectRatio = viewBox.width() / viewBox.height()
        val widthPx = (heightPx * aspectRatio).toInt()
        val bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val scaleX = widthPx.toFloat() / viewBox.width()
        val scaleY = heightPx.toFloat() / viewBox.height()
        canvas.translate(-viewBox.left * scaleX, -viewBox.top * scaleY)
        canvas.scale(scaleX, scaleY)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.FILL
        for ((path, color) in coloredPaths) { paint.color = color; canvas.drawPath(path, paint) }
        return BitmapDrawable(context.resources, bitmap)
    }

    private fun loadSvgFromAssets(context: Context, path: String): String? {
        return try { context.assets.open(path).bufferedReader().use { it.readText() } } catch (e: Exception) { null }
    }

    private fun parseViewBox(svg: String): RectF? {
        val regex = Regex("""viewBox="([^"]+)"""")
        val match = regex.find(svg) ?: return null
        val parts = match.groupValues[1].split(" ").mapNotNull { it.toFloatOrNull() }
        if (parts.size != 4) return null
        return RectF(parts[0], parts[1], parts[0] + parts[2], parts[1] + parts[3])
    }

    private fun parsePaths(svg: String): List<Path> {
        val results = mutableListOf<Path>()
        val pathRegex = Regex("""<path[^>]*/>|<path[^>]*>""")
        for (match in pathRegex.findAll(svg)) {
            val tag = match.value
            val d = extractAttribute("d", tag) ?: continue
            val fillRule = if (extractAttribute("fill-rule", tag) == "evenodd") Path.FillType.EVEN_ODD else Path.FillType.WINDING
            results.add(SVGPathParser.parse(d, fillRule))
        }
        return results
    }

    private fun parseColoredPaths(svg: String): List<Pair<Path, Int>> {
        val results = mutableListOf<Pair<Path, Int>>()
        val pathRegex = Regex("""<path[^>]*/>|<path[^>]*>""")
        for (match in pathRegex.findAll(svg)) {
            val tag = match.value
            val d = extractAttribute("d", tag) ?: continue
            val fillRule = if (extractAttribute("fill-rule", tag) == "evenodd") Path.FillType.EVEN_ODD else Path.FillType.WINDING
            val path = SVGPathParser.parse(d, fillRule)
            val hex = extractAttribute("fill", tag)
            val color = if (hex != null && hex != "none") { try { Color.parseColor(hex) } catch (e: Exception) { Color.BLACK } } else Color.BLACK
            results.add(path to color)
        }
        return results
    }

    private fun extractAttribute(name: String, tag: String): String? {
        val regex = Regex("""$name="([^"]+)"""")
        return regex.find(tag)?.groupValues?.get(1)
    }
}
