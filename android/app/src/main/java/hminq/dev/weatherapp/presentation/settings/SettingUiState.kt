package hminq.dev.weatherapp.presentation.settings

import hminq.dev.weatherapp.domain.entity.UserSetting

/**
 * UI State - represents the current state of the Setting Screen
 */
sealed class SettingUiState {
    data class Success(val userSetting: UserSetting) : SettingUiState()
}