package com.example.components.attachedmedia

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.PaintDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.components.R
import com.example.components.designsystem.DSIcon
import com.example.components.designsystem.DSTypography

class AttachedMediaView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    enum class MediaType { FILE, MEDIA }
    enum class FileType { FILE, AUDIO, IMAGE, VIDEO }

    var onClose: (() -> Unit)? = null

    private var currentType: MediaType = MediaType.FILE
    private var currentFileType: FileType = FileType.FILE
    private var isError: Boolean = false
    private var colorScheme: AttachedMediaColorScheme = AttachedMediaColorScheme.DEFAULT

    private val density = context.resources.displayMetrics.density

    // File mode views
    private val fileContainer: LinearLayout
    private val filePreviewContainer: FrameLayout
    private val filePreviewIcon: ImageView
    private val filePreviewImage: ImageView
    private val fileVideoOverlay: View
    private val fileVideoPlayIcon: ImageView
    private val fileNameText: TextView
    private val fileSizeText: TextView

    // Media mode views
    private val mediaImage: ImageView
    private val mediaErrorGradient: View
    private val mediaBadge: LinearLayout
    private val badgePlayIcon: ImageView
    private val badgeDurationText: TextView
    private val mediaErrorText: TextView

    // Shared
    private val closeButton: ImageView

    init {
        LayoutInflater.from(context).inflate(R.layout.view_attached_media, this, true)

        fileContainer = findViewById(R.id.fileContainer)
        filePreviewContainer = findViewById(R.id.filePreviewContainer)
        filePreviewIcon = findViewById(R.id.filePreviewIcon)
        filePreviewImage = findViewById(R.id.filePreviewImage)
        fileVideoOverlay = findViewById(R.id.fileVideoOverlay)
        fileVideoPlayIcon = findViewById(R.id.fileVideoPlayIcon)
        fileNameText = findViewById(R.id.fileNameText)
        fileSizeText = findViewById(R.id.fileSizeText)

        mediaImage = findViewById(R.id.mediaImage)
        mediaErrorGradient = findViewById(R.id.mediaErrorGradient)
        mediaBadge = findViewById(R.id.mediaBadge)
        badgePlayIcon = findViewById(R.id.badgePlayIcon)
        badgeDurationText = findViewById(R.id.badgeDurationText)
        mediaErrorText = findViewById(R.id.mediaErrorText)

        closeButton = findViewById(R.id.closeButton)
        closeButton.setOnClickListener { onClose?.invoke() }

        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.AttachedMediaView)
            try {
                val typeInt = ta.getInt(R.styleable.AttachedMediaView_attachedMediaType, 0)
                currentType = MediaType.entries[typeInt]
                val fileTypeInt = ta.getInt(R.styleable.AttachedMediaView_attachedFileType, 0)
                currentFileType = FileType.entries[fileTypeInt]
                val fileName = ta.getString(R.styleable.AttachedMediaView_attachedFileName) ?: ""
                val fileSize = ta.getString(R.styleable.AttachedMediaView_attachedFileSize) ?: ""
                val errorText = ta.getString(R.styleable.AttachedMediaView_attachedErrorText) ?: ""
                isError = ta.getBoolean(R.styleable.AttachedMediaView_attachedIsError, false)
                badgeDurationText.text = ta.getString(R.styleable.AttachedMediaView_attachedMediaDuration) ?: ""
                mediaBadge.visibility = if (ta.getBoolean(R.styleable.AttachedMediaView_attachedShowBadge, false)) VISIBLE else GONE

                colorScheme = AttachedMediaColorScheme(
                    backgroundFile = ta.getColor(R.styleable.AttachedMediaView_attachedBackgroundFile, colorScheme.backgroundFile),
                    backgroundFileError = ta.getColor(R.styleable.AttachedMediaView_attachedBackgroundFileError, colorScheme.backgroundFileError),
                    filePreviewBg = ta.getColor(R.styleable.AttachedMediaView_attachedFilePreviewBg, colorScheme.filePreviewBg),
                    fileIconTint = ta.getColor(R.styleable.AttachedMediaView_attachedFileIconTint, colorScheme.fileIconTint),
                    fileIconErrorTint = ta.getColor(R.styleable.AttachedMediaView_attachedFileIconErrorTint, colorScheme.fileIconErrorTint),
                    fileNameColor = ta.getColor(R.styleable.AttachedMediaView_attachedFileNameColor, colorScheme.fileNameColor),
                    fileSizeColor = ta.getColor(R.styleable.AttachedMediaView_attachedFileSizeColor, colorScheme.fileSizeColor),
                    fileNameErrorColor = ta.getColor(R.styleable.AttachedMediaView_attachedFileNameErrorColor, colorScheme.fileNameErrorColor),
                    fileSubErrorColor = ta.getColor(R.styleable.AttachedMediaView_attachedFileSubErrorColor, colorScheme.fileSubErrorColor),
                    mediaErrorTextColor = ta.getColor(R.styleable.AttachedMediaView_attachedMediaErrorTextColor, colorScheme.mediaErrorTextColor),
                    closeButtonBg = ta.getColor(R.styleable.AttachedMediaView_attachedCloseButtonBg, colorScheme.closeButtonBg),
                    closeIconTint = ta.getColor(R.styleable.AttachedMediaView_attachedCloseIconTint, colorScheme.closeIconTint),
                    badgeBg = ta.getColor(R.styleable.AttachedMediaView_attachedBadgeBg, colorScheme.badgeBg),
                    badgeTextColor = ta.getColor(R.styleable.AttachedMediaView_attachedBadgeTextColor, colorScheme.badgeTextColor),
                    dangerColor = ta.getColor(R.styleable.AttachedMediaView_attachedDangerColor, colorScheme.dangerColor),
                    videoPlayBg = ta.getColor(R.styleable.AttachedMediaView_attachedVideoPlayBg, colorScheme.videoPlayBg),
                    videoPlayIconTint = ta.getColor(R.styleable.AttachedMediaView_attachedVideoPlayIconTint, colorScheme.videoPlayIconTint)
                )

                fileNameText.text = fileName
                fileSizeText.text = if (isError) errorText else fileSize
            } finally {
                ta.recycle()
            }
        }

        updateAppearance()
    }

    fun configure(
        type: MediaType = MediaType.FILE,
        fileType: FileType = FileType.FILE,
        fileName: String = "",
        fileSize: String = "",
        errorText: String = "",
        isError: Boolean = false,
        thumbnailImage: Bitmap? = null,
        mediaDuration: String = "",
        showBadge: Boolean = false,
        colorScheme: AttachedMediaColorScheme = AttachedMediaColorScheme.DEFAULT
    ) {
        currentType = type
        currentFileType = fileType
        this.isError = isError
        this.colorScheme = colorScheme

        fileNameText.text = fileName
        fileSizeText.text = if (isError) errorText else fileSize
        mediaErrorText.text = errorText
        badgeDurationText.text = mediaDuration

        if (thumbnailImage != null) {
            when (type) {
                MediaType.MEDIA -> mediaImage.setImageBitmap(thumbnailImage)
                MediaType.FILE -> {
                    if (fileType == FileType.IMAGE || fileType == FileType.VIDEO) {
                        filePreviewImage.setImageBitmap(thumbnailImage)
                    }
                }
            }
        }

        mediaBadge.visibility = if (type == MediaType.MEDIA && showBadge && !isError) VISIBLE else GONE

        updateAppearance()
    }

    private fun updateAppearance() {
        when (currentType) {
            MediaType.FILE -> setupFileMode()
            MediaType.MEDIA -> setupMediaMode()
        }
        setupCloseButton()
    }

    private fun setupFileMode() {
        fileContainer.visibility = VISIBLE
        mediaImage.visibility = GONE
        mediaErrorGradient.visibility = GONE
        mediaErrorText.visibility = GONE
        mediaBadge.visibility = GONE

        // Reset to wrap_content for file mode (fileContainer has its own 216x56dp)
        layoutParams = layoutParams?.apply {
            width = LayoutParams.WRAP_CONTENT
            height = LayoutParams.WRAP_CONTENT
        } ?: LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        background = null
        clipToOutline = false

        // File card background
        val cardBg = GradientDrawable()
        cardBg.cornerRadius = dpToPx(16).toFloat()
        cardBg.setColor(if (isError) colorScheme.backgroundFileError else colorScheme.backgroundFile)
        fileContainer.background = cardBg

        // Preview container background
        val previewBg = GradientDrawable()
        previewBg.cornerRadius = dpToPx(8).toFloat()

        when {
            currentFileType == FileType.IMAGE || currentFileType == FileType.VIDEO -> {
                // Thumbnail preview — shown in both normal and error states
                if (isError) {
                    previewBg.setColor(Color.parseColor("#33FFFFFF"))
                }
                filePreviewContainer.background = previewBg
                filePreviewImage.visibility = VISIBLE
                filePreviewIcon.visibility = GONE
                filePreviewImage.clipToOutline = true
                filePreviewImage.outlineProvider = RoundedOutlineProvider(dpToPx(8).toFloat())

                if (currentFileType == FileType.VIDEO) {
                    setupVideoOverlay()
                } else {
                    fileVideoOverlay.visibility = GONE
                    fileVideoPlayIcon.visibility = GONE
                }
            }
            isError -> {
                // Error state for FILE/AUDIO — icon preview
                previewBg.setColor(Color.parseColor("#33FFFFFF"))
                filePreviewContainer.background = previewBg
                setupFileIcon()
            }
            else -> {
                previewBg.setColor(colorScheme.filePreviewBg)
                filePreviewContainer.background = previewBg
                filePreviewImage.visibility = GONE
                fileVideoOverlay.visibility = GONE
                fileVideoPlayIcon.visibility = GONE
                setupFileIcon()
            }
        }

        // Text colors
        if (isError) {
            fileNameText.setTextColor(colorScheme.fileNameErrorColor)
            fileSizeText.setTextColor(colorScheme.fileSubErrorColor)
        } else {
            fileNameText.setTextColor(colorScheme.fileNameColor)
            fileSizeText.setTextColor(colorScheme.fileSizeColor)
        }

        // Typography
        DSTypography.bubbleM13.apply(fileNameText)
        DSTypography.caption2R.apply(fileSizeText)

        // Re-apply text colors after typography (apply() doesn't set color)
        if (isError) {
            fileNameText.setTextColor(colorScheme.fileNameErrorColor)
            fileSizeText.setTextColor(colorScheme.fileSubErrorColor)
        } else {
            fileNameText.setTextColor(colorScheme.fileNameColor)
            fileSizeText.setTextColor(colorScheme.fileSizeColor)
        }
    }

    private fun setupFileIcon() {
        filePreviewImage.visibility = GONE
        filePreviewIcon.visibility = VISIBLE
        fileVideoOverlay.visibility = GONE
        fileVideoPlayIcon.visibility = GONE

        val iconName = when (currentFileType) {
            FileType.FILE -> "document-24"
            FileType.AUDIO -> "sound"
            FileType.IMAGE -> "document-24"
            FileType.VIDEO -> "document-24"
        }

        val icon = DSIcon.named(context, iconName, 24f)
        filePreviewIcon.setImageDrawable(icon)
        filePreviewIcon.setColorFilter(if (isError) colorScheme.fileIconErrorTint else colorScheme.fileIconTint, android.graphics.PorterDuff.Mode.SRC_IN)
    }

    private fun setupVideoOverlay() {
        fileVideoOverlay.visibility = VISIBLE
        fileVideoPlayIcon.visibility = VISIBLE

        // 24dp circle with basicColor60 background
        val overlaySize = dpToPx(24)
        fileVideoOverlay.layoutParams = (fileVideoOverlay.layoutParams as? LayoutParams)?.apply {
            width = overlaySize
            height = overlaySize
            gravity = android.view.Gravity.CENTER
        } ?: LayoutParams(overlaySize, overlaySize).apply {
            gravity = android.view.Gravity.CENTER
        }
        val overlayBg = GradientDrawable()
        overlayBg.shape = GradientDrawable.OVAL
        overlayBg.setColor(colorScheme.videoPlayBg)
        fileVideoOverlay.background = overlayBg

        // 16dp play icon with white70
        val playIcon = DSIcon.named(context, "play", 16f)
        fileVideoPlayIcon.layoutParams = (fileVideoPlayIcon.layoutParams as? LayoutParams)?.apply {
            width = dpToPx(16)
            height = dpToPx(16)
            gravity = android.view.Gravity.CENTER
        } ?: LayoutParams(dpToPx(16), dpToPx(16)).apply {
            gravity = android.view.Gravity.CENTER
        }
        fileVideoPlayIcon.setImageDrawable(playIcon)
        fileVideoPlayIcon.setColorFilter(colorScheme.videoPlayIconTint, android.graphics.PorterDuff.Mode.SRC_IN)
    }

    private fun setupMediaMode() {
        fileContainer.visibility = GONE
        mediaImage.visibility = VISIBLE

        // Set explicit 80x80dp size for media mode
        val size80 = dpToPx(80)
        layoutParams = layoutParams?.apply {
            width = size80
            height = size80
        } ?: LayoutParams(size80, size80)

        // Clip media image to rounded corners
        val mediaBg = GradientDrawable()
        mediaBg.cornerRadius = dpToPx(16).toFloat()
        mediaBg.setColor(Color.DKGRAY)
        background = mediaBg
        clipToOutline = true
        outlineProvider = RoundedOutlineProvider(dpToPx(16).toFloat())

        if (isError) {
            // Error gradient overlay
            mediaErrorGradient.visibility = VISIBLE
            val gradient = PaintDrawable()
            gradient.shape = RectShape()
            gradient.shaderFactory = object : ShapeDrawable.ShaderFactory() {
                override fun resize(width: Int, height: Int): Shader {
                    return LinearGradient(
                        0f, 0f, 0f, height.toFloat(),
                        intArrayOf(Color.TRANSPARENT, colorScheme.dangerColor),
                        floatArrayOf(0f, 1f),
                        Shader.TileMode.CLAMP
                    )
                }
            }
            mediaErrorGradient.background = gradient

            mediaErrorText.visibility = VISIBLE
            DSTypography.caption2R.apply(mediaErrorText)
            mediaErrorText.setTextColor(colorScheme.mediaErrorTextColor)

            mediaBadge.visibility = GONE
        } else {
            mediaErrorGradient.visibility = GONE
            mediaErrorText.visibility = GONE
        }

        // Badge setup
        if (mediaBadge.visibility == VISIBLE) {
            val badgeBgDrawable = GradientDrawable()
            badgeBgDrawable.cornerRadius = dpToPx(12).toFloat()
            badgeBgDrawable.setColor(colorScheme.badgeBg)
            mediaBadge.background = badgeBgDrawable

            val playIcon = DSIcon.named(context, "play", 16f)
            badgePlayIcon.setImageDrawable(playIcon)
            badgePlayIcon.setColorFilter(colorScheme.badgeTextColor, android.graphics.PorterDuff.Mode.SRC_IN)

            DSTypography.subcaptionR.apply(badgeDurationText)
            badgeDurationText.setTextColor(colorScheme.badgeTextColor)
        }
    }

    private fun setupCloseButton() {
        val closeBg = GradientDrawable()
        closeBg.shape = GradientDrawable.OVAL
        closeBg.setColor(colorScheme.closeButtonBg)
        closeButton.background = closeBg

        val closeIcon = DSIcon.named(context, "close-s", 24f)
        closeButton.setImageDrawable(closeIcon)
        closeButton.setColorFilter(colorScheme.closeIconTint, android.graphics.PorterDuff.Mode.SRC_IN)
    }

    private fun dpToPx(dp: Int): Int = (dp * density).toInt()

    private class RoundedOutlineProvider(private val radius: Float) : android.view.ViewOutlineProvider() {
        override fun getOutline(view: View, outline: android.graphics.Outline) {
            outline.setRoundRect(0, 0, view.width, view.height, radius)
        }
    }
}
