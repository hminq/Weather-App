package hminq.dev.weatherapp.domain.exception

import androidx.annotation.StringRes
import hminq.dev.weatherapp.R

sealed class DomainException(
    message: String? = null,
    cause: Throwable? = null,
    @StringRes val messageResId: Int? = R.string.general_error
) : Exception(message, cause)