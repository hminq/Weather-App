package hminq.dev.weatherapp.data.model

import hminq.dev.weatherapp.data.model.enum.LanguageModel
import hminq.dev.weatherapp.data.model.enum.SpeedTypeModel
import hminq.dev.weatherapp.data.model.enum.TemperatureModel
import hminq.dev.weatherapp.data.model.enum.ThemeModel

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