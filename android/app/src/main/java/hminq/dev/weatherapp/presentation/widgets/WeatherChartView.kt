package hminq.dev.weatherapp.presentation.widgets

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.color.MaterialColors
import hminq.dev.weatherapp.R
import hminq.dev.weatherapp.domain.entity.HourForecast
import hminq.dev.weatherapp.domain.entity.enum.Condition
import hminq.dev.weatherapp.domain.entity.enum.Temperature
import java.time.Instant
import java.time.ZoneId
import kotlin.math.abs

class WeatherChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    // Internal data class for chart
    private data class HourlyData(
        val hour: Int,
        val temperature: Int,
        val condition: Condition,
        val conditionIcon: Int
    )

    // Current hourly data (from API or default)
    private var hourlyData: List<HourlyData> = emptyList()

    // Temperature unit setting
    private var temperatureUnit: Temperature = Temperature.CELSIUS

    // Interpolated data (60 points per hour = 1440 total for smooth curve)
    private data class MinuteData(
        val hour: Int,
        val minute: Int,
        val temperature: Float,
        val condition: Condition,
        val conditionIcon: Int
    )

    // Calculated points on the curve
    private val points = mutableListOf<PointF>()
    private val minuteData = mutableListOf<MinuteData>()

    // Current selection
    private var selectedIndex: Int = 11 * 60

    // Dimensions
    private val chartPaddingTop = 60f.dpToPx()      // Space for tooltip above chart
    private val chartPaddingBottom = 20f.dpToPx()   // Space at bottom
    private val chartPaddingHorizontal = 50f.dpToPx()  // Padding for tooltip visibility at edges
    
    // Vertical scaling - controls how compressed the chart is vertically
    private val minValueBuffer = 0.5f   // Min temp at 50% from bottom (higher = more compressed)
    private val maxValueBuffer = 0.1f   // Max temp at 90% from bottom
    private val indicatorRadius = 8f.dpToPx()
    private val indicatorStrokeWidth = 3f.dpToPx()
    private val tooltipMargin = 12f.dpToPx()

    // Theme colors
    private val colorSurface: Int
    private val colorOnSurfaceVariant: Int

    // Paints
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val areaPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val indicatorFillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val indicatorStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Paths
    private val linePath = Path()
    private val areaPath = Path()

    // Chart drawing view
    private val chartView: View

    // Tooltip views
    private val tooltipView: View
    private val tvTime: TextView
    private val tvTemperature: TextView
    private val ivCondition: ImageView

    init {
        // Get theme colors
        colorSurface = MaterialColors.getColor(this, com.google.android.material.R.attr.colorSurface, Color.WHITE)
        colorOnSurfaceVariant = MaterialColors.getColor(this, com.google.android.material.R.attr.colorOnSurfaceVariant, Color.GRAY)

        // Disable clipping
        clipChildren = false
        clipToPadding = false

        // Create chart drawing view
        chartView = object : View(context) {
            override fun onDraw(canvas: Canvas) {
                super.onDraw(canvas)
                drawChart(canvas)
            }
        }
        addView(chartView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))

        // Inflate tooltip layout
        tooltipView = LayoutInflater.from(context).inflate(R.layout.item_hourly_temp, this, false)
        tvTime = tooltipView.findViewById(R.id.tvTime)
        tvTemperature = tooltipView.findViewById(R.id.tvTemperature)
        ivCondition = tooltipView.findViewById(R.id.ivCondition)
        addView(tooltipView)

        setupPaints()
    }

    private fun setupPaints() {
        linePaint.apply {
            style = Paint.Style.STROKE
            strokeWidth = 2f.dpToPx()
            color = colorOnSurfaceVariant
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }

        areaPaint.apply {
            style = Paint.Style.FILL
        }

        indicatorFillPaint.apply {
            style = Paint.Style.FILL
            color = colorSurface
        }

        indicatorStrokePaint.apply {
            style = Paint.Style.STROKE
            strokeWidth = indicatorStrokeWidth
            color = colorOnSurfaceVariant
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        calculatePoints()
        updateAreaGradient()
        updateTooltipPosition()
    }

    private fun calculatePoints() {
        points.clear()
        minuteData.clear()

        if (hourlyData.isEmpty() || width == 0 || height == 0) return

        // Generate minute-level data with smooth temperature interpolation
        hourlyData.forEachIndexed { hourIndex, data ->
            val currentTemp = data.temperature.toFloat()
            val nextTemp = if (hourIndex < hourlyData.size - 1) {
                hourlyData[hourIndex + 1].temperature.toFloat()
            } else {
                currentTemp
            }

            for (minute in 0 until 60) {
                val t = minute / 60f
                val interpolatedTemp = currentTemp + (nextTemp - currentTemp) * t

                minuteData.add(MinuteData(
                    hour = data.hour,
                    minute = minute,
                    temperature = interpolatedTemp,
                    condition = data.condition,
                    conditionIcon = data.conditionIcon
                ))
            }
        }

        val chartWidth = width - 2 * chartPaddingHorizontal
        val chartHeight = height - chartPaddingTop - chartPaddingBottom

        val minTemp = hourlyData.minOf { it.temperature }
        val maxTemp = hourlyData.maxOf { it.temperature }
        val tempRange = (maxTemp - minTemp).coerceAtLeast(1)

        minuteData.forEachIndexed { index, data ->
            val x = chartPaddingHorizontal + (index.toFloat() / (minuteData.size - 1)) * chartWidth
            val normalizedTemp = (data.temperature - minTemp) / tempRange
            // Compress vertical range: min at minValueBuffer, max at (1 - maxValueBuffer)
            val verticalRange = 1f - minValueBuffer - maxValueBuffer
            val adjustedNormalized = minValueBuffer + normalizedTemp * verticalRange
            val y = chartPaddingTop + chartHeight * (1 - adjustedNormalized)
            points.add(PointF(x, y))
        }

        updatePaths()
    }

    private fun updatePaths() {
        linePath.reset()
        areaPath.reset()

        if (points.size < 2) return

        // Line extends from left edge to first data point
        linePath.moveTo(0f, points[0].y)
        linePath.lineTo(points[0].x, points[0].y)
        
        // Area extends to full width for better visual
        areaPath.moveTo(0f, height.toFloat())  // Start at bottom-left corner
        areaPath.lineTo(0f, points[0].y)       // Up to first point's Y level
        areaPath.lineTo(points[0].x, points[0].y)  // To first point

        // Use Catmull-Rom spline for smooth curve
        // Tension factor (0.5 = Catmull-Rom, lower = smoother)
        val tension = 0.3f

        for (i in 0 until points.size - 1) {
            val p0 = if (i > 0) points[i - 1] else points[i]
            val p1 = points[i]
            val p2 = points[i + 1]
            val p3 = if (i < points.size - 2) points[i + 2] else points[i + 1]

            // Calculate control points using Catmull-Rom to Bezier conversion
            val cp1x = p1.x + (p2.x - p0.x) * tension
            val cp1y = p1.y + (p2.y - p0.y) * tension
            val cp2x = p2.x - (p3.x - p1.x) * tension
            val cp2y = p2.y - (p3.y - p1.y) * tension

            linePath.cubicTo(cp1x, cp1y, cp2x, cp2y, p2.x, p2.y)
            areaPath.cubicTo(cp1x, cp1y, cp2x, cp2y, p2.x, p2.y)
        }

        // Extend line to right edge
        linePath.lineTo(width.toFloat(), points.last().y)

        // Extend area to full width
        areaPath.lineTo(width.toFloat(), points.last().y)  // Extend to right edge at last point's Y
        areaPath.lineTo(width.toFloat(), height.toFloat())  // Down to bottom-right corner
        areaPath.close()
    }

    private fun updateAreaGradient() {
        // Extract RGB from theme color for gradient
        val red = Color.red(colorOnSurfaceVariant)
        val green = Color.green(colorOnSurfaceVariant)
        val blue = Color.blue(colorOnSurfaceVariant)
        
        val gradient = LinearGradient(
            0f, chartPaddingTop,
            0f, height.toFloat(),
            intArrayOf(
                Color.argb(50, red, green, blue),  // Top: 20% opacity
                Color.argb(0, red, green, blue)    // Bottom: transparent
            ),
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP
        )
        areaPaint.shader = gradient
    }

    private fun drawChart(canvas: Canvas) {
        if (points.isEmpty() || minuteData.isEmpty()) return

        // Draw area fill
        canvas.drawPath(areaPath, areaPaint)

        // Draw line
        canvas.drawPath(linePath, linePaint)

        // Draw indicator
        if (selectedIndex in points.indices) {
            val point = points[selectedIndex]
            canvas.drawCircle(point.x, point.y, indicatorRadius, indicatorStrokePaint)
            canvas.drawCircle(point.x, point.y, indicatorRadius - indicatorStrokeWidth / 2, indicatorFillPaint)
        }
    }

    private fun updateTooltipPosition() {
        if (points.isEmpty() || minuteData.isEmpty() || selectedIndex !in points.indices) {
            tooltipView.visibility = GONE
            return
        }

        tooltipView.visibility = VISIBLE
        val point = points[selectedIndex]
        val data = minuteData[selectedIndex]

        // Update tooltip content
        tvTime.text = formatTime(data.hour, data.minute)
        tvTemperature.text = "${data.temperature.toInt()}°"
        ivCondition.setImageResource(data.conditionIcon)

        // Measure tooltip
        tooltipView.measure(
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )
        val tooltipWidth = tooltipView.measuredWidth
        val tooltipHeight = tooltipView.measuredHeight

        // Position tooltip above indicator, centered
        var tooltipX = point.x - tooltipWidth / 2
        val tooltipY = point.y - tooltipMargin - tooltipHeight - indicatorRadius

        // Keep within bounds
        tooltipX = tooltipX.coerceIn(0f, (width - tooltipWidth).toFloat())

        tooltipView.translationX = tooltipX
        tooltipView.translationY = tooltipY
    }

    private fun formatTime(hour: Int, minute: Int): String {
        val minuteStr = minute.toString().padStart(2, '0')
        return when {
            hour == 0 -> "12:$minuteStr AM"
            hour < 12 -> "$hour:$minuteStr AM"
            hour == 12 -> "12:$minuteStr PM"
            else -> "${hour - 12}:$minuteStr PM"
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                val clampedX = event.x.coerceIn(0f, width.toFloat())
                val nearestIndex = findNearestPointIndex(clampedX)
                if (nearestIndex != selectedIndex && nearestIndex in points.indices) {
                    selectedIndex = nearestIndex
                    chartView.invalidate()
                    updateTooltipPosition()
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun findNearestPointIndex(touchX: Float): Int {
        if (points.isEmpty()) return 0

        val chartWidth = width.toFloat()
        val ratio = (touchX / chartWidth).coerceIn(0f, 1f)
        val estimatedIndex = (ratio * (points.size - 1)).toInt()

        var nearestIndex = estimatedIndex
        var minDistance = abs(points[estimatedIndex].x - touchX)

        for (offset in 1..60) {
            if (estimatedIndex - offset >= 0) {
                val dist = abs(points[estimatedIndex - offset].x - touchX)
                if (dist < minDistance) {
                    minDistance = dist
                    nearestIndex = estimatedIndex - offset
                }
            }
            if (estimatedIndex + offset < points.size) {
                val dist = abs(points[estimatedIndex + offset].x - touchX)
                if (dist < minDistance) {
                    minDistance = dist
                    nearestIndex = estimatedIndex + offset
                }
            }
        }

        return nearestIndex
    }

    fun setSelectedHour(hour: Int, minute: Int = 0) {
        val index = hour * 60 + minute.coerceIn(0, 59)
        if (index != selectedIndex) {
            selectedIndex = index
            if (minuteData.isNotEmpty()) {
                chartView.invalidate()
                updateTooltipPosition()
            }
        }
    }

    /**
     * Set hourly weather data from API
     * @param data List of HourForecast (should be 24 items for full day)
     * @param timeZoneId Location's timezone ID
     * @param unit Temperature unit preference
     */
    fun setData(
        data: List<HourForecast>,
        timeZoneId: String = "UTC",
        unit: Temperature = Temperature.CELSIUS
    ) {
        temperatureUnit = unit
        val zoneId = ZoneId.of(timeZoneId)

        // Convert HourForecast to internal HourlyData
        hourlyData = data.map { hourly ->
            val temp = if (unit == Temperature.CELSIUS) {
                hourly.tempC.toInt()
            } else {
                hourly.tempF.toInt()
            }

            // Extract hour using location's timezone
            val hour = Instant.ofEpochSecond(hourly.timeEpoch)
                .atZone(zoneId)
                .hour

            HourlyData(
                hour = hour,
                temperature = temp,
                condition = hourly.condition,
                conditionIcon = getConditionIcon(hourly.condition)
            )
        }

        // Recalculate if view is already laid out
        if (width > 0 && height > 0) {
            calculatePoints()
            updateAreaGradient()
            updateTooltipPosition()
            chartView.invalidate()
        }
    }

    /**
     * Set default/loading data for the chart (24 hours with placeholder values)
     * @param unit Temperature unit preference
     */
    fun setDefaultData(unit: Temperature = Temperature.CELSIUS) {
        temperatureUnit = unit
        val now = java.time.ZonedDateTime.now(ZoneId.of("UTC"))
        val defaultTemp = if (unit == Temperature.CELSIUS) 20 else 68 // 20°C = 68°F

        // Create 24 hours of default data
        hourlyData = (0..23).map { hourOffset ->
            val hour = (now.hour + hourOffset) % 24
            HourlyData(
                hour = hour,
                temperature = defaultTemp,
                condition = Condition.UNKNOWN,
                conditionIcon = getConditionIcon(Condition.UNKNOWN)
            )
        }

        // Recalculate if view is already laid out
        if (width > 0 && height > 0) {
            calculatePoints()
            updateAreaGradient()
            updateTooltipPosition()
            chartView.invalidate()
        }
    }

    private fun getConditionIcon(condition: Condition): Int {
        return when (condition) {
            Condition.CLEAR_DAY -> R.drawable.ic_clear_day
            Condition.CLEAR_NIGHT -> R.drawable.ic_clear_night
            Condition.CLOUDY -> R.drawable.ic_cloudy
            Condition.RAIN -> R.drawable.ic_rain
            Condition.SNOW -> R.drawable.ic_snow
            Condition.ICE -> R.drawable.ic_ice
            Condition.THUNDER -> R.drawable.ic_thunder
            Condition.FOG -> R.drawable.ic_fog
            Condition.UNKNOWN -> R.drawable.ic_cloudy
        }
    }

    private fun getConditionText(condition: Condition): String {
        return when (condition) {
            Condition.CLEAR_DAY -> "Clear"
            Condition.CLEAR_NIGHT -> "Clear"
            Condition.CLOUDY -> "Cloudy"
            Condition.RAIN -> "Rainy"
            Condition.SNOW -> "Snowy"
            Condition.ICE -> "Icy"
            Condition.THUNDER -> "Thunder"
            Condition.FOG -> "Foggy"
            Condition.UNKNOWN -> "Unknown"
        }
    }

    private fun Float.dpToPx(): Float = this * context.resources.displayMetrics.density
}
