package com.example.components.checkbox

import android.graphics.*
import android.graphics.drawable.Drawable
import com.example.components.designsystem.SVGPathParser

class CheckboxDrawable : Drawable() {

    enum class Shape { SQUARE, CIRCLE }

    var shape: Shape = Shape.SQUARE
        set(value) { field = value; invalidateSelf() }

    var isChecked: Boolean = false
        set(value) { field = value; invalidateSelf() }

    var isViewEnabled: Boolean = true
        set(value) { field = value; invalidateSelf() }

    var colorScheme: CheckboxColorScheme = CheckboxColorScheme.DEFAULT
        set(value) { field = value; invalidateSelf() }

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }
    private val checkmarkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val checkmarkPath: Path by lazy {
        SVGPathParser.parse(CHECKMARK_PATH_DATA)
    }

    override fun draw(canvas: Canvas) {
        val width = bounds.width().toFloat()
        val height = bounds.height().toFloat()
        if (width <= 0f || height <= 0f) return

        val scaleX = width / VIEWBOX_SIZE
        val scaleY = height / VIEWBOX_SIZE
        val strokeWidth = 2f * scaleX

        if (isChecked) {
            fillPaint.color = colorScheme.checkedFill
            drawShape(canvas, fillPaint, width, height, CORNER_RADIUS * scaleX, Paint.Style.FILL, 0f)

            canvas.save()
            canvas.scale(scaleX, scaleY)
            checkmarkPaint.color = colorScheme.checkmarkColor
            canvas.drawPath(checkmarkPath, checkmarkPaint)
            canvas.restore()
        } else {
            val borderColor = if (isViewEnabled) colorScheme.borderEnabled else colorScheme.borderDisabled
            strokePaint.color = borderColor
            strokePaint.strokeWidth = strokeWidth
            drawShape(canvas, strokePaint, width, height, CORNER_RADIUS * scaleX, Paint.Style.STROKE, strokeWidth)
        }
    }

    private fun drawShape(canvas: Canvas, paint: Paint, w: Float, h: Float, cornerRadius: Float, style: Paint.Style, strokeWidth: Float) {
        paint.style = style
        val inset = strokeWidth / 2f
        when (shape) {
            Shape.SQUARE -> {
                val rect = RectF(inset, inset, w - inset, h - inset)
                canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
            }
            Shape.CIRCLE -> {
                canvas.drawOval(RectF(inset, inset, w - inset, h - inset), paint)
            }
        }
    }

    override fun setAlpha(alpha: Int) {
        fillPaint.alpha = alpha
        strokePaint.alpha = alpha
        checkmarkPaint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        fillPaint.colorFilter = colorFilter
        strokePaint.colorFilter = colorFilter
        checkmarkPaint.colorFilter = colorFilter
    }

    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun getIntrinsicWidth(): Int = VIEWBOX_SIZE.toInt()
    override fun getIntrinsicHeight(): Int = VIEWBOX_SIZE.toInt()

    companion object {
        private const val VIEWBOX_SIZE = 24f
        private const val CORNER_RADIUS = 6f
        private const val CHECKMARK_PATH_DATA =
            "M8.72153 11.3035C8.3277 10.9329 7.68919 10.9329 7.29537 11.3035C6.90154 11.674 6.90154 12.2748 7.29537 12.6454L10.4917 15.6527C10.6947 15.8438 11.0148 15.8319 11.203 15.6263L16.7046 9.61977C17.0985 9.24922 17.0985 8.64845 16.7046 8.27791C16.3108 7.90736 15.6723 7.90736 15.2785 8.27791L10.8608 13.1006L8.72153 11.3035Z"
    }
}
