package hminq.dev.weatherapp.presentation.settings

import hminq.dev.weatherapp.domain.entity.enum.Language
import hminq.dev.weatherapp.presentation.messages.UiMessage

/**
 * One-time events for UI actions for Language Screen
 */
sealed class LanguageEvent {
    data class LanguageChanged(val language: Language) : LanguageEvent()
    data class SaveSuccess(val message: UiMessage) : LanguageEvent()
    data class SaveError(val message: UiMessage) : LanguageEvent()
}
