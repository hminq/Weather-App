package hminq.dev.weatherapp.data.model

import com.squareup.moshi.Json

data class HourlyWeatherDto(
    @Json(name = "time_epoch")
    val timeEpoch: Long,
    @Json(name = "temp_c")
    val tempC: Double,
    @Json(name = "temp_f")
    val tempF: Double,
    @Json(name = "is_day")
    val isDay: Int,
    val condition: ConditionDto
)
