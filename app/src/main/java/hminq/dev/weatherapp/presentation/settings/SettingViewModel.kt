package hminq.dev.weatherapp.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hminq.dev.weatherapp.domain.entity.UserSetting
import hminq.dev.weatherapp.domain.entity.enum.SpeedType
import hminq.dev.weatherapp.domain.entity.enum.Temperature
import hminq.dev.weatherapp.domain.exception.DomainException
import hminq.dev.weatherapp.domain.exception.UnknownException
import hminq.dev.weatherapp.domain.usecase.GetUserSetting
import hminq.dev.weatherapp.domain.usecase.SetUserSetting
import jakarta.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val getUserSetting: GetUserSetting,
    private val setUserSetting: SetUserSetting
) : ViewModel() {

    // UI State - single source of truth
    private val _uiState = MutableStateFlow<SettingUiState>(SettingUiState.Loading)
    val uiState: StateFlow<SettingUiState> = _uiState.asStateFlow()

    // One-time events using Channel (won't re-emit on config change)
    private val _event = Channel<SettingEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    // Derived property for current settings
    val currentSetting: UserSetting?
        get() = (_uiState.value as? SettingUiState.Success)?.userSetting

    init {
        loadUserSettings()
    }

    private fun loadUserSettings() {
        viewModelScope.launch {
            _uiState.value = SettingUiState.Loading

            getUserSetting.invoke()
                .onEach { setting ->
                    _uiState.value = SettingUiState.Success(setting)
                }
                .catch { exception ->
                    val domainException = exception as? DomainException
                        ?: UnknownException(exception.message ?: "Unknown error")
                    _uiState.value = SettingUiState.Error(domainException)
                }
                .collect {} // Terminal operator instead of launchIn
        }
    }

    private fun saveUserSetting(userSetting: UserSetting) {
        viewModelScope.launch {
            try {
                setUserSetting.invoke(userSetting)
                // Update local state immediately for responsive UI
                _uiState.value = SettingUiState.Success(userSetting)
                _event.send(SettingEvent.SaveSuccess("Settings saved successfully"))
            } catch (e: DomainException) {
                _event.send(SettingEvent.SaveError(e.message ?: "Failed to save settings"))
            } catch (e: Exception) {
                _event.send(SettingEvent.SaveError("Unexpected error occurred"))
            }
        }
    }

    fun updateTemperatureUnit(temperature: Temperature) {
        val setting = currentSetting ?: UserSetting()
        val updatedSetting = setting.copy(temperature = temperature)
        saveUserSetting(updatedSetting)
    }

    fun updateWindSpeedUnit(speedType: SpeedType) {
        val setting = currentSetting ?: UserSetting()
        val updatedSetting = setting.copy(windSpeedType = speedType)
        saveUserSetting(updatedSetting)
    }

    /**
     * UI State - represents the current state of the screen
     */
    sealed class SettingUiState {
        data object Loading : SettingUiState()
        data class Success(val userSetting: UserSetting) : SettingUiState()
        data class Error(val exception: DomainException) : SettingUiState()
    }

    /**
     * One-time events for UI actions (Toast, Snackbar, Navigation)
     */
    sealed class SettingEvent {
        data class SaveSuccess(val message: String) : SettingEvent()
        data class SaveError(val message: String) : SettingEvent()
    }
}