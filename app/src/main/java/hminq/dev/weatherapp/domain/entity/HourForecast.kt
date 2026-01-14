package hminq.dev.weatherapp.domain.entity

import hminq.dev.weatherapp.domain.entity.enum.Condition

/**
 * Domain entity for hourly forecast data
 * Raw data from Repository, business logic applied in UseCase
 */
data class HourForecast(
    val timeEpoch: Long,
    val condition: Condition,
    val tempC: Double,
    val tempF: Double,
    val isDay: Boolean
)
