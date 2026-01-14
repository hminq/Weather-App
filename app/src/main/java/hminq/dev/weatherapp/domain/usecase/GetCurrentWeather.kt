package hminq.dev.weatherapp.domain.usecase

import hminq.dev.weatherapp.domain.entity.CurrentWeather
import hminq.dev.weatherapp.domain.repository.WeatherRepository
import javax.inject.Inject

class GetCurrentWeather @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double): CurrentWeather {
        return weatherRepository.getCurrentWeather(lat, lon)
    }
}
