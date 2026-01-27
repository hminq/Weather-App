package hminq.dev.weatherapp.domain.exception

class NetworkException(
    cause: Throwable? = null
) : DomainException(cause = cause)
