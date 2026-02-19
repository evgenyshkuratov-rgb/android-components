package com.example.components.designsystem

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import com.example.components.attachedmedia.AttachedMediaColorScheme
import com.example.components.avatar.AvatarColorScheme
import com.example.components.avatar.AvatarGradientPair
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

    fun newBadgeColor(isDark: Boolean): Int = when (this) {
        FRISBEE -> Color.parseColor("#40B259")
        TDM -> Color.parseColor("#E5505A")
        SOVER -> Color.parseColor(if (isDark) "#C4944D" else "#C7964F")
        KCHAT -> Color.parseColor("#E5505A")
        SENSE_NEW -> Color.parseColor(if (isDark) "#824FC2" else "#7548AD")
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

    fun avatarColorScheme(isDark: Boolean): AvatarColorScheme {
        val pairs = avatarGradientPairs(isDark)
        val saved = avatarSavedGradient(isDark)
        val bot = pairs[5] // Pair 5 is the blue bot gradient
        return AvatarColorScheme(
            initialsGradientTop = pairs[0].top,
            initialsGradientBottom = pairs[0].bottom,
            botGradientTop = bot.top,
            botGradientBottom = bot.bottom,
            savedGradientTop = saved.top,
            savedGradientBottom = saved.bottom,
            contentColor = Color.WHITE
        )
    }

    fun avatarSavedGradient(isDark: Boolean): AvatarGradientPair = when (this) {
        SENSE_NEW -> AvatarGradientPair(Color.parseColor("#A8A8A8"), Color.parseColor("#777784"))
        else -> if (isDark) {
            AvatarGradientPair(Color.parseColor("#A8A8A8"), Color.parseColor("#777784"))
        } else {
            AvatarGradientPair(Color.parseColor("#BABABA"), Color.parseColor("#777784"))
        }
    }

    fun avatarGradientPairs(isDark: Boolean): List<AvatarGradientPair> = when (this) {
        SENSE_NEW -> if (isDark) SENSE_GRADIENT_PAIRS_DARK else SENSE_GRADIENT_PAIRS_LIGHT
        else -> if (isDark) DEFAULT_GRADIENT_PAIRS_DARK else DEFAULT_GRADIENT_PAIRS_LIGHT
    }

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

        // Frisbee, TDM, Sover, KCHAT share identical avatarka gradients
        private val DEFAULT_GRADIENT_PAIRS_LIGHT = listOf(
            AvatarGradientPair(Color.parseColor("#4BCBEC"), Color.parseColor("#0099D6")),
            AvatarGradientPair(Color.parseColor("#F2A867"), Color.parseColor("#C75F22")),
            AvatarGradientPair(Color.parseColor("#6CCD65"), Color.parseColor("#41A33F")),
            AvatarGradientPair(Color.parseColor("#AD95EE"), Color.parseColor("#8153CB")),
            AvatarGradientPair(Color.parseColor("#E3B642"), Color.parseColor("#DC881A")),
            AvatarGradientPair(Color.parseColor("#70ACF1"), Color.parseColor("#407EDA")),
            AvatarGradientPair(Color.parseColor("#FF9190"), Color.parseColor("#CF4239")),
            AvatarGradientPair(Color.parseColor("#49D393"), Color.parseColor("#1AA866")),
            AvatarGradientPair(Color.parseColor("#F77BBD"), Color.parseColor("#C04B82")),
            AvatarGradientPair(Color.parseColor("#FF9457"), Color.parseColor("#CC5800")),
            AvatarGradientPair(Color.parseColor("#DA94E7"), Color.parseColor("#AF66BD"))
        )

        private val DEFAULT_GRADIENT_PAIRS_DARK = listOf(
            AvatarGradientPair(Color.parseColor("#84E3F3"), Color.parseColor("#296580")),
            AvatarGradientPair(Color.parseColor("#FEB085"), Color.parseColor("#AC501C")),
            AvatarGradientPair(Color.parseColor("#B1EC5F"), Color.parseColor("#537F23")),
            AvatarGradientPair(Color.parseColor("#E1D6FE"), Color.parseColor("#6841B7")),
            AvatarGradientPair(Color.parseColor("#FED873"), Color.parseColor("#A9742E")),
            AvatarGradientPair(Color.parseColor("#AED3FE"), Color.parseColor("#3F6AAC")),
            AvatarGradientPair(Color.parseColor("#FFA573"), Color.parseColor("#F84316")),
            AvatarGradientPair(Color.parseColor("#95DDA1"), Color.parseColor("#397D3F")),
            AvatarGradientPair(Color.parseColor("#FEA2D3"), Color.parseColor("#9E3F6C")),
            AvatarGradientPair(Color.parseColor("#FEB085"), Color.parseColor("#AB4E37")),
            AvatarGradientPair(Color.parseColor("#EFAFFA"), Color.parseColor("#71377C"))
        )

        private val SENSE_GRADIENT_PAIRS_LIGHT = listOf(
            AvatarGradientPair(Color.parseColor("#EE4AAC"), Color.parseColor("#FF6AC3")),
            AvatarGradientPair(Color.parseColor("#D435CE"), Color.parseColor("#FF71FA")),
            AvatarGradientPair(Color.parseColor("#C14FF3"), Color.parseColor("#F56EFF")),
            AvatarGradientPair(Color.parseColor("#9D35E6"), Color.parseColor("#C776FF")),
            AvatarGradientPair(Color.parseColor("#8840E1"), Color.parseColor("#7C6BFF")),
            AvatarGradientPair(Color.parseColor("#4C71D0"), Color.parseColor("#769DFF")),
            AvatarGradientPair(Color.parseColor("#E03C3C"), Color.parseColor("#FF6A80")),
            AvatarGradientPair(Color.parseColor("#328DC8"), Color.parseColor("#57BCFF")),
            AvatarGradientPair(Color.parseColor("#EA5555"), Color.parseColor("#F58585")),
            AvatarGradientPair(Color.parseColor("#F47EFF"), Color.parseColor("#B44EBD")),
            AvatarGradientPair(Color.parseColor("#F55CBB"), Color.parseColor("#F57AC7"))
        )

        private val SENSE_GRADIENT_PAIRS_DARK = listOf(
            AvatarGradientPair(Color.parseColor("#FF9AD7"), Color.parseColor("#FA45B2")),
            AvatarGradientPair(Color.parseColor("#FF95FB"), Color.parseColor("#D64BD1")),
            AvatarGradientPair(Color.parseColor("#E2A1FF"), Color.parseColor("#BF37FB")),
            AvatarGradientPair(Color.parseColor("#B49BFF"), Color.parseColor("#773AF2")),
            AvatarGradientPair(Color.parseColor("#D293FF"), Color.parseColor("#A840F1")),
            AvatarGradientPair(Color.parseColor("#7EA3FF"), Color.parseColor("#4C71D0")),
            AvatarGradientPair(Color.parseColor("#FF6F85"), Color.parseColor("#CA3737")),
            AvatarGradientPair(Color.parseColor("#8AD0FF"), Color.parseColor("#3894D1")),
            AvatarGradientPair(Color.parseColor("#F27FCC"), Color.parseColor("#FF6161")),
            AvatarGradientPair(Color.parseColor("#F47EFF"), Color.parseColor("#B44EBD")),
            AvatarGradientPair(Color.parseColor("#F57AC7"), Color.parseColor("#DB5CAB"))
        )
    }
}
