package hminq.dev.weatherapp.domain.entity

import hminq.dev.weatherapp.domain.entity.enum.Language
import hminq.dev.weatherapp.domain.entity.enum.SpeedType
import hminq.dev.weatherapp.domain.entity.enum.Temperature
import hminq.dev.weatherapp.domain.entity.enum.Theme

data class UserSetting(
    val language: Language = Language.ENGLISH,
    val theme: Theme = Theme.SYSTEM,
    val temperature: Temperature = Temperature.CELSIUS,
    val windSpeedType: SpeedType = SpeedType.KMH
)