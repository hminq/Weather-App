package hminq.dev.weatherapp.presentation.settings

import hminq.dev.weatherapp.presentation.messages.UiMessage

/**
 * One-time events for UI actions for Setting Screen
 */
sealed class SettingEvent {
    data class SaveSuccess(
        val message: UiMessage
    ) : SettingEvent()

    data class SaveError(
        val message: UiMessage
    ) : SettingEvent()
}