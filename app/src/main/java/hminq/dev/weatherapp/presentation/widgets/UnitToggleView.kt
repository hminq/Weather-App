package hminq.dev.weatherapp.presentation.widgets

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.TextView
import com.google.android.material.color.MaterialColors
import hminq.dev.weatherapp.R

class UnitToggleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val leftOption: TextView
    private val rightOption: TextView
    private val selectedIndicator: View

    private var isLeftSelected = true
    private var onToggleListener: ((Boolean) -> Unit)? = null

    // Theme colors
    private val colorPrimary: Int
    private val colorOnPrimary: Int
    private val colorOnSurfaceVariant: Int

    init {
        // Get theme colors - use android.R.attr for colorPrimary
        colorPrimary = MaterialColors.getColor(this, android.R.attr.colorPrimary)
        colorOnPrimary = MaterialColors.getColor(this, com.google.android.material.R.attr.colorOnPrimary)
        colorOnSurfaceVariant = MaterialColors.getColor(this, com.google.android.material.R.attr.colorOnSurfaceVariant)

        // Setup container
        setBackgroundResource(R.drawable.bg_toggle_container)
        val paddingPx = 4.dpToPx()
        setPadding(paddingPx, paddingPx, paddingPx, paddingPx)

        // Create selected indicator
        selectedIndicator = View(context).apply {
            setBackgroundResource(R.drawable.bg_toggle_selected)
        }
        addView(selectedIndicator)

        // Create left option
        leftOption = createOptionTextView().apply {
            gravity = Gravity.CENTER
        }
        addView(leftOption)

        // Create right option
        rightOption = createOptionTextView().apply {
            gravity = Gravity.CENTER
        }
        addView(rightOption)

        // Set click listeners
        leftOption.setOnClickListener { selectLeft() }
        rightOption.setOnClickListener { selectRight() }

        updateColors()
    }

    private fun createOptionTextView(): TextView {
        return TextView(context).apply {
            textSize = 13f
            setPadding(16.dpToPx(), 8.dpToPx(), 16.dpToPx(), 8.dpToPx())
            isClickable = true
            isFocusable = true
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Measure children first
        measureChild(leftOption, widthMeasureSpec, heightMeasureSpec)
        measureChild(rightOption, widthMeasureSpec, heightMeasureSpec)

        val optionWidth = maxOf(leftOption.measuredWidth, rightOption.measuredWidth)
        val optionHeight = maxOf(leftOption.measuredHeight, rightOption.measuredHeight)

        val totalWidth = optionWidth * 2 + paddingLeft + paddingRight
        val totalHeight = optionHeight + paddingTop + paddingBottom

        setMeasuredDimension(totalWidth, totalHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val optionWidth = (measuredWidth - paddingLeft - paddingRight) / 2
        val optionHeight = measuredHeight - paddingTop - paddingBottom

        // Layout left option
        leftOption.layout(
            paddingLeft,
            paddingTop,
            paddingLeft + optionWidth,
            paddingTop + optionHeight
        )

        // Layout right option
        rightOption.layout(
            paddingLeft + optionWidth,
            paddingTop,
            paddingLeft + optionWidth * 2,
            paddingTop + optionHeight
        )

        // Layout selected indicator
        val indicatorLeft = if (isLeftSelected) paddingLeft else paddingLeft + optionWidth
        selectedIndicator.layout(
            indicatorLeft,
            paddingTop,
            indicatorLeft + optionWidth,
            paddingTop + optionHeight
        )
    }

    fun setOptions(leftText: String, rightText: String) {
        leftOption.text = leftText
        rightOption.text = rightText
        requestLayout()
    }

    fun setSelectedPosition(isLeft: Boolean, animate: Boolean = false) {
        if (isLeftSelected == isLeft) return
        isLeftSelected = isLeft

        if (animate) {
            animateIndicator()
        } else {
            requestLayout()
        }
        updateColors()
    }

    fun setOnToggleListener(listener: (Boolean) -> Unit) {
        onToggleListener = listener
    }

    private fun selectLeft() {
        if (!isLeftSelected) {
            isLeftSelected = true
            animateIndicator()
            updateColors()
            onToggleListener?.invoke(true)
        }
    }

    private fun selectRight() {
        if (isLeftSelected) {
            isLeftSelected = false
            animateIndicator()
            updateColors()
            onToggleListener?.invoke(false)
        }
    }

    private fun animateIndicator() {
        val optionWidth = (measuredWidth - paddingLeft - paddingRight) / 2
        val startX = selectedIndicator.left
        val endX = if (isLeftSelected) paddingLeft else paddingLeft + optionWidth

        ValueAnimator.ofInt(startX, endX).apply {
            duration = 200
            interpolator = DecelerateInterpolator()
            addUpdateListener { animator ->
                val value = animator.animatedValue as Int
                selectedIndicator.layout(
                    value,
                    selectedIndicator.top,
                    value + optionWidth,
                    selectedIndicator.bottom
                )
            }
            start()
        }
    }

    private fun updateColors() {
        leftOption.setTextColor(if (isLeftSelected) colorOnPrimary else colorOnSurfaceVariant)
        rightOption.setTextColor(if (!isLeftSelected) colorOnPrimary else colorOnSurfaceVariant)
    }

    private fun Int.dpToPx(): Int = (this * context.resources.displayMetrics.density).toInt()
}
