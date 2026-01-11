package hminq.dev.weatherapp.presentation.messages

import androidx.annotation.StringRes

sealed class UiMessage {
    data class DynamicString(val value: String) : UiMessage()

    data class StringResource(
        @StringRes val resId: Int,
        val args: List<Any> = emptyList()
    ) : UiMessage()
}