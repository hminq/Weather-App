package hminq.dev.weatherapp.presentation.theme

import androidx.appcompat.app.AppCompatDelegate
import hminq.dev.weatherapp.domain.entity.enum.Theme
import hminq.dev.weatherapp.domain.usecase.GetUserSetting
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeManager @Inject constructor(
    private val getUserSetting: GetUserSetting
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    fun initialize() {
        scope.launch {
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
