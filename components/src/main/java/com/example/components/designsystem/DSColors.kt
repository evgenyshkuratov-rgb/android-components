package com.example.components.designsystem

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.components.R

object DSColors {
    fun backgroundBase(context: Context) = ContextCompat.getColor(context, R.color.ds_background_base)
    fun backgroundSecond(context: Context) = ContextCompat.getColor(context, R.color.ds_background_second)
    fun backgroundSheet(context: Context) = ContextCompat.getColor(context, R.color.ds_background_sheet)
    fun textPrimary(context: Context) = ContextCompat.getColor(context, R.color.ds_text_primary)
    fun textPrimary90(context: Context) = ContextCompat.getColor(context, R.color.ds_text_primary_90)
    fun textSecondary(context: Context) = ContextCompat.getColor(context, R.color.ds_text_secondary)
    fun textTertiary(context: Context) = ContextCompat.getColor(context, R.color.ds_text_tertiary)
    fun basic100(context: Context) = ContextCompat.getColor(context, R.color.ds_basic_100)
    fun basic90(context: Context) = ContextCompat.getColor(context, R.color.ds_basic_90)
    fun basic50(context: Context) = ContextCompat.getColor(context, R.color.ds_basic_50)
    fun basic08(context: Context) = ContextCompat.getColor(context, R.color.ds_basic_08)
    fun separator(context: Context) = ContextCompat.getColor(context, R.color.ds_separator)
    fun chipBackground(context: Context) = ContextCompat.getColor(context, R.color.ds_chip_background)
    fun success(context: Context) = ContextCompat.getColor(context, R.color.ds_success)
    fun danger(context: Context) = ContextCompat.getColor(context, R.color.ds_danger)
    fun warning(context: Context) = ContextCompat.getColor(context, R.color.ds_warning)
    fun white100(context: Context) = ContextCompat.getColor(context, R.color.ds_white_100)
    fun badgeMuted(context: Context) = ContextCompat.getColor(context, R.color.ds_badge_muted)
}
