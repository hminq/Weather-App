package hminq.dev.weatherapp.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hminq.dev.weatherapp.R
import hminq.dev.weatherapp.domain.entity.UserSetting
import hminq.dev.weatherapp.domain.entity.enum.Language
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
class LanguageViewModel @Inject constructor(
    private val getUserSetting: GetUserSetting,
    private val setUserSetting: SetUserSetting
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow<LanguageUiState>(LanguageUiState.Success(UserSetting()))
    val uiState: StateFlow<LanguageUiState> = _uiState.asStateFlow()
    private val _event = Channel<LanguageEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()
    val currentSetting: UserSetting
        get() = when (val state = _uiState.value) {
            is LanguageUiState.Success -> state.userSetting
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
                    _uiState.value = LanguageUiState.Success(setting)
                }
                .collect() 
        }
    }

    private fun saveUserSetting(userSetting: UserSetting) {
        viewModelScope.launch {
            try {
                setUserSetting.invoke(userSetting)
                // State will be automatically updated via Flow from Repository
                // Send SaveSuccess before LanguageChanged
                _event.send(LanguageEvent.SaveSuccess(saveSuccessMsg))
                _event.send(LanguageEvent.LanguageChanged(userSetting.language))
            } catch (_: DomainException) {
                _event.send(LanguageEvent.SaveError(saveErrorMsg))
            } catch (_: Exception) {
                _event.send(LanguageEvent.SaveError(unknownErrorMsg))
            }
        }
    }

    fun updateLanguage(language: Language) {
        val updatedSetting = currentSetting.copy(language = language)
        saveUserSetting(updatedSetting)
    }
}
