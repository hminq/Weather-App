package hminq.dev.weatherapp.domain.entity

import hminq.dev.weatherapp.domain.entity.enum.Condition

/**
 * Domain model for forecast display items
 * Used for both "Tomorrow" (hourly) and "This Week" (daily)
 */
data class ForecastItem(
    val label: String,      // "10 am" or "Mon", "Tue"
    val condition: Condition,
    val tempC: Double,
    val tempF: Double,
    val isToday: Boolean = false
)
