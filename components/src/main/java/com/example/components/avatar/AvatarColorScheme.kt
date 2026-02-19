package com.example.components.avatar

import android.graphics.Color

data class AvatarGradientPair(val top: Int, val bottom: Int)

data class AvatarColorScheme(
    val initialsGradientTop: Int,
    val initialsGradientBottom: Int,
    val botGradientTop: Int,
    val botGradientBottom: Int,
    val savedGradientTop: Int,
    val savedGradientBottom: Int,
    val contentColor: Int
) {
    companion object {
        val DEFAULT = AvatarColorScheme(
            initialsGradientTop = Color.parseColor("#4BCBEC"),
            initialsGradientBottom = Color.parseColor("#0099D6"),
            botGradientTop = Color.parseColor("#70ACF1"),
            botGradientBottom = Color.parseColor("#407EDA"),
            savedGradientTop = Color.parseColor("#BABABA"),
            savedGradientBottom = Color.parseColor("#777784"),
            contentColor = Color.WHITE
        )
    }
}
