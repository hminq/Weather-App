package hminq.dev.weatherapp

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp
import hminq.dev.weatherapp.domain.entity.enum.Theme
import hminq.dev.weatherapp.domain.usecase.GetUserSetting
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class WeatherApp : Application() {

    @Inject
    lateinit var getUserSetting: GetUserSetting

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        
        // Apply theme on app startup
        applicationScope.launch {
            getUserSetting.invoke().collect { setting ->
                applyTheme(setting.theme)
            }
        }
    }

    private fun applyTheme(theme: Theme) {
        val mode = when (theme) {
            Theme.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            Theme.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            Theme.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}