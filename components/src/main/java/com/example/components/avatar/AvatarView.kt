package com.example.components.avatar

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.example.components.R
import com.example.components.designsystem.DSIcon
import com.example.components.designsystem.DSTypography

class AvatarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    enum class AvatarViewType { IMAGE, INITIALS, BOT, SAVED }

    enum class AvatarSize(val dp: Int) {
        SIZE_160(160),
        SIZE_120(120),
        SIZE_80(80),
        SIZE_56(56),
        SIZE_48(48),
        SIZE_40(40),
        SIZE_36(36),
        SIZE_32(32),
        SIZE_24(24),
        SIZE_20(20),
        SIZE_18(18),
        SIZE_16(16)
    }

    private var currentType: AvatarViewType = AvatarViewType.IMAGE
    private var currentSize: AvatarSize = AvatarSize.SIZE_48
    private var initialsText: String = ""
    private var imageBitmap: Bitmap? = null
    private var colorScheme: AvatarColorScheme = AvatarColorScheme.DEFAULT

    private val avatarImage: ImageView
    private val avatarInitials: TextView
    private val avatarIcon: ImageView
    private val density = context.resources.displayMetrics.density

    init {
        LayoutInflater.from(context).inflate(R.layout.view_avatar, this, true)

        avatarImage = findViewById(R.id.avatarImage)
        avatarInitials = findViewById(R.id.avatarInitials)
        avatarIcon = findViewById(R.id.avatarIcon)

        outlineProvider = ViewOutlineProvider.BACKGROUND
        clipToOutline = true

        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.AvatarView)
            try {
                val typeInt = ta.getInt(R.styleable.AvatarView_avatarViewType, 0)
                currentType = AvatarViewType.entries[typeInt]
                val sizeInt = ta.getInt(R.styleable.AvatarView_avatarSize, 4)
                currentSize = AvatarSize.entries[sizeInt]
                initialsText = ta.getString(R.styleable.AvatarView_avatarText) ?: ""

                val initialsTop = ta.getColor(R.styleable.AvatarView_avatarInitialsGradientTop, colorScheme.initialsGradientTop)
                val initialsBottom = ta.getColor(R.styleable.AvatarView_avatarInitialsGradientBottom, colorScheme.initialsGradientBottom)
                val botTop = ta.getColor(R.styleable.AvatarView_avatarBotGradientTop, colorScheme.botGradientTop)
                val botBottom = ta.getColor(R.styleable.AvatarView_avatarBotGradientBottom, colorScheme.botGradientBottom)
                val savedTop = ta.getColor(R.styleable.AvatarView_avatarSavedGradientTop, colorScheme.savedGradientTop)
                val savedBottom = ta.getColor(R.styleable.AvatarView_avatarSavedGradientBottom, colorScheme.savedGradientBottom)
                val content = ta.getColor(R.styleable.AvatarView_avatarContentColor, colorScheme.contentColor)
                colorScheme = AvatarColorScheme(initialsTop, initialsBottom, botTop, botBottom, savedTop, savedBottom, content)
            } finally {
                ta.recycle()
            }
        }
        updateAppearance()
    }

    fun configure(
        type: AvatarViewType = AvatarViewType.IMAGE,
        size: AvatarSize = AvatarSize.SIZE_48,
        text: String = "",
        image: Bitmap? = null,
        colorScheme: AvatarColorScheme = AvatarColorScheme.DEFAULT
    ) {
        currentType = type
        currentSize = size
        initialsText = text
        imageBitmap = image
        this.colorScheme = colorScheme
        updateAppearance()
    }

    private fun updateAppearance() {
        val sizePx = dpToPx(currentSize.dp)
        layoutParams = layoutParams?.apply {
            width = sizePx
            height = sizePx
        } ?: LayoutParams(sizePx, sizePx)

        val ovalBg = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(0, 0))
        ovalBg.shape = GradientDrawable.OVAL
        background = ovalBg

        avatarImage.visibility = View.GONE
        avatarInitials.visibility = View.GONE
        avatarIcon.visibility = View.GONE

        when (currentType) {
            AvatarViewType.IMAGE -> setupImage(ovalBg)
            AvatarViewType.INITIALS -> setupInitials(ovalBg)
            AvatarViewType.BOT -> setupBot(ovalBg)
            AvatarViewType.SAVED -> setupSaved(ovalBg)
        }
    }

    private fun setupImage(bg: GradientDrawable) {
        bg.colors = intArrayOf(0x00000000, 0x00000000)
        avatarImage.visibility = View.VISIBLE
        imageBitmap?.let { avatarImage.setImageBitmap(it) }
    }

    private fun setupInitials(bg: GradientDrawable) {
        bg.colors = intArrayOf(colorScheme.initialsGradientTop, colorScheme.initialsGradientBottom)
        avatarInitials.visibility = View.VISIBLE
        avatarInitials.text = initialsText.uppercase()
        avatarInitials.setTextColor(colorScheme.contentColor)
        applyInitialsTypography()
    }

    private fun setupBot(bg: GradientDrawable) {
        bg.colors = intArrayOf(colorScheme.botGradientTop, colorScheme.botGradientBottom)
        avatarIcon.visibility = View.VISIBLE

        val iconSizeDp = currentSize.dp * 0.5f
        val iconDrawable = DSIcon.named(context, "bot", iconSizeDp)
        avatarIcon.setImageDrawable(iconDrawable)
        avatarIcon.setColorFilter(colorScheme.contentColor, android.graphics.PorterDuff.Mode.SRC_IN)

        val iconSizePx = dpToPx(iconSizeDp.toInt())
        avatarIcon.layoutParams = LayoutParams(iconSizePx, iconSizePx, Gravity.CENTER)
    }

    private fun setupSaved(bg: GradientDrawable) {
        bg.colors = intArrayOf(colorScheme.savedGradientTop, colorScheme.savedGradientBottom)
        avatarIcon.visibility = View.VISIBLE

        val iconSizeDp = when {
            currentSize.dp >= 120 -> 32
            currentSize.dp >= 40 -> 24
            else -> 16
        }
        val iconDrawable = DSIcon.named(context, "bookmark", iconSizeDp.toFloat())
        avatarIcon.setImageDrawable(iconDrawable)
        avatarIcon.setColorFilter(colorScheme.contentColor, android.graphics.PorterDuff.Mode.SRC_IN)

        val iconSizePx = dpToPx(iconSizeDp)
        avatarIcon.layoutParams = LayoutParams(iconSizePx, iconSizePx, Gravity.CENTER)
    }

    private fun applyInitialsTypography() {
        val robotoTypeface = try {
            ResourcesCompat.getFont(context, R.font.roboto)
        } catch (_: Exception) {
            Typeface.DEFAULT
        }

        when {
            currentSize.dp >= 120 -> {
                DSTypography.title1B.apply(avatarInitials)
            }
            currentSize.dp == 80 -> {
                DSTypography.title3B.apply(avatarInitials)
            }
            currentSize.dp in 48..56 -> {
                DSTypography.title5B.apply(avatarInitials)
            }
            currentSize.dp == 40 -> {
                DSTypography.body2B.apply(avatarInitials)
            }
            currentSize.dp == 36 -> {
                DSTypography.body3M.apply(avatarInitials)
            }
            currentSize.dp == 32 -> {
                DSTypography.caption1B.apply(avatarInitials)
            }
            currentSize.dp == 24 -> {
                avatarInitials.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
                avatarInitials.typeface = Typeface.create(robotoTypeface, 500, false)
            }
            currentSize.dp == 20 -> {
                avatarInitials.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8f)
                avatarInitials.typeface = Typeface.create(robotoTypeface, 500, false)
            }
            currentSize.dp == 18 -> {
                avatarInitials.setTextSize(TypedValue.COMPLEX_UNIT_SP, 7f)
                avatarInitials.typeface = Typeface.create(robotoTypeface, 500, false)
            }
            currentSize.dp == 16 -> {
                avatarInitials.setTextSize(TypedValue.COMPLEX_UNIT_SP, 6f)
                avatarInitials.typeface = Typeface.create(robotoTypeface, 500, false)
            }
        }
        avatarInitials.setTextColor(colorScheme.contentColor)
    }

    private fun dpToPx(dp: Int): Int = (dp * density).toInt()
}
