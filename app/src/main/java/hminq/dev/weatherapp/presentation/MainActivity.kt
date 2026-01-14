package hminq.dev.weatherapp.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import hminq.dev.weatherapp.R
import hminq.dev.weatherapp.domain.usecase.GetCurrentLocation
import hminq.dev.weatherapp.presentation.weather.WeatherViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var getCurrentLocation: GetCurrentLocation

    private val weatherViewModel: WeatherViewModel by viewModels()

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineLocationGranted || coarseLocationGranted) {
            weatherViewModel.onLocationPermissionResult(true)
            loadLocationAndWeather()
        } else {
            weatherViewModel.onLocationPermissionResult(false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkLocationPermissionAndLoad()
    }

    private fun checkLocationPermissionAndLoad() {
        when {
            hasLocationPermission() -> {
                weatherViewModel.onLocationPermissionResult(true)
                loadLocationAndWeather()
            }
            else -> {
                requestLocationPermission()
            }
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun loadLocationAndWeather() {
        lifecycleScope.launch {
            val location = getCurrentLocation()
            location?.let {
                weatherViewModel.loadWeatherData(it.latitude, it.longitude)
            }
        }
    }
}