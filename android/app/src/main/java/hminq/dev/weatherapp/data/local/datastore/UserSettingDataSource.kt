package hminq.dev.weatherapp.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import hminq.dev.weatherapp.data.model.UserSettingModel
import hminq.dev.weatherapp.data.model.enum.LanguageModel
import hminq.dev.weatherapp.data.model.enum.SpeedTypeModel
import hminq.dev.weatherapp.data.model.enum.TemperatureModel
import hminq.dev.weatherapp.data.model.enum.ThemeModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserSettingDataSource @Inject constructor(
    @ApplicationContext context: Context
) {
    private val dataStore = context.userSettingDataStore

    suspend fun save(userSettingModel: UserSettingModel) {
        try {
            dataStore.edit { preferences ->
                // Save enum Language as a String
                preferences[UserSettingKey.LANGUAGE] = userSettingModel.language.name
                preferences[UserSettingKey.THEME] = userSettingModel.theme.name
                preferences[UserSettingKey.TEMPERATURE] = userSettingModel.temperature.name
                preferences[UserSettingKey.SPEED_TYPE] = userSettingModel.windSpeedType.name
            }
        } catch (e: IOException) {
            throw e
        } catch (ex: Exception) {
            throw ex
        }
    }

    fun get(): Flow<UserSettingModel> {
        return dataStore.data
            .catch { _ ->
                // Fallback: emit empty preferences if read fails
                emit(emptyPreferences())
            }
            .map { preferences ->
                // Read Language (String) & convert to Enum
                val language = preferences[UserSettingKey.LANGUAGE] ?: UserSettingModel.DEFAULT_SETTING.language.name
                val theme = preferences[UserSettingKey.THEME] ?: UserSettingModel.DEFAULT_SETTING.theme.name
                val temperature = preferences[UserSettingKey.TEMPERATURE] ?: UserSettingModel.DEFAULT_SETTING.temperature.name
                val windSpeedType = preferences[UserSettingKey.SPEED_TYPE] ?: UserSettingModel.DEFAULT_SETTING.windSpeedType.name

                val languageModel = try {
                    LanguageModel.valueOf(language)
                } catch (_: IllegalArgumentException) {
                    UserSettingModel.DEFAULT_SETTING.language
                }

                val themeModel = try {
                    ThemeModel.valueOf(theme)
                } catch (_: IllegalArgumentException) {
                    UserSettingModel.DEFAULT_SETTING.theme
                }

                val temperatureModel = try {
                    TemperatureModel.valueOf(temperature)
                } catch (_: IllegalArgumentException) {
                    UserSettingModel.DEFAULT_SETTING.temperature
                }

                val windSpeedTypeModel = try {
                    SpeedTypeModel.valueOf(windSpeedType)
                } catch (_: IllegalArgumentException) {
                    UserSettingModel.DEFAULT_SETTING.windSpeedType
                }

                UserSettingModel(languageModel, themeModel, temperatureModel, windSpeedTypeModel)
            }
    }
}