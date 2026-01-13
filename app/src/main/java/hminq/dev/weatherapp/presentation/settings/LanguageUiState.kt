package hminq.dev.weatherapp.presentation.settings

import hminq.dev.weatherapp.domain.entity.UserSetting
import hminq.dev.weatherapp.presentation.messages.UiMessage

/**
 * UI State - represents the current state of the Language Screen
 */
sealed class LanguageUiState {
    data class Success(val userSetting: UserSetting) : LanguageUiState()
    data class Error(val message: UiMessage) : LanguageUiState()
}
