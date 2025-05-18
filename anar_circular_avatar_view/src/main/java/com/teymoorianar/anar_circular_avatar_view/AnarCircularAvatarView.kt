package com.teymoorianar.anar_circular_avatar_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import kotlin.math.min

/**
 * A custom ImageView that displays an image or text in a circular shape with an optional border.
 * Supports loading images from a URL or resource, displaying text, and setting a background color.
 */
class AnarCircularAvatarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    // Properties for the border
    private var borderColor: Int = Color.TRANSPARENT
    private var borderWidth: Int = 0

    // Properties for text
    private var text: String? = null
    private var textColor: Int = Color.BLACK
    private var textSize: Float = 0f

    // Paint object for drawing the border
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    // Paint object for drawing the text
    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
    }

    init {
        // Obtain styled attributes
        val a = context.obtainStyledAttributes(attrs, R.styleable.AnarCircularAvatarView, defStyleAttr, 0)
        try {
            // Extract attributes
            val imageResource: Drawable? = a.getDrawable(R.styleable.AnarCircularAvatarView_imageResource)
            val url = a.getString(R.styleable.AnarCircularAvatarView_imageUrl)
            borderColor = a.getColor(R.styleable.AnarCircularAvatarView_borderColor, Color.TRANSPARENT)
            borderWidth = a.getDimensionPixelSize(R.styleable.AnarCircularAvatarView_borderWidth, 0)
            text = a.getString(R.styleable.AnarCircularAvatarView_text)
            textColor = a.getColor(R.styleable.AnarCircularAvatarView_textColor, Color.BLACK)
            textSize = a.getDimension(R.styleable.AnarCircularAvatarView_textSize, 0f)

            // Set scale type if provided
            val scaleTypeIndex = a.getInt(R.styleable.AnarCircularAvatarView_scaleType, -1)
            if (scaleTypeIndex != -1) {
                val scaleTypes = arrayOf(
                    ScaleType.CENTER,
                    ScaleType.CENTER_CROP,
                    ScaleType.CENTER_INSIDE,
                    ScaleType.FIT_CENTER,
                    ScaleType.FIT_END,
                    ScaleType.FIT_START,
                    ScaleType.FIT_XY,
                    ScaleType.MATRIX
                )
                scaleType = scaleTypes[scaleTypeIndex]
            }

            // Set up text rendering if text is provided
            if (!text.isNullOrEmpty()) {
                textPaint.color = textColor
                textPaint.textSize = textSize
                setImageDrawable(null) // Clear any image to show text
            } else {
                // Load image based on provided attributes if no text
                if (!url.isNullOrEmpty()) {
                    // Load from URL with imageResource as fallback if loading fails
                    Glide.with(this)
                        .load(url)
                        .apply {
                            if (imageResource != null) {
                                error(imageResource) // Fallback to imageResource if URL fails
                            }
                        }
                        .into(this)
                } else if (imageResource != null) {
                    // Load directly from imageResource if no URL is provided
                    setImageDrawable(imageResource)
                }
            }

            // Set up circular clipping for API 21+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                outlineProvider = object : ViewOutlineProvider() {
                    override fun getOutline(view: View, outline: Outline) {
                        outline.setOval(0, 0, view.width, view.height)
                    }
                }
                clipToOutline = true
            }
        } finally {
            a.recycle()
        }
    }

    // Ensure the view is square to maintain a circular shape
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size = min(measuredWidth, measuredHeight)
        setMeasuredDimension(size, size)
    }

    // Draw the border and text (if applicable) on top of the image
    override fun onDraw(canvas: Canvas) {
        // Draw the image (if any) first
        super.onDraw(canvas)

        // Draw text if provided
        if (!text.isNullOrEmpty()) {
            val x = width / 2f
            val y = (height / 2f) - ((textPaint.descent() + textPaint.ascent()) / 2)
            canvas.drawText(text!!, x, y, textPaint)
        }

        // Draw the border if specified
        if (borderWidth > 0) {
            val radius = (width - borderWidth) / 2f
            borderPaint.color = borderColor
            borderPaint.strokeWidth = borderWidth.toFloat()
            canvas.drawCircle(width / 2f, height / 2f, radius, borderPaint)
        }
    }

    /**
     * Sets the image URL to load, with the existing imageResource as fallback if loading fails.
     */
    fun setImageUrl(url: String?) {
        if (!url.isNullOrEmpty()) {
            text = null // Clear text to prioritize image
            Glide.with(this)
                .load(url)
                .apply {
                    val currentDrawable = drawable
                    if (currentDrawable != null) {
                        error(currentDrawable) // Use current drawable as fallback
                    }
                }
                .into(this)
        }
    }

    /**
     * Sets the image resource ID to display.
     */
    override fun setImageResource(resourceId: Int) {
        if (resourceId != 0) {
            text = null // Clear text to prioritize image
            setImageResource(resourceId) // Use AppCompatImageView's setImageResource
        }
    }

    /**
     * Sets the text to display, clearing any image.
     */
    fun setText(text: String?) {
        this.text = text
        if (!text.isNullOrEmpty()) {
            textPaint.color = textColor
            textPaint.textSize = textSize
            setImageDrawable(null) // Clear image to show text
        }
        invalidate() // Redraw to reflect changes
    }

    /**
     * Sets the text color.
     */
    fun setTextColor(color: Int) {
        textColor = color
        textPaint.color = color
        invalidate() // Redraw to reflect changes
    }

    /**
     * Sets the text size in pixels.
     */
    fun setTextSize(size: Float) {
        textSize = size
        textPaint.textSize = size
        invalidate() // Redraw to reflect changes
    }

    /**
     * Sets the border width in pixels.
     */
    fun setBorderWidth(width: Int) {
        borderWidth = width
        invalidate() // Redraw to reflect changes
    }

    /**
     * Sets the border color.
     */
    fun setBorderColor(color: Int) {
        borderColor = color
        borderPaint.color = color
        invalidate() // Redraw to reflect changes
    }

    /**
     * Sets the background color for the view.
     */
    override fun setBackgroundColor(color: Int) {
        super.setBackgroundColor(color)
    }
}