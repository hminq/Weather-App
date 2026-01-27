package hminq.dev.weatherapp.presentation.settings

import hminq.dev.weatherapp.presentation.messages.UiMessage

/**
 * One-time events for UI actions for Theme Screen
 */
sealed class ThemeEvent {
    object ThemeChanged : ThemeEvent()
    data class SaveSuccess(val message: UiMessage) : ThemeEvent()
    data class SaveError(val message: UiMessage) : ThemeEvent()
}
