package hminq.dev.weatherapp.domain.repository

import hminq.dev.weatherapp.domain.entity.Location

/**
 * Repository interface for location operations
 * Domain layer - no Android dependencies
 */
interface LocationRepository {
    /**
     * Get current device location
     * @return Location with latitude and longitude, or null if unavailable
     */
    suspend fun getCurrentLocation(): Location?
}
