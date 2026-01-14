package hminq.dev.weatherapp.presentation.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hminq.dev.weatherapp.R
import hminq.dev.weatherapp.domain.exception.DomainException
import hminq.dev.weatherapp.domain.exception.NetworkException
import hminq.dev.weatherapp.domain.usecase.GetCurrentWeather
import hminq.dev.weatherapp.domain.usecase.GetHourlyForecast
import hminq.dev.weatherapp.domain.usecase.GetTomorrowForecast
import hminq.dev.weatherapp.domain.usecase.GetUserSetting
import hminq.dev.weatherapp.domain.usecase.GetWeekForecast
import hminq.dev.weatherapp.presentation.messages.UiMessage
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getCurrentWeather: GetCurrentWeather,
    private val getHourlyForecast: GetHourlyForecast,
    private val getTomorrowForecast: GetTomorrowForecast,
    private val getWeekForecast: GetWeekForecast,
    private val getUserSetting: GetUserSetting
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()
    private val networkErrorMsg = UiMessage.StringResource(R.string.network_error)
    private val unknownErrorMsg = UiMessage.StringResource(R.string.unknown_error)

    init {
        loadUserSettings()
    }

    private fun loadUserSettings() {
        viewModelScope.launch {
            getUserSetting.invoke().collect { setting ->
                _uiState.update { it.copy(userSetting = setting) }
            }
        }
    }

    fun onLocationPermissionResult(granted: Boolean) {
        _uiState.update { it.copy(locationPermissionGranted = granted) }
    }

    fun selectTab(tab: ForecastTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun loadWeatherData(lat: Double, lon: Double) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isForecastLoading = true, error = null) }

            try {
                // Fetch all endpoints in parallel
                val currentWeatherDeferred = async { getCurrentWeather(lat, lon) }
                val hourlyForecastDeferred = async { getHourlyForecast(lat, lon) }
                val tomorrowForecastDeferred = async { getTomorrowForecast(lat, lon) }
                val weekForecastDeferred = async { getWeekForecast(lat, lon) }

                // Await all results
                val currentWeather = currentWeatherDeferred.await()
                val (timeZoneId, hourlyForecast) = hourlyForecastDeferred.await()
                val tomorrowForecast = tomorrowForecastDeferred.await()
                val weekForecast = weekForecastDeferred.await()

                // Update UI state once with all data
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isForecastLoading = false,
                        currentWeather = currentWeather,
                        hourlyForecast = hourlyForecast,
                        hourlyTimeZoneId = timeZoneId,
                        tomorrowForecast = tomorrowForecast,
                        weekForecast = weekForecast,
                        error = null
                    )
                }
            } catch (e: NetworkException) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isForecastLoading = false,
                        error = networkErrorMsg
                    )
                }
            } catch (e: DomainException) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isForecastLoading = false,
                        error = unknownErrorMsg
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isForecastLoading = false,
                        error = unknownErrorMsg
                    )
                }
            }
        }
    }
}
