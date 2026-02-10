package com.example.components.chips

import android.graphics.Color

data class ChipsColorScheme(
    val backgroundDefault: Int,
    val backgroundActive: Int,
    val textPrimary: Int,
    val closeIconTint: Int
) {
    companion object {
        val DEFAULT = ChipsColorScheme(
            backgroundDefault = Color.parseColor("#14000000"),
            backgroundActive = Color.parseColor("#40B259"),
            textPrimary = Color.parseColor("#E6000000"),
            closeIconTint = Color.parseColor("#80000000")
        )
    }
}
