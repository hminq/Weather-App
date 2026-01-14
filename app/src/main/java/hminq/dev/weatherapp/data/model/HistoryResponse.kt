package hminq.dev.weatherapp.data.model

import com.squareup.moshi.Json

/**
 * Response from History API endpoint
 * Only includes fields needed for "This Week" display
 */
data class HistoryResponse(
    val location: LocationDto,
    val forecast: HistoryForecastDto
)

data class HistoryForecastDto(
    @Json(name = "forecastday")
    val forecastDay: List<HistoryDayDto>
)

data class HistoryDayDto(
    @Json(name = "date_epoch")
    val dateEpoch: Long,
    val day: DaySummaryDto
)
