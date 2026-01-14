package hminq.dev.weatherapp.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hminq.dev.weatherapp.R
import hminq.dev.weatherapp.domain.entity.UserSetting
import hminq.dev.weatherapp.domain.entity.enum.Theme
import hminq.dev.weatherapp.domain.exception.DomainException
import hminq.dev.weatherapp.domain.usecase.GetUserSetting
import hminq.dev.weatherapp.domain.usecase.SetUserSetting
import hminq.dev.weatherapp.presentation.messages.UiMessage
import jakarta.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val getUserSetting: GetUserSetting,
    private val setUserSetting: SetUserSetting
) : ViewModel() {

    private val _uiState = MutableStateFlow<ThemeUiState>(ThemeUiState.Success(UserSetting()))
    val uiState: StateFlow<ThemeUiState> = _uiState.asStateFlow()

    private val _event = Channel<ThemeEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    private val currentSetting: UserSetting
        get() = when (val state = _uiState.value) {
            is ThemeUiState.Success -> state.userSetting
        }

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
                    _uiState.value = ThemeUiState.Success(setting)
                }
                .collect() 
        }
    }

    private fun saveUserSetting(userSetting: UserSetting) {
        viewModelScope.launch {
            try {
                setUserSetting.invoke(userSetting)
                // State will be automatically updated via Flow from Repository
                _event.send(ThemeEvent.SaveSuccess(saveSuccessMsg))
                _event.send(ThemeEvent.ThemeChanged)
            } catch (_: DomainException) {
                _event.send(ThemeEvent.SaveError(saveErrorMsg))
            } catch (_: Exception) {
                _event.send(ThemeEvent.SaveError(unknownErrorMsg))
            }
        }
    }

    fun updateTheme(theme: Theme) {
        val updatedSetting = currentSetting.copy(theme = theme)
        saveUserSetting(updatedSetting)
    }
}
