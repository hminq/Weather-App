package hminq.dev.weatherapp.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hminq.dev.weatherapp.R
import hminq.dev.weatherapp.domain.entity.UserSetting
import hminq.dev.weatherapp.domain.entity.enum.SpeedType
import hminq.dev.weatherapp.domain.entity.enum.Temperature
import hminq.dev.weatherapp.domain.exception.DomainException
import hminq.dev.weatherapp.domain.usecase.GetUserSetting
import hminq.dev.weatherapp.domain.usecase.SetUserSetting
import hminq.dev.weatherapp.presentation.messages.UiMessage
import jakarta.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val getUserSetting: GetUserSetting,
    private val setUserSetting: SetUserSetting
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow<SettingUiState>(SettingUiState.Success(UserSetting()))
    val uiState: StateFlow<SettingUiState> = _uiState.asStateFlow()
    private val _event = Channel<SettingEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()
    val currentSetting: UserSetting?
        get() = (_uiState.value as? SettingUiState.Success)?.userSetting

    private val saveSuccessMsg = UiMessage.StringResource(R.string.save_setting_success)
    private val saveErrorMsg = UiMessage.StringResource(R.string.save_setting_error)
    private val unknownErrorMsg = UiMessage.StringResource(R.string.unknown_error)

    init {
        loadUserSettings()
    }

    private fun loadUserSettings() {
        viewModelScope.launch {
            getUserSetting.invoke()
                .onEach { setting ->
                    _uiState.value = SettingUiState.Success(setting)
                }
                .collect {}
        }
    }

    private fun saveUserSetting(userSetting: UserSetting) {
        viewModelScope.launch {
            try {
                setUserSetting.invoke(userSetting)
                // Update local state
                _uiState.value = SettingUiState.Success(userSetting)
                _event.send(SettingEvent.SaveSuccess(saveSuccessMsg))
            } catch (_: DomainException) {
                _event.send(SettingEvent.SaveError(saveErrorMsg))
            } catch (_: Exception) {
                _event.send(SettingEvent.SaveError(unknownErrorMsg))
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
}