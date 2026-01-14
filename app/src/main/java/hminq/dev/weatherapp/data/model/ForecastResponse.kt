package hminq.dev.weatherapp.data.model

import com.squareup.moshi.Json

data class ForecastResponse(
    val location: LocationDto,
    val current: CurrentWeatherDto,
    val forecast: ForecastDto
)

/**
 * Forecast container
 */
data class ForecastDto(
    @Json(name = "forecastday")
    val forecastDay: List<ForecastDayDto>
)
