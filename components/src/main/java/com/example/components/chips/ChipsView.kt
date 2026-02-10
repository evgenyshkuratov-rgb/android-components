package com.example.components.chips

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.example.components.R

class ChipsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    enum class ChipState { DEFAULT, ACTIVE, AVATAR }
    enum class ChipSize(val heightDp: Int, val avatarDp: Int) {
        SMALL(32, 24),
        MEDIUM(40, 32)
    }

    var onTap: (() -> Unit)? = null
    var onClose: (() -> Unit)? = null

    private var currentState: ChipState = ChipState.DEFAULT
    private var currentSize: ChipSize = ChipSize.SMALL
    private var colorScheme: ChipsColorScheme = ChipsColorScheme.DEFAULT

    private val avatarImageView: ImageView
    private val iconImageView: ImageView
    private val textLabel: TextView
    private val closeButton: ImageView
    private val density = context.resources.displayMetrics.density

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL

        LayoutInflater.from(context).inflate(R.layout.view_chips, this, true)

        avatarImageView = findViewById(R.id.avatarImageView)
        iconImageView = findViewById(R.id.iconImageView)
        textLabel = findViewById(R.id.textLabel)
        closeButton = findViewById(R.id.closeButton)

        closeButton.setOnClickListener { onClose?.invoke() }
        setOnClickListener { onTap?.invoke() }

        if (attrs != null) {
            val ta: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.ChipsView)
            try {
                val stateInt = ta.getInt(R.styleable.ChipsView_chipState, 0)
                currentState = ChipState.entries[stateInt]
                val sizeInt = ta.getInt(R.styleable.ChipsView_chipSize, 0)
                currentSize = ChipSize.entries[sizeInt]
                val text = ta.getString(R.styleable.ChipsView_chipText)
                if (text != null) textLabel.text = text
                val iconDrawable = ta.getDrawable(R.styleable.ChipsView_chipIcon)
                if (iconDrawable != null) {
                    iconImageView.setImageDrawable(iconDrawable)
                    iconImageView.visibility = VISIBLE
                }
                val bgDefault = ta.getColor(R.styleable.ChipsView_chipsBackgroundDefault, colorScheme.backgroundDefault)
                val bgActive = ta.getColor(R.styleable.ChipsView_chipsBackgroundActive, colorScheme.backgroundActive)
                val textPrimary = ta.getColor(R.styleable.ChipsView_chipsTextPrimary, colorScheme.textPrimary)
                val closeTint = ta.getColor(R.styleable.ChipsView_chipsCloseIconTint, colorScheme.closeIconTint)
                colorScheme = ChipsColorScheme(bgDefault, bgActive, textPrimary, closeTint)
            } finally {
                ta.recycle()
            }
        }
        updateAppearance()
    }

    fun configure(
        text: String,
        icon: Drawable? = null,
        state: ChipState = ChipState.DEFAULT,
        size: ChipSize = ChipSize.SMALL,
        colorScheme: ChipsColorScheme = ChipsColorScheme.DEFAULT
    ) {
        currentState = state
        currentSize = size
        this.colorScheme = colorScheme
        textLabel.text = text
        iconImageView.setImageDrawable(icon)
        iconImageView.visibility = if (icon != null) VISIBLE else GONE
        avatarImageView.visibility = GONE
        closeButton.visibility = GONE
        updateAppearance()
    }

    fun configureAvatar(
        name: String,
        avatarImage: Bitmap? = null,
        closeIcon: Drawable? = null,
        size: ChipSize = ChipSize.SMALL,
        colorScheme: ChipsColorScheme = ChipsColorScheme.DEFAULT
    ) {
        currentState = ChipState.AVATAR
        currentSize = size
        this.colorScheme = colorScheme
        textLabel.text = name
        avatarImageView.setImageBitmap(avatarImage)
        avatarImageView.visibility = VISIBLE
        val avatarSizePx = dpToPx(size.avatarDp)
        avatarImageView.layoutParams = LayoutParams(avatarSizePx, avatarSizePx)
        avatarImageView.outlineProvider = android.view.ViewOutlineProvider.BACKGROUND
        avatarImageView.clipToOutline = true
        val avatarBg = GradientDrawable()
        avatarBg.shape = GradientDrawable.OVAL
        avatarImageView.background = avatarBg
        iconImageView.visibility = GONE
        closeButton.visibility = VISIBLE
        if (closeIcon != null) closeButton.setImageDrawable(closeIcon)
        updateAppearance()
    }

    private fun updateAppearance() {
        val heightPx = dpToPx(currentSize.heightDp)
        layoutParams = layoutParams?.also { it.height = heightPx } ?: LayoutParams(LayoutParams.WRAP_CONTENT, heightPx)

        val bgDrawable = GradientDrawable()
        bgDrawable.cornerRadius = heightPx / 2f

        val robotoTypeface = try { ResourcesCompat.getFont(context, R.font.roboto) } catch (e: Exception) { Typeface.DEFAULT }

        when (currentState) {
            ChipState.DEFAULT -> {
                bgDrawable.setColor(colorScheme.backgroundDefault)
                textLabel.setTextColor(colorScheme.textPrimary)
                textLabel.typeface = Typeface.create(robotoTypeface, 500, false)
                textLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                iconImageView.setColorFilter(colorScheme.textPrimary)
            }
            ChipState.ACTIVE -> {
                bgDrawable.setColor(colorScheme.backgroundActive)
                textLabel.setTextColor(colorScheme.textPrimary)
                textLabel.typeface = Typeface.create(robotoTypeface, 500, false)
                textLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                iconImageView.setColorFilter(colorScheme.textPrimary)
            }
            ChipState.AVATAR -> {
                bgDrawable.setColor(colorScheme.backgroundDefault)
                textLabel.setTextColor(colorScheme.textPrimary)
                textLabel.typeface = Typeface.create(robotoTypeface, 400, false)
                textLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                textLabel.letterSpacing = 0.25f / 14f
                closeButton.setColorFilter(colorScheme.closeIconTint)
            }
        }
        background = bgDrawable
        updatePadding()
    }

    private fun updatePadding() {
        when (currentState) {
            ChipState.DEFAULT, ChipState.ACTIVE -> {
                val leadPad = dpToPx(if (currentSize == ChipSize.SMALL) 8 else 12)
                val trailPad = dpToPx(12)
                setPadding(leadPad, 0, trailPad, 0)
                (iconImageView.layoutParams as? MarginLayoutParams)?.marginEnd = dpToPx(8)
                (textLabel.layoutParams as? MarginLayoutParams)?.marginEnd = 0
                closeButton.layoutParams = LayoutParams(dpToPx(36), dpToPx(36))
            }
            ChipState.AVATAR -> {
                val leadPad = dpToPx(4)
                setPadding(leadPad, 0, 0, 0)
                (avatarImageView.layoutParams as? MarginLayoutParams)?.marginEnd = dpToPx(8)
                (textLabel.layoutParams as? MarginLayoutParams)?.marginEnd = 0
                val closeSizePx = dpToPx(currentSize.heightDp) - dpToPx(8)
                closeButton.layoutParams = LayoutParams(closeSizePx, closeSizePx)
            }
        }
    }

    private fun dpToPx(dp: Int): Int = (dp * density).toInt()
}
