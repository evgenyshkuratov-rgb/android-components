package com.example.components.designsystem

import android.content.Context
import android.graphics.Typeface
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.example.components.R

data class DSTextStyle(
    val sizeSp: Float,
    val weight: Int,
    val lineHeightSp: Float,
    val letterSpacingSp: Float = 0f,
    val isMono: Boolean = false
) {
    fun apply(textView: TextView) {
        textView.textSize = sizeSp
        textView.setLineSpacing(0f, lineHeightSp / sizeSp)
        if (letterSpacingSp != 0f) {
            textView.letterSpacing = letterSpacingSp / sizeSp
        }
        val typeface = getTypeface(textView.context)
        if (typeface != null) {
            textView.typeface = Typeface.create(typeface, weight, false)
        }
    }

    fun getTypeface(context: Context): Typeface? {
        return try {
            val resId = if (isMono) R.font.roboto_mono else R.font.roboto
            ResourcesCompat.getFont(context, resId)
        } catch (e: Exception) {
            null
        }
    }
}

object DSTypography {
    val title1B = DSTextStyle(sizeSp = 32f, weight = 700, lineHeightSp = 40f, letterSpacingSp = 0.11f)
    val title2B = DSTextStyle(sizeSp = 28f, weight = 700, lineHeightSp = 32f)
    val title3B = DSTextStyle(sizeSp = 24f, weight = 700, lineHeightSp = 32f)
    val title4R = DSTextStyle(sizeSp = 24f, weight = 400, lineHeightSp = 32f)
    val title5B = DSTextStyle(sizeSp = 20f, weight = 700, lineHeightSp = 28f)
    val title6M = DSTextStyle(sizeSp = 20f, weight = 500, lineHeightSp = 28f)
    val title7R = DSTextStyle(sizeSp = 20f, weight = 400, lineHeightSp = 28f)
    val subtitle1M = DSTextStyle(sizeSp = 18f, weight = 500, lineHeightSp = 24f)
    val subtitle2R = DSTextStyle(sizeSp = 18f, weight = 400, lineHeightSp = 24f)
    val body1R = DSTextStyle(sizeSp = 16f, weight = 400, lineHeightSp = 20f)
    val body2B = DSTextStyle(sizeSp = 16f, weight = 700, lineHeightSp = 22f, letterSpacingSp = 0.32f)
    val body3M = DSTextStyle(sizeSp = 16f, weight = 500, lineHeightSp = 22f)
    val body4M = DSTextStyle(sizeSp = 14f, weight = 500, lineHeightSp = 16f)
    val body5R = DSTextStyle(sizeSp = 14f, weight = 400, lineHeightSp = 16f)
    val subhead1B = DSTextStyle(sizeSp = 14f, weight = 700, lineHeightSp = 20f)
    val subhead2R = DSTextStyle(sizeSp = 14f, weight = 400, lineHeightSp = 20f)
    val subhead3R = DSTextStyle(sizeSp = 13f, weight = 400, lineHeightSp = 16f)
    val subhead4M = DSTextStyle(sizeSp = 14f, weight = 500, lineHeightSp = 20f)
    val caption1B = DSTextStyle(sizeSp = 12f, weight = 700, lineHeightSp = 16f)
    val caption2R = DSTextStyle(sizeSp = 12f, weight = 400, lineHeightSp = 14f)
    val caption3M = DSTextStyle(sizeSp = 11f, weight = 500, lineHeightSp = 14f)
    val subcaptionR = DSTextStyle(sizeSp = 11f, weight = 400, lineHeightSp = 13f)
    val bubbleR13 = DSTextStyle(sizeSp = 13f, weight = 400, lineHeightSp = 16f)
    val bubbleR14 = DSTextStyle(sizeSp = 14f, weight = 400, lineHeightSp = 18f)
    val bubbleR15 = DSTextStyle(sizeSp = 15f, weight = 400, lineHeightSp = 20f)
    val bubbleR16 = DSTextStyle(sizeSp = 16f, weight = 400, lineHeightSp = 22f)
    val bubbleR18 = DSTextStyle(sizeSp = 18f, weight = 400, lineHeightSp = 24f)
    val bubbleR20 = DSTextStyle(sizeSp = 20f, weight = 400, lineHeightSp = 24f)
    val bubbleR22 = DSTextStyle(sizeSp = 24f, weight = 400, lineHeightSp = 30f)
    val bubbleM13 = DSTextStyle(sizeSp = 13f, weight = 500, lineHeightSp = 16f)
    val bubbleMonoR13 = DSTextStyle(sizeSp = 13f, weight = 400, lineHeightSp = 16f, isMono = true)
    val bubbleMonoR14 = DSTextStyle(sizeSp = 14f, weight = 400, lineHeightSp = 18f, isMono = true)
    val bubbleMonoR15 = DSTextStyle(sizeSp = 15f, weight = 400, lineHeightSp = 20f, isMono = true)
    val bubbleMonoR16 = DSTextStyle(sizeSp = 16f, weight = 400, lineHeightSp = 22f, isMono = true)
    val bubbleMonoR18 = DSTextStyle(sizeSp = 18f, weight = 400, lineHeightSp = 24f, isMono = true)
    val bubbleMonoR20 = DSTextStyle(sizeSp = 20f, weight = 400, lineHeightSp = 24f, isMono = true)
    val bubbleMonoR22 = DSTextStyle(sizeSp = 24f, weight = 400, lineHeightSp = 30f, isMono = true)
}
