package hminq.dev.weatherapp.data.remote

import hminq.dev.weatherapp.data.model.CurrentWeatherModel
import hminq.dev.weatherapp.data.model.ForecastResponse
import hminq.dev.weatherapp.data.model.HistoryResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET("current.json")
    suspend fun getCurrentWeather(
        @Query("key") apiKey: String,
        @Query("q") query: String
    ): CurrentWeatherModel

    @GET("forecast.json")
    suspend fun getForecast(
        @Query("key") apiKey: String,
        @Query("q") query: String,
        @Query("days") days: Int = 1
    ): ForecastResponse

    @GET("history.json")
    suspend fun getHistory(
        @Query("key") apiKey: String,
        @Query("q") query: String,
        @Query("dt") date: String // yyyy-MM-dd format
    ): HistoryResponse
}
