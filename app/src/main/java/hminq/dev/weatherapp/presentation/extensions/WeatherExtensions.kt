package hminq.dev.weatherapp.presentation.extensions

import hminq.dev.weatherapp.domain.entity.CurrentWeather
import hminq.dev.weatherapp.domain.entity.enum.Condition
import hminq.dev.weatherapp.domain.entity.enum.SpeedType
import hminq.dev.weatherapp.domain.entity.enum.Temperature
import hminq.dev.weatherapp.R

/**
 * Extension functions for weather data formatting
 */

fun CurrentWeather.formatTemperature(unit: Temperature): String {
    return if (unit == Temperature.CELSIUS) {
        "${tempC.toInt()}°C"
    } else {
        "${tempF.toInt()}°F"
    }
}

fun CurrentWeather.formatWindSpeed(speedType: SpeedType): String {
    return if (speedType == SpeedType.KMH) {
        "${windKph.toInt()} km/h"
    } else {
        "${windMph.toInt()} mph"
    }
}

fun CurrentWeather.formatHumidity(): String {
    return "$humidity %"
}

fun CurrentWeather.formatRain(): String {
    // If condition is RAIN but precipitation is 0, it means light rain (< 1mm)
    // Show "< 1 mm" instead of "0 mm" for better UX
    return when {
        condition == Condition.RAIN && rainMm < 1.0 -> "< 1 mm"
        rainMm == 0.0 -> "0 mm"
        else -> "${rainMm.toInt()} mm"
    }
}

fun Condition.getDisplayText(): String {
    return when (this) {
        Condition.CLEAR_DAY -> "Clear"
        Condition.CLEAR_NIGHT -> "Clear Night"
        Condition.CLOUDY -> "Cloudy"
        Condition.RAIN -> "Rainy"
        Condition.SNOW -> "Snowy"
        Condition.ICE -> "Icy"
        Condition.THUNDER -> "Thunderstorm"
        Condition.FOG -> "Foggy"
        Condition.UNKNOWN -> "Unknown"
    }
}

fun Condition.getIconResource(): Int {
    return when (this) {
        Condition.CLEAR_DAY -> R.drawable.ic_clear_day
        Condition.CLEAR_NIGHT -> R.drawable.ic_clear_night
        Condition.CLOUDY -> R.drawable.ic_cloudy
        Condition.RAIN -> R.drawable.ic_rain
        Condition.SNOW -> R.drawable.ic_snow
        Condition.ICE -> R.drawable.ic_ice
        Condition.THUNDER -> R.drawable.ic_thunder
        Condition.FOG -> R.drawable.ic_fog
        Condition.UNKNOWN -> R.drawable.ic_cloudy
    }
}
