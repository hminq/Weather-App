package hminq.dev.weatherapp.domain.usecase

import hminq.dev.weatherapp.domain.entity.HourForecast
import hminq.dev.weatherapp.domain.repository.WeatherRepository
import javax.inject.Inject

/**
 * UseCase to get today's hourly forecast for the chart
 * Returns timezone info + hourly data
 */
class GetHourlyForecast @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    /**
     * @return Pair of (timeZoneId, hourlyForecasts)
     */
    suspend operator fun invoke(lat: Double, lon: Double): Pair<String, List<HourForecast>> {
        return weatherRepository.getTodayHourlyForecast(lat, lon)
    }
}
