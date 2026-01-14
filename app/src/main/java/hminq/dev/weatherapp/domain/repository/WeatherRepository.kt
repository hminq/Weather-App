package hminq.dev.weatherapp.domain.repository

import hminq.dev.weatherapp.domain.entity.CurrentWeather
import hminq.dev.weatherapp.domain.entity.DayForecast
import hminq.dev.weatherapp.domain.entity.ForecastData
import hminq.dev.weatherapp.domain.entity.HourForecast

interface WeatherRepository {
    /**
     * Get current weather for location
     */
    suspend fun getCurrentWeather(lat: Double, lon: Double): CurrentWeather

    /**
     * Get forecast for N days (includes hourly data)
     * @return ForecastData with timezone info
     */
    suspend fun getForecast(lat: Double, lon: Double, days: Int): ForecastData

    /**
     * Get historical weather for a specific date
     * @param date in yyyy-MM-dd format
     */
    suspend fun getHistory(lat: Double, lon: Double, date: String): DayForecast?

    /**
     * Get hourly forecast for today (for chart)
     */
    suspend fun getTodayHourlyForecast(lat: Double, lon: Double): Pair<String, List<HourForecast>>
}