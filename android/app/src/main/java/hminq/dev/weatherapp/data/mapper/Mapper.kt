package hminq.dev.weatherapp.data.mapper

import hminq.dev.weatherapp.data.model.CurrentWeatherModel
import hminq.dev.weatherapp.data.model.DaySummaryDto
import hminq.dev.weatherapp.data.model.ForecastDayDto
import hminq.dev.weatherapp.data.model.ForecastResponse
import hminq.dev.weatherapp.data.model.HistoryDayDto
import hminq.dev.weatherapp.data.model.HourlyWeatherDto
import hminq.dev.weatherapp.data.model.UserSettingModel
import hminq.dev.weatherapp.data.model.enum.LanguageModel
import hminq.dev.weatherapp.data.model.enum.SpeedTypeModel
import hminq.dev.weatherapp.data.model.enum.TemperatureModel
import hminq.dev.weatherapp.data.model.enum.ThemeModel
import hminq.dev.weatherapp.domain.entity.CurrentWeather
import hminq.dev.weatherapp.domain.entity.DayForecast
import hminq.dev.weatherapp.domain.entity.DayForecastWithHours
import hminq.dev.weatherapp.domain.entity.ForecastData
import hminq.dev.weatherapp.domain.entity.HourForecast
import hminq.dev.weatherapp.domain.entity.UserSetting
import hminq.dev.weatherapp.domain.entity.enum.Condition
import hminq.dev.weatherapp.domain.entity.enum.Language
import hminq.dev.weatherapp.domain.entity.enum.SpeedType
import hminq.dev.weatherapp.domain.entity.enum.Temperature
import hminq.dev.weatherapp.domain.entity.enum.Theme

// ============== User Setting Mappers ==============

fun UserSettingModel.toDomain(): UserSetting {
    return UserSetting(
        language = runCatching { Language.valueOf(language.name) }.getOrDefault(Language.ENGLISH),
        theme = runCatching { Theme.valueOf(theme.name) }.getOrDefault(Theme.SYSTEM),
        temperature = runCatching { Temperature.valueOf(temperature.name) }.getOrDefault(Temperature.CELSIUS),
        windSpeedType = runCatching { SpeedType.valueOf(windSpeedType.name) }.getOrDefault(SpeedType.KMH)
    )
}

fun UserSetting.toData(): UserSettingModel {
    return UserSettingModel(
        language = runCatching { LanguageModel.valueOf(language.name) }.getOrDefault(LanguageModel.ENGLISH),
        theme = runCatching { ThemeModel.valueOf(theme.name) }.getOrDefault(ThemeModel.SYSTEM),
        temperature = runCatching { TemperatureModel.valueOf(temperature.name) }.getOrDefault(TemperatureModel.CELSIUS),
        windSpeedType = runCatching { SpeedTypeModel.valueOf(windSpeedType.name) }.getOrDefault(SpeedTypeModel.KMH)
    )
}

// ============== Weather Mappers ==============

fun CurrentWeatherModel.toDomain(): CurrentWeather =
    CurrentWeather(
        city = location.name,
        country = location.country,
        localTime = location.localTime,
        condition = mapToDomainCondition(current.condition.code, current.isDay == 1),
        tempC = current.tempC,
        tempF = current.tempF,
        windMph = current.windMph,
        windKph = current.windKph,
        humidity = current.humidity,
        rainMm = current.precipMm
    )

fun ForecastResponse.toDomain(): ForecastData =
    ForecastData(
        timeZoneId = location.tzId,
        days = forecast.forecastDay.map { it.toDomain() }
    )

fun ForecastDayDto.toDomain(): DayForecastWithHours =
    DayForecastWithHours(
        day = DayForecast(
            dateEpoch = dateEpoch,
            condition = mapToDomainCondition(day.condition.code, true),
            avgTempC = day.avgTempC,
            avgTempF = day.avgTempF
        ),
        hours = hourly.map { it.toDomainHour() }
    )

fun HourlyWeatherDto.toDomainHour(): HourForecast =
    HourForecast(
        timeEpoch = timeEpoch,
        condition = mapToDomainCondition(condition.code, isDay == 1),
        tempC = tempC,
        tempF = tempF,
        isDay = isDay == 1
    )

fun HistoryDayDto.toDomain(): DayForecast =
    DayForecast(
        dateEpoch = dateEpoch,
        condition = mapToDomainCondition(day.condition.code, true),
        avgTempC = day.avgTempC,
        avgTempF = day.avgTempF
    )

fun DaySummaryDto.toDomain(dateEpoch: Long): DayForecast =
    DayForecast(
        dateEpoch = dateEpoch,
        condition = mapToDomainCondition(condition.code, true),
        avgTempC = avgTempC,
        avgTempF = avgTempF
    )

// ============== Condition Mapper ==============

fun mapToDomainCondition(code: Int, isDay: Boolean): Condition =
    when (code) {
        1000 -> if (isDay) Condition.CLEAR_DAY else Condition.CLEAR_NIGHT
        in listOf(1003, 1006, 1009) -> Condition.CLOUDY
        in listOf(1030, 1135, 1147) -> Condition.FOG
        1063, in 1150..1201, in 1240..1246 -> Condition.RAIN
        1066, in 1114..1225, in 1255..1258 -> Condition.SNOW
        1069, 1072, 1237, in 1249..1264 -> Condition.ICE
        1087, in 1273..1282 -> Condition.THUNDER
        else -> Condition.UNKNOWN
    }
