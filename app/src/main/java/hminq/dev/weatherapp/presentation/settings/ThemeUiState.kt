package hminq.dev.weatherapp.presentation.settings

import hminq.dev.weatherapp.domain.entity.UserSetting
import hminq.dev.weatherapp.presentation.messages.UiMessage

/**
 * UI State - represents the current state of the Theme Screen
 */
sealed class ThemeUiState {
    data class Success(val userSetting: UserSetting) : ThemeUiState()
    data class Error(val message: UiMessage) : ThemeUiState()
}
