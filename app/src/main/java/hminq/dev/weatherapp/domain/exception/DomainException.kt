package hminq.dev.weatherapp.domain.exception

sealed class DomainException(
    message: String? = null,
    cause: Throwable? = null
) : Exception(message, cause)