package hminq.dev.weatherapp.presentation.weather

import hminq.dev.weatherapp.domain.entity.CurrentWeather
import hminq.dev.weatherapp.domain.entity.ForecastItem
import hminq.dev.weatherapp.domain.entity.HourForecast
import hminq.dev.weatherapp.domain.entity.UserSetting
import hminq.dev.weatherapp.presentation.messages.UiMessage

enum class ForecastTab { TOMORROW, THIS_WEEK }

data class WeatherUiState(
    val isLoading: Boolean = true,
    val isForecastLoading: Boolean = true,
    val currentWeather: CurrentWeather? = null,
    val hourlyForecast: List<HourForecast> = emptyList(),
    val hourlyTimeZoneId: String = "UTC",
    val tomorrowForecast: List<ForecastItem> = emptyList(),
    val weekForecast: List<ForecastItem> = emptyList(),
    val selectedTab: ForecastTab = ForecastTab.TOMORROW,
    val userSetting: UserSetting = UserSetting(),
    val error: UiMessage? = null,
    val locationPermissionGranted: Boolean = false
)
