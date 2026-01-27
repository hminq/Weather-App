package hminq.dev.weatherapp.domain.exception

class UnknownException(
    cause: Throwable? = null
) : DomainException(cause = cause)
