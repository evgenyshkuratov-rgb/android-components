package com.example.components.designsystem

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import com.example.components.attachedmedia.AttachedMediaColorScheme
import com.example.components.avatar.AvatarColorScheme
import com.example.components.checkbox.CheckboxColorScheme
import com.example.components.chips.ChipsColorScheme

enum class DSBrand(val displayName: String) {
    FRISBEE("Frisbee"),
    TDM("TDM"),
    SOVER("Sover"),
    KCHAT("KCHAT"),
    SENSE_NEW("Sens");

    fun accentColor(isDark: Boolean): Int = when (this) {
        FRISBEE -> Color.parseColor("#40B259")
        TDM -> Color.parseColor(if (isDark) "#3886E1" else "#3E87DD")
        SOVER -> Color.parseColor(if (isDark) "#C4944D" else "#C7964F")
        KCHAT -> Color.parseColor(if (isDark) "#E9474E" else "#EA5355")
        SENSE_NEW -> Color.parseColor("#7548AD")
    }

    fun backgroundBase(isDark: Boolean): Int {
        if (!isDark) return Color.parseColor("#FFFFFF")
        return when (this) {
            SOVER -> Color.parseColor("#101D2E")
            SENSE_NEW -> Color.parseColor("#161419")
            else -> Color.parseColor("#1A1A1A")
        }
    }

    fun backgroundSecond(isDark: Boolean): Int {
        if (!isDark) return Color.parseColor("#F5F5F5")
        return when (this) {
            SOVER -> Color.parseColor("#1C2838")
            SENSE_NEW -> Color.parseColor("#2A282E")
            else -> Color.parseColor("#313131")
        }
    }

    fun basicColor08(isDark: Boolean): Int = Color.parseColor(if (isDark) "#14FFFFFF" else "#14000000")
    fun basicColor90(isDark: Boolean): Int = Color.parseColor(if (isDark) "#E6FFFFFF" else "#E6000000")
    fun basicColor50(isDark: Boolean): Int = Color.parseColor(if (isDark) "#80FFFFFF" else "#80000000")
    fun basicColor55(isDark: Boolean): Int = Color.parseColor(if (isDark) "#8CFFFFFF" else "#8C000000")
    fun basicColor15(isDark: Boolean): Int = Color.parseColor(if (isDark) "#26FFFFFF" else "#26000000")
    fun basicColor25(isDark: Boolean): Int = Color.parseColor(if (isDark) "#40FFFFFF" else "#40000000")
    fun basicColor60(isDark: Boolean): Int = Color.parseColor(if (isDark) "#99FFFFFF" else "#99000000")

    fun checkboxColorScheme(isDark: Boolean): CheckboxColorScheme {
        val accent = accentColor(isDark)
        val accentDisabled = (accent and 0x00FFFFFF) or 0x66000000
        return CheckboxColorScheme(
            borderEnabled = basicColor55(isDark),
            borderDisabled = basicColor25(isDark),
            checkedFill = accent,
            checkedFillDisabled = accentDisabled,
            checkmarkColor = Color.WHITE,
            checkmarkColorDisabled = Color.parseColor("#80FFFFFF"),
            textEnabled = basicColor50(isDark),
            textDisabled = basicColor25(isDark)
        )
    }

    fun dangerDefault(): Int = Color.parseColor("#E06141")

    fun attachedMediaColorScheme(isDark: Boolean) = AttachedMediaColorScheme(
        backgroundFile = backgroundSecond(isDark),
        backgroundFileError = dangerDefault(),
        filePreviewBg = basicColor15(isDark),
        fileIconTint = basicColor55(isDark),
        fileIconErrorTint = Color.parseColor("#CCFFFFFF"),
        fileNameColor = basicColor90(isDark),
        fileSizeColor = basicColor50(isDark),
        fileNameErrorColor = Color.WHITE,
        fileSubErrorColor = Color.parseColor("#B3FFFFFF"),
        mediaErrorTextColor = Color.WHITE,
        closeButtonBg = Color.parseColor("#CC000000"),
        closeIconTint = Color.WHITE,
        badgeBg = Color.parseColor("#CC000000"),
        badgeTextColor = Color.WHITE,
        dangerColor = dangerDefault(),
        videoPlayBg = Color.parseColor("#80000000"),
        videoPlayIconTint = Color.parseColor("#B3FFFFFF")
    )

    fun avatarColorScheme(isDark: Boolean) = AvatarColorScheme(
        initialsGradientTop = Color.parseColor("#4BCBEC"),
        initialsGradientBottom = Color.parseColor("#0099D6"),
        botGradientTop = Color.parseColor("#70ACF1"),
        botGradientBottom = Color.parseColor("#407EDA"),
        savedGradientTop = Color.parseColor("#BABABA"),
        savedGradientBottom = Color.parseColor("#777784"),
        contentColor = Color.WHITE
    )

    fun chipsColorScheme(isDark: Boolean) = ChipsColorScheme(
        backgroundDefault = basicColor08(isDark),
        backgroundActive = accentColor(isDark),
        textPrimary = basicColor90(isDark),
        textActive = Color.WHITE,
        closeIconTint = basicColor50(isDark)
    )

    companion object {
        fun isDarkMode(context: Context): Boolean =
            (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }
}
