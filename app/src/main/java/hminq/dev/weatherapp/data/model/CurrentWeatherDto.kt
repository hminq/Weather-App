package hminq.dev.weatherapp.data.model

import com.squareup.moshi.Json

data class CurrentWeatherDto(
    @Json(name = "temp_c")
    val tempC: Double,
    @Json(name = "temp_f")
    val tempF: Double,
    @Json(name = "is_day")
    val isDay: Int,
    val condition: ConditionDto,
    @Json(name = "wind_mph")
    val windMph: Double,
    @Json(name = "wind_kph")
    val windKph: Double,
    @Json(name = "precip_mm")
    val precipMm: Double,
    val humidity: Int
)