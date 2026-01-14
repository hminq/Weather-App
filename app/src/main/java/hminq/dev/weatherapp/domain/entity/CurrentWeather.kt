package hminq.dev.weatherapp.domain.entity

import hminq.dev.weatherapp.domain.entity.enum.Condition

data class CurrentWeather(
    val city: String,
    val country: String,
    val localTime: String,
    val condition: Condition,
    val tempC: Double,
    val tempF: Double,
    val windMph: Double,
    val windKph: Double,
    val humidity: Int,
    val rainMm: Double  // Precipitation in millimeters
)
