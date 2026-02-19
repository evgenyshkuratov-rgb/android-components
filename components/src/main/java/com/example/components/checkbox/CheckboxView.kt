package com.example.components.checkbox

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.example.components.R

class CheckboxView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    enum class Shape { SQUARE, CIRCLE }

    var onCheckedChange: ((Boolean) -> Unit)? = null

    private var currentShape: Shape = Shape.SQUARE
    private var isChecked: Boolean = false
    private var isViewEnabled: Boolean = true
    private var showText: Boolean = true
    private var colorScheme: CheckboxColorScheme = CheckboxColorScheme.DEFAULT

    private val indicator: View
    private val label: TextView
    private val checkboxDrawable = CheckboxDrawable()
    private val density = context.resources.displayMetrics.density

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL

        LayoutInflater.from(context).inflate(R.layout.view_checkbox, this, true)

        indicator = findViewById(R.id.checkboxIndicator)
        label = findViewById(R.id.checkboxLabel)

        indicator.background = checkboxDrawable

        setOnClickListener {
            if (isViewEnabled) {
                toggle()
            }
        }

        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.CheckboxView)
            try {
                val shapeInt = ta.getInt(R.styleable.CheckboxView_checkboxShape, 0)
                currentShape = Shape.entries[shapeInt]
                isChecked = ta.getBoolean(R.styleable.CheckboxView_checkboxChecked, false)
                isViewEnabled = ta.getBoolean(R.styleable.CheckboxView_checkboxEnabled, true)
                showText = ta.getBoolean(R.styleable.CheckboxView_checkboxShowText, true)
                val text = ta.getString(R.styleable.CheckboxView_checkboxText)
                if (text != null) label.text = text

                val borderEnabled = ta.getColor(R.styleable.CheckboxView_checkboxBorderEnabled, colorScheme.borderEnabled)
                val borderDisabled = ta.getColor(R.styleable.CheckboxView_checkboxBorderDisabled, colorScheme.borderDisabled)
                val checkedFill = ta.getColor(R.styleable.CheckboxView_checkboxCheckedFill, colorScheme.checkedFill)
                val checkedFillDisabled = ta.getColor(R.styleable.CheckboxView_checkboxCheckedFillDisabled, colorScheme.checkedFillDisabled)
                val checkmarkColor = ta.getColor(R.styleable.CheckboxView_checkboxCheckmarkColor, colorScheme.checkmarkColor)
                val checkmarkColorDisabled = ta.getColor(R.styleable.CheckboxView_checkboxCheckmarkColorDisabled, colorScheme.checkmarkColorDisabled)
                val textEnabled = ta.getColor(R.styleable.CheckboxView_checkboxTextEnabled, colorScheme.textEnabled)
                val textDisabled = ta.getColor(R.styleable.CheckboxView_checkboxTextDisabled, colorScheme.textDisabled)
                colorScheme = CheckboxColorScheme(borderEnabled, borderDisabled, checkedFill, checkedFillDisabled, checkmarkColor, checkmarkColorDisabled, textEnabled, textDisabled)
            } finally {
                ta.recycle()
            }
        }
        updateAppearance()
    }

    fun configure(
        text: String = "",
        shape: Shape = Shape.SQUARE,
        isChecked: Boolean = false,
        isEnabled: Boolean = true,
        showText: Boolean = true,
        colorScheme: CheckboxColorScheme = CheckboxColorScheme.DEFAULT
    ) {
        currentShape = shape
        this.isChecked = isChecked
        this.isViewEnabled = isEnabled
        this.showText = showText
        this.colorScheme = colorScheme
        label.text = text
        updateAppearance()
    }

    fun setChecked(checked: Boolean) {
        if (this.isChecked != checked) {
            this.isChecked = checked
            updateAppearance()
        }
    }

    fun getChecked(): Boolean = isChecked

    fun toggle() {
        isChecked = !isChecked
        updateAppearance()
        onCheckedChange?.invoke(isChecked)
    }

    private fun updateAppearance() {
        checkboxDrawable.shape = when (currentShape) {
            Shape.SQUARE -> CheckboxDrawable.Shape.SQUARE
            Shape.CIRCLE -> CheckboxDrawable.Shape.CIRCLE
        }
        checkboxDrawable.isChecked = isChecked
        checkboxDrawable.isViewEnabled = isViewEnabled
        checkboxDrawable.colorScheme = colorScheme

        val robotoTypeface = try {
            ResourcesCompat.getFont(context, R.font.roboto)
        } catch (_: Exception) {
            Typeface.DEFAULT
        }

        label.typeface = Typeface.create(robotoTypeface, 400, false)
        label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        label.setLineSpacing(0f, 1f)
        label.setTextColor(if (isViewEnabled) colorScheme.textEnabled else colorScheme.textDisabled)

        if (showText && label.text.isNotEmpty()) {
            label.visibility = VISIBLE
            (label.layoutParams as? MarginLayoutParams)?.marginStart = dpToPx(8)
        } else {
            label.visibility = GONE
        }

        indicator.invalidate()
    }

    private fun dpToPx(dp: Int): Int = (dp * density).toInt()
}
