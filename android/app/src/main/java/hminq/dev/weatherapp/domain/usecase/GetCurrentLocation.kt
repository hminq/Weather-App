package hminq.dev.weatherapp.domain.usecase

import hminq.dev.weatherapp.domain.entity.Location
import hminq.dev.weatherapp.domain.repository.LocationRepository
import javax.inject.Inject

/**
 * UseCase to get current device location
 * Encapsulates location fetching logic
 */
class GetCurrentLocation @Inject constructor(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(): Location? {
        return locationRepository.getCurrentLocation()
    }
}
