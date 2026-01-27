package hminq.dev.weatherapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import hminq.dev.weatherapp.presentation.theme.ThemeManager
import javax.inject.Inject

@HiltAndroidApp
class WeatherApp : Application() {

    @Inject
    lateinit var themeManager: ThemeManager

    override fun onCreate() {
        super.onCreate()
        themeManager.initialize()
    }
}