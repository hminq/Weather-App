package hminq.dev.weatherapp.presentation.weather

import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import hminq.dev.weatherapp.R

/**
 * ItemDecoration for ForecastAdapter
 * - For 4 items or less: Evenly distributes items across RecyclerView width
 * - For more than 4 items: Uses fixed spacing between items (no start/end margins)
 * Note: RecyclerView is already inside ConstraintLayout with paddingHorizontal, so items don't need extra start/end margins
 */
class ForecastItemDecoration(
    private val itemSpacing: Int,
    private val itemWidthDp: Int = 84 // Default from dimens.xml hourly_item_size
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = state.itemCount

        if (itemCount <= 0) return

        // For 4 items or less: evenly distribute across RecyclerView width
        if (itemCount <= 4) {
            // RecyclerView width already accounts for parent padding, so use full width
            val totalWidth = parent.width - parent.paddingStart - parent.paddingEnd
            val density = view.context.resources.displayMetrics.density
            val itemWidthPx = (itemWidthDp * density).toInt()
            val totalItemWidth = itemWidthPx * itemCount
            val availableSpace = totalWidth - totalItemWidth
            val spacingBetweenItems = if (itemCount > 1) availableSpace / (itemCount - 1) else 0

            // Distribute spacing evenly - each gap between items should be equal
            if (position == 0) {
                // First item: no start margin, half spacing on right
                outRect.left = 0
                outRect.right = spacingBetweenItems / 2
            } else if (position == itemCount - 1) {
                // Last item: half spacing on left, no end margin
                outRect.left = spacingBetweenItems / 2
                outRect.right = 0
            } else {
                // Middle items: half spacing on both sides (creates equal gaps)
                outRect.left = spacingBetweenItems / 2
                outRect.right = spacingBetweenItems / 2
            }
        } else {
            // For more than 4 items: fixed spacing between items
            // First item: no start margin
            // Last item: no end margin (right = 0)
            // Middle items: spacing on left only
            outRect.left = if (position == 0) 0 else itemSpacing
            outRect.right = 0 // All items have no end margin for This Week
        }
    }
}
