package hminq.dev.weatherapp.domain.usecase

import hminq.dev.weatherapp.domain.entity.ForecastItem
import hminq.dev.weatherapp.domain.entity.HourForecast
import hminq.dev.weatherapp.domain.repository.WeatherRepository
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

/**
 * UseCase to get tomorrow's forecast at 4 evenly spaced times
 * Contains all business logic for hour selection and formatting
 */
class GetTomorrowForecast @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    // 4 evenly spaced hours: 6am, 12pm, 6pm, 11pm
    private val targetHours = listOf(6, 12, 18, 23)

    suspend operator fun invoke(lat: Double, lon: Double): List<ForecastItem> {
        val forecastData = weatherRepository.getForecast(lat, lon, days = 2)
        val timeZone = ZoneId.of(forecastData.timeZoneId)

        // Get tomorrow's hourly data (index 1)
        val tomorrowHours = forecastData.days.getOrNull(1)?.hours ?: return emptyList()

        return targetHours.mapNotNull { targetHour ->
            tomorrowHours.getOrNull(targetHour)?.toForecastItem(timeZone)
        }
    }

    private fun HourForecast.toForecastItem(timeZone: ZoneId): ForecastItem {
        val time = Instant.ofEpochSecond(timeEpoch).atZone(timeZone)
        val hour = time.hour
        val label = when {
            hour == 0 -> "12 am"
            hour < 12 -> "$hour am"
            hour == 12 -> "12 pm"
            else -> "${hour - 12} pm"
        }
        return ForecastItem(
            label = label,
            condition = condition,
            tempC = tempC,
            tempF = tempF,
            isToday = false
        )
    }
}
