package com.example.components.designsystem

object DSSpacing {
    const val horizontalPadding = 16
    const val verticalSection = 24
    const val listItemSpacing = 12
    const val chipGap = 8
    const val innerCardPadding = 16
}

object DSCornerRadius {
    const val card = 16f
    const val inputField = 12f
    fun capsule(height: Float) = height / 2f
    fun circle(size: Float) = size / 2f
}
