package com.example.components.designsystem

import android.graphics.Path
import android.graphics.PointF

object SVGPathParser {

    fun parse(d: String, fillRule: Path.FillType = Path.FillType.WINDING): Path {
        val path = Path()
        path.fillType = fillRule
        val tokens = tokenize(d)
        var i = 0
        var currentCommand = 'M'
        val currentPoint = PointF(0f, 0f)

        while (i < tokens.size) {
            val token = tokens[i]
            if (token.length == 1 && token[0].isLetter()) {
                currentCommand = token[0]
                i++
            }
            when (currentCommand) {
                'M' -> { val x = number(tokens, i); i++; val y = number(tokens, i); i++; path.moveTo(x, y); currentPoint.set(x, y); currentCommand = 'L' }
                'm' -> { val dx = number(tokens, i); i++; val dy = number(tokens, i); i++; path.rMoveTo(dx, dy); currentPoint.offset(dx, dy); currentCommand = 'l' }
                'L' -> { val x = number(tokens, i); i++; val y = number(tokens, i); i++; path.lineTo(x, y); currentPoint.set(x, y) }
                'l' -> { val dx = number(tokens, i); i++; val dy = number(tokens, i); i++; path.rLineTo(dx, dy); currentPoint.offset(dx, dy) }
                'H' -> { val x = number(tokens, i); i++; path.lineTo(x, currentPoint.y); currentPoint.x = x }
                'h' -> { val dx = number(tokens, i); i++; path.rLineTo(dx, 0f); currentPoint.x += dx }
                'V' -> { val y = number(tokens, i); i++; path.lineTo(currentPoint.x, y); currentPoint.y = y }
                'v' -> { val dy = number(tokens, i); i++; path.rLineTo(0f, dy); currentPoint.y += dy }
                'C' -> { val x1 = number(tokens, i); i++; val y1 = number(tokens, i); i++; val x2 = number(tokens, i); i++; val y2 = number(tokens, i); i++; val x = number(tokens, i); i++; val y = number(tokens, i); i++; path.cubicTo(x1, y1, x2, y2, x, y); currentPoint.set(x, y) }
                'c' -> { val x1 = number(tokens, i); i++; val y1 = number(tokens, i); i++; val x2 = number(tokens, i); i++; val y2 = number(tokens, i); i++; val dx = number(tokens, i); i++; val dy = number(tokens, i); i++; path.rCubicTo(x1, y1, x2, y2, dx, dy); currentPoint.offset(dx, dy) }
                'Z', 'z' -> { path.close() }
                else -> i++
            }
        }
        return path
    }

    private fun tokenize(d: String): List<String> {
        val tokens = mutableListOf<String>()
        val current = StringBuilder()
        for (ch in d) {
            when {
                ch.isLetter() -> { if (current.isNotEmpty()) { tokens.add(current.toString()); current.clear() }; tokens.add(ch.toString()) }
                ch == ',' || ch == ' ' || ch == '\n' || ch == '\t' -> { if (current.isNotEmpty()) { tokens.add(current.toString()); current.clear() } }
                ch == '-' && current.isNotEmpty() && current.last() != 'e' && current.last() != 'E' -> { tokens.add(current.toString()); current.clear(); current.append(ch) }
                else -> current.append(ch)
            }
        }
        if (current.isNotEmpty()) tokens.add(current.toString())
        return tokens
    }

    private fun number(tokens: List<String>, index: Int): Float = if (index < tokens.size) tokens[index].toFloatOrNull() ?: 0f else 0f
}
