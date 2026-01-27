package hminq.dev.weatherapp.data.model

data class CurrentWeatherModel(
    val location: LocationDto,
    val current: CurrentWeatherDto
)