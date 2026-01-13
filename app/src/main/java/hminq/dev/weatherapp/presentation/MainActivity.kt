package hminq.dev.weatherapp.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import hminq.dev.weatherapp.R

/**
 * MainActivity - Entry point of the application
 *
 * Language handling:
 * - Per-app language preferences are managed via AppCompatDelegate.setApplicationLocales()
 * - This is handled in LanguageFragment when user changes language
 * - AppCompat automatically persists and restores the selected locale
 * - See: https://developer.android.com/guide/topics/resources/app-languages
 *
 * Theme handling:
 * - Theme is managed via AppCompatDelegate.setDefaultNightMode() in WeatherApp (Application class)
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}