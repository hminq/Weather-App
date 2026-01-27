package hminq.dev.weatherapp.presentation.settings

import hminq.dev.weatherapp.domain.entity.UserSetting

/**
 * UI State - represents the current state of the Language Screen
 */
sealed class LanguageUiState {
    data class Success(val userSetting: UserSetting) : LanguageUiState()
}
