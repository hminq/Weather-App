package hminq.dev.weatherapp.domain.entity

/**
 * Container for forecast data with location timezone
 */
data class ForecastData(
    val timeZoneId: String,
    val days: List<DayForecastWithHours>
)

data class DayForecastWithHours(
    val day: DayForecast,
    val hours: List<HourForecast>
)
