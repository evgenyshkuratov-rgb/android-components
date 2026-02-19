package com.example.components.checkbox

import android.graphics.Color

data class CheckboxColorScheme(
    val borderEnabled: Int,
    val borderDisabled: Int,
    val checkedFill: Int,
    val checkedFillDisabled: Int,
    val checkmarkColor: Int,
    val checkmarkColorDisabled: Int,
    val textEnabled: Int,
    val textDisabled: Int
) {
    companion object {
        val DEFAULT = CheckboxColorScheme(
            borderEnabled = Color.parseColor("#8C000000"),
            borderDisabled = Color.parseColor("#40000000"),
            checkedFill = Color.parseColor("#40B259"),
            checkedFillDisabled = Color.parseColor("#6640B259"),
            checkmarkColor = Color.WHITE,
            checkmarkColorDisabled = Color.parseColor("#80FFFFFF"),
            textEnabled = Color.parseColor("#80000000"),
            textDisabled = Color.parseColor("#40000000")
        )
    }
}
