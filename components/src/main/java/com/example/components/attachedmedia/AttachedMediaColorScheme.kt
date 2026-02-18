package com.example.components.attachedmedia

import android.graphics.Color

data class AttachedMediaColorScheme(
    val backgroundFile: Int,
    val backgroundFileError: Int,
    val filePreviewBg: Int,
    val fileIconTint: Int,
    val fileIconErrorTint: Int,
    val fileNameColor: Int,
    val fileSizeColor: Int,
    val fileNameErrorColor: Int,
    val fileSubErrorColor: Int,
    val mediaErrorTextColor: Int,
    val closeButtonBg: Int,
    val closeIconTint: Int,
    val badgeBg: Int,
    val badgeTextColor: Int,
    val dangerColor: Int,
    val videoPlayBg: Int,
    val videoPlayIconTint: Int
) {
    companion object {
        val DEFAULT = AttachedMediaColorScheme(
            backgroundFile = Color.parseColor("#F5F5F5"),
            backgroundFileError = Color.parseColor("#E06141"),
            filePreviewBg = Color.parseColor("#26000000"),
            fileIconTint = Color.parseColor("#8C000000"),
            fileIconErrorTint = Color.parseColor("#CCFFFFFF"),
            fileNameColor = Color.parseColor("#E6000000"),
            fileSizeColor = Color.parseColor("#80000000"),
            fileNameErrorColor = Color.WHITE,
            fileSubErrorColor = Color.parseColor("#B3FFFFFF"),
            mediaErrorTextColor = Color.WHITE,
            closeButtonBg = Color.parseColor("#CC000000"),
            closeIconTint = Color.WHITE,
            badgeBg = Color.parseColor("#CC000000"),
            badgeTextColor = Color.WHITE,
            dangerColor = Color.parseColor("#E06141"),
            videoPlayBg = Color.parseColor("#99000000"),
            videoPlayIconTint = Color.parseColor("#B3FFFFFF")
        )
    }
}
