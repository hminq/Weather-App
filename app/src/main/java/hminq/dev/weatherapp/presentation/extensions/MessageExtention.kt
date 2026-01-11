package hminq.dev.weatherapp.presentation.extensions

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import hminq.dev.weatherapp.R
import hminq.dev.weatherapp.presentation.messages.UiMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun UiMessage.asString(context: Context): String {
    return when (this) {
        is UiMessage.DynamicString -> value
        is UiMessage.StringResource -> {
            if (args.isEmpty()) context.getString(resId)
            else context.getString(resId, *args.toTypedArray())
        }
    }
}

enum class MessageType(val iconRes: Int) {
    SUCCESS(R.drawable.ic_success),
    ERROR(R.drawable.ic_error)
}

private var hideJob: Job? = null

fun CardView.showMessage(
    message: UiMessage,
    type: MessageType,
    scope: CoroutineScope,
    autoHideMillis: Long = 2000L
) {
    val ivMessage = findViewById<ImageView>(R.id.ivMessage)
    val tvMessage = findViewById<TextView>(R.id.tvMessage)

    ivMessage.setImageResource(type.iconRes)
    tvMessage.text = message.asString(context)
    tvMessage.visibility = View.VISIBLE

    visibility = View.VISIBLE
    alpha = 0f
    animate().alpha(1f).setDuration(200).start()

    hideJob?.cancel()
    hideJob = scope.launch {
        delay(autoHideMillis)
        hideMessage()
    }
}

fun CardView.hideMessage() {
    hideJob?.cancel()
    animate()
        .alpha(0f)
        .setDuration(200)
        .withEndAction { visibility = View.GONE }
        .start()
}