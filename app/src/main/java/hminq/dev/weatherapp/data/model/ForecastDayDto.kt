package hminq.dev.weatherapp.data.model

import com.squareup.moshi.Json

data class ForecastDayDto(
    @Json(name = "date_epoch")
    val dateEpoch: Long,
    val day: DaySummaryDto,
    @Json(name = "hour")
    val hourly: List<HourlyWeatherDto>
)

/**
 * Daily weather summary - only fields needed for "This Week" UI
 */
data class DaySummaryDto(
    @Json(name = "avgtemp_c")
    val avgTempC: Double,
    @Json(name = "avgtemp_f")
    val avgTempF: Double,
    val condition: ConditionDto
)
