package hminq.dev.weatherapp.data.mapper

import hminq.dev.weatherapp.domain.entity.UserSetting
import hminq.dev.weatherapp.domain.entity.enum.Language
import hminq.dev.weatherapp.domain.entity.enum.SpeedType
import hminq.dev.weatherapp.domain.entity.enum.Temperature
import hminq.dev.weatherapp.domain.entity.enum.Theme
import hminq.dev.weatherapp.model.UserSettingModel
import hminq.dev.weatherapp.model.enum.LanguageModel
import hminq.dev.weatherapp.model.enum.SpeedTypeModel
import hminq.dev.weatherapp.model.enum.TemperatureModel
import hminq.dev.weatherapp.model.enum.ThemeModel

fun UserSettingModel.toDomain(): UserSetting {
    val defaultDomainLanguage = Language.ENGLISH
    val defaultDomainTheme = Theme.SYSTEM
    val defaultDomainTemperature = Temperature.CELSIUS
    val defaultWindSpeedType = SpeedType.KMH

    val domainLanguage = try {
        Language.valueOf(this.language.name)
    } catch (_: IllegalArgumentException) {
        defaultDomainLanguage
    }

    val domainTheme = try {
        Theme.valueOf(this.theme.name)
    } catch (_: IllegalArgumentException) {
        defaultDomainTheme
    }

    val domainTemperature = try {
        Temperature.valueOf(this.temperature.name)
    } catch (_: IllegalArgumentException) {
        defaultDomainTemperature
    }

    val domainWindSpeedType = try {
        SpeedType.valueOf(this.windSpeedType.name)
    } catch (_: IllegalArgumentException) {
        defaultWindSpeedType
    }

    return UserSetting(
        language = domainLanguage,
        theme = domainTheme,
        temperature = domainTemperature,
        windSpeedType = domainWindSpeedType,
    )
}

fun UserSetting.toData(): UserSettingModel {
    val defaultLanguageModel = LanguageModel.ENGLISH
    val defaultThemeModel = ThemeModel.SYSTEM
    val defaultTemperatureModel = TemperatureModel.CELSIUS
    val defaultWindSpeedTypeModel = SpeedTypeModel.KMH

    val languageModel = try {
        LanguageModel.valueOf(this.language.name)
    } catch (_: IllegalArgumentException) {
        defaultLanguageModel
    }

    val themeModel = try {
        ThemeModel.valueOf(this.theme.name)
    } catch (_: IllegalArgumentException) {
        defaultThemeModel
    }

    val temperatureModel = try {
        TemperatureModel.valueOf(this.temperature.name)
    } catch (_: IllegalArgumentException) {
        defaultTemperatureModel
    }

    val windSpeedTypeModel = try {
        SpeedTypeModel.valueOf(this.windSpeedType.name)
    } catch (_: IllegalArgumentException) {
        defaultWindSpeedTypeModel
    }

    return UserSettingModel(
        language = languageModel,
        theme = themeModel,
        temperature = temperatureModel,
        windSpeedType = windSpeedTypeModel
    )
}