package com.teymoorianar.anar_circular_avatar_view
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.widget.LinearLayout
import kotlin.math.min
import kotlin.random.Random

class AnarStackedAvatarsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    // Attribute variables
    private var maxNumber: Int = 3
    private var size: Int = 0
    private var backwardCoverage: Int = 0
    private var blankSpace: Int = 0
    private var defaultBackgroundImage: Int = 0
    private var randomBackgroundColors: Boolean = false
    private val randomBgColors = mutableListOf<Int>()

    // List to hold avatar views
    private val avatarList = mutableListOf<AnarCircularAvatarView>()

    init {
        // Set horizontal orientation for stacking avatars
        orientation = HORIZONTAL

        // Extract attributes from XML
        val a = context.obtainStyledAttributes(attrs, R.styleable.AnarStackedAvatarsView, defStyleAttr, 0)
        try {
            maxNumber = a.getInteger(R.styleable.AnarStackedAvatarsView_maxNumber, 3)
            size = a.getDimensionPixelSize(R.styleable.AnarStackedAvatarsView_size, 0)
            backwardCoverage = a.getDimensionPixelSize(R.styleable.AnarStackedAvatarsView_backwardCoverage, 0)
            blankSpace = a.getDimensionPixelSize(R.styleable.AnarStackedAvatarsView_blankSpace, 0)
            defaultBackgroundImage = a.getResourceId(R.styleable.AnarStackedAvatarsView_defaultBackgroundImage, 0)
            randomBackgroundColors = a.getBoolean(R.styleable.AnarStackedAvatarsView_randomBackgroundColors, false)
            randomBgColors.add(a.getColor(R.styleable.AnarStackedAvatarsView_randomBgColor1, Color.TRANSPARENT))
            randomBgColors.add(a.getColor(R.styleable.AnarStackedAvatarsView_randomBgColor2, Color.TRANSPARENT))
            randomBgColors.add(a.getColor(R.styleable.AnarStackedAvatarsView_randomBgColor3, Color.TRANSPARENT))
            randomBgColors.add(a.getColor(R.styleable.AnarStackedAvatarsView_randomBgColor4, Color.TRANSPARENT))
            randomBgColors.add(a.getColor(R.styleable.AnarStackedAvatarsView_randomBgColor5, Color.TRANSPARENT))
        } finally {
            a.recycle()
        }
    }

    /**
     * Sets the list of avatars to display.
     * @param avatars List of AvatarItem objects containing image data.
     */
    fun setAvatars(avatars: List<AvatarItem>) {
        removeAllViews() // Clear existing views
        avatarList.clear() // Clear the tracking list

        // Calculate how many avatars to display and handle extras
        val displayCount = min(maxNumber, avatars.size)
        val extraCount = avatars.size - maxNumber + 1

        // Add avatars to the view
        for (i in 0 until displayCount) {
            val avatarView = AnarCircularAvatarView(context).apply {
                // Set size and overlap using layout params
                layoutParams = LayoutParams(size, size).apply {
                    if (i > 0) {
                        setMargins(-backwardCoverage, 0, 0, 0) // Overlap except for the first avatar
                    }
                }
                // Use blankSpace as a partial stroke for separation
                setBorderWidth(blankSpace)
                setBorderColor(Color.TRANSPARENT) // Transparent border for partial curve effect
            }

            // Handle the last avatar when there are extra items
            if (i == displayCount - 1 && extraCount > 0) {
                setRandomOrDefaultBackground(avatarView)
                avatarView.setText("+$extraCount") // Show "+N" for remaining items
                avatarView.setTextColor(Color.BLUE)
                avatarView.setTextSize(80f)
                setRandomOrDefaultBackground(avatarView)
            } else {
                // Normal avatar rendering
                val avatar = avatars[i]
                if (avatar.imageUrl != null) {
                    avatarView.setImageUrl(avatar.imageUrl)
                } else if (avatar.imageResource != null) {
                    avatarView.setImageResource(avatar.imageResource)
                } else {
                    setRandomOrDefaultBackground(avatarView)
                }
            }

            avatarList.add(avatarView)
            addView(avatarView)
        }
    }

    /**
     * Sets a random background color or default background image for an avatar.
     * @param avatarView The avatar view to apply the background to.
     */
    private fun setRandomOrDefaultBackground(avatarView: AnarCircularAvatarView) {
        Log.d("BG SAG", "setRandomOrDefaultBackground: ...")
        if (randomBackgroundColors && randomBgColors.isNotEmpty()) {
            val validColors = randomBgColors.filter { it != Color.TRANSPARENT }
            if (validColors.isNotEmpty()) {
                val randomColor = validColors[Random.nextInt(validColors.size)]
                avatarView.setBackgroundColor(randomColor)
                Log.d("BG SAG", "setRandomOrDefaultBackground: set random color as background")
            } else if (defaultBackgroundImage != 0) {
                avatarView.setImageResource(defaultBackgroundImage)
                Log.d("BG SAG", "setRandomOrDefaultBackground: set default bg image")
            }
        } else if (defaultBackgroundImage != 0) {
            avatarView.setImageResource(defaultBackgroundImage)
            Log.d("BG SAG", "setRandomOrDefaultBackground: set default bg image 2")
        }
    }

    /**
     * Data class representing an avatar item.
     */
    data class AvatarItem(
        val imageUrl: String? = null,
        val imageResource: Int? = null
    )
}