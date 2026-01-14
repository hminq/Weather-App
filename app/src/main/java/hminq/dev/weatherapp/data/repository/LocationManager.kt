package hminq.dev.weatherapp.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import hminq.dev.weatherapp.domain.entity.Location
import hminq.dev.weatherapp.domain.repository.LocationRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

/**
 * Repository implementation for location operations
 * Handles FusedLocationProviderClient logic
 * Note: Permission should be checked by caller (Activity) before calling this method
 */
class LocationManager @Inject constructor(
    @ApplicationContext private val context: Context
) : LocationRepository {

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Location? = suspendCancellableCoroutine { continuation ->
        // Permission should be checked by caller, but add safety check
        if (!hasLocationPermission()) {
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }

        val cancellationToken = CancellationTokenSource()
        
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            cancellationToken.token
        ).addOnSuccessListener { location ->
            location?.let {
                continuation.resume(Location(it.latitude, it.longitude))
            } ?: run {
                // Try last known location as fallback
                fusedLocationClient.lastLocation.addOnSuccessListener { lastLocation ->
                    lastLocation?.let {
                        continuation.resume(Location(it.latitude, it.longitude))
                    } ?: continuation.resume(null)
                }.addOnFailureListener {
                    continuation.resume(null)
                }
            }
        }.addOnFailureListener {
            // Try last known location as fallback
            fusedLocationClient.lastLocation.addOnSuccessListener { lastLocation ->
                lastLocation?.let {
                    continuation.resume(Location(it.latitude, it.longitude))
                } ?: continuation.resume(null)
            }.addOnFailureListener {
                continuation.resume(null)
            }
        }

        continuation.invokeOnCancellation {
            cancellationToken.cancel()
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }
}
