package com.example.gallery.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.components.designsystem.DSTextStyle
import com.example.components.R as ComponentsR

val RobotoFamily = FontFamily(
    Font(ComponentsR.font.roboto, FontWeight.Normal),
    Font(ComponentsR.font.roboto, FontWeight.Medium),
    Font(ComponentsR.font.roboto, FontWeight.Bold),
)

val RobotoMonoFamily = FontFamily(
    Font(ComponentsR.font.roboto_mono, FontWeight.Normal),
    Font(ComponentsR.font.roboto_mono, FontWeight.Medium),
    Font(ComponentsR.font.roboto_mono, FontWeight.Bold),
)

fun DSTextStyle.toComposeTextStyle(): TextStyle {
    val family = if (isMono) RobotoMonoFamily else RobotoFamily
    return TextStyle(
        fontFamily = family,
        fontWeight = FontWeight(weight),
        fontSize = sizeSp.sp,
        lineHeight = lineHeightSp.sp,
        letterSpacing = if (letterSpacingSp != 0f) letterSpacingSp.sp else 0.sp
    )
}
