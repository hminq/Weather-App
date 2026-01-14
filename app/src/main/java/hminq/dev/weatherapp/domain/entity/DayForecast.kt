package hminq.dev.weatherapp.domain.entity

import hminq.dev.weatherapp.domain.entity.enum.Condition

/**
 * Domain entity for daily forecast data
 * Raw data from Repository, business logic applied in UseCase
 */
data class DayForecast(
    val dateEpoch: Long,
    val condition: Condition,
    val avgTempC: Double,
    val avgTempF: Double
)
