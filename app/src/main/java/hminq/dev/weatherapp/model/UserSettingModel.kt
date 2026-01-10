package hminq.dev.weatherapp.model

import hminq.dev.weatherapp.model.enum.LanguageModel
import hminq.dev.weatherapp.model.enum.SpeedTypeModel
import hminq.dev.weatherapp.model.enum.TemperatureModel
import hminq.dev.weatherapp.model.enum.ThemeModel

data class UserSettingModel(
    val language: LanguageModel = LanguageModel.ENGLISH,
    val theme: ThemeModel = ThemeModel.SYSTEM,
    val temperature: TemperatureModel = TemperatureModel.CELSIUS,
    val windSpeedType: SpeedTypeModel = SpeedTypeModel.KMH,
) {
    companion object {
        val DEFAULT_SETTING: UserSettingModel = UserSettingModel()
    }
}