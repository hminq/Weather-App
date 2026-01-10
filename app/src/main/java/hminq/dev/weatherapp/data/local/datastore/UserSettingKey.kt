package hminq.dev.weatherapp.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.userSettingDataStore by preferencesDataStore("local_setting")

internal object UserSettingKey {
    val LANGUAGE = stringPreferencesKey("language_key")
    val THEME = stringPreferencesKey("theme_key")
    val TEMPERATURE = stringPreferencesKey("temperature_key")
    val SPEED_TYPE = stringPreferencesKey("speed_type_key")
}