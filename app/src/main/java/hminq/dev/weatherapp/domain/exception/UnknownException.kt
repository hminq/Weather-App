package hminq.dev.weatherapp.domain.exception

data class UnknownException(
    override val message: String = "Unknown error"
) : DomainException(message)
