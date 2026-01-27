package hminq.dev.weatherapp.data.repository

import hminq.dev.weatherapp.data.mapper.toDomain
import hminq.dev.weatherapp.data.mapper.toDomainHour
import hminq.dev.weatherapp.data.remote.WeatherApiService
import hminq.dev.weatherapp.domain.entity.CurrentWeather
import hminq.dev.weatherapp.domain.entity.DayForecast
import hminq.dev.weatherapp.domain.entity.ForecastData
import hminq.dev.weatherapp.domain.entity.HourForecast
import hminq.dev.weatherapp.domain.exception.NetworkException
import hminq.dev.weatherapp.domain.repository.WeatherRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named

class WeatherRepositoryImpl @Inject constructor(
    private val apiService: WeatherApiService,
    @Named("apiKey") private val apiKey: String
) : WeatherRepository {

    override suspend fun getCurrentWeather(lat: Double, lon: Double): CurrentWeather {
        val query = "$lat,$lon"
        return try {
            apiService.getCurrentWeather(apiKey, query).toDomain()
        } catch (e: HttpException) {
            throw NetworkException(cause = e)
        } catch (e: IOException) {
            throw NetworkException(cause = e)
        } catch (e: Exception) {
            throw NetworkException(cause = e)
        }
    }

    override suspend fun getForecast(lat: Double, lon: Double, days: Int): ForecastData {
        val query = "$lat,$lon"
        return try {
            apiService.getForecast(apiKey, query, days).toDomain()
        } catch (e: HttpException) {
            throw NetworkException(cause = e)
        } catch (e: IOException) {
            throw NetworkException(cause = e)
        } catch (e: Exception) {
            throw NetworkException(cause = e)
        }
    }

    override suspend fun getHistory(lat: Double, lon: Double, date: String): DayForecast? {
        val query = "$lat,$lon"
        return try {
            val response = apiService.getHistory(apiKey, query, date)
            response.forecast.forecastDay.firstOrNull()?.toDomain()
        } catch (e: HttpException) {
            // Return null for 404 (no history data available) instead of throwing exception
            if (e.code() == 404) {
                null
            } else {
                throw NetworkException(cause = e)
            }
        } catch (e: IOException) {
            throw NetworkException(cause = e)
        } catch (e: Exception) {
            throw NetworkException(cause = e)
        }
    }

    override suspend fun getTodayHourlyForecast(lat: Double, lon: Double): Pair<String, List<HourForecast>> {
        val query = "$lat,$lon"
        return try {
            val response = apiService.getForecast(apiKey, query, days = 1)
            val hours = response.forecast.forecastDay
                .firstOrNull()
                ?.hourly
                ?.map { it.toDomainHour() }
                ?: emptyList()
            response.location.tzId to hours
        } catch (e: HttpException) {
            throw NetworkException(cause = e)
        } catch (e: IOException) {
            throw NetworkException(cause = e)
        } catch (e: Exception) {
            throw NetworkException(cause = e)
        }
    }
}