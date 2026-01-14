package hminq.dev.weatherapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import hminq.dev.weatherapp.domain.repository.UserSettingRepository
import hminq.dev.weatherapp.domain.repository.WeatherRepository
import hminq.dev.weatherapp.domain.repository.LocationRepository
import hminq.dev.weatherapp.domain.usecase.GetCurrentLocation
import hminq.dev.weatherapp.domain.usecase.GetCurrentWeather
import hminq.dev.weatherapp.domain.usecase.GetHourlyForecast
import hminq.dev.weatherapp.domain.usecase.GetTomorrowForecast
import hminq.dev.weatherapp.domain.usecase.GetUserSetting
import hminq.dev.weatherapp.domain.usecase.GetWeekForecast
import hminq.dev.weatherapp.domain.usecase.SetUserSetting

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    fun provideGetUserSettingUseCase(
        userSettingRepository: UserSettingRepository
    ): GetUserSetting = GetUserSetting(userSettingRepository)

    @Provides
    fun provideSetUserSettingUseCase(
        userSettingRepository: UserSettingRepository
    ): SetUserSetting = SetUserSetting(userSettingRepository)

    @Provides
    fun provideGetCurrentWeatherUseCase(
        weatherRepository: WeatherRepository
    ): GetCurrentWeather = GetCurrentWeather(weatherRepository)

    @Provides
    fun provideGetHourlyForecastUseCase(
        weatherRepository: WeatherRepository
    ): GetHourlyForecast = GetHourlyForecast(weatherRepository)

    @Provides
    fun provideGetTomorrowForecastUseCase(
        weatherRepository: WeatherRepository
    ): GetTomorrowForecast = GetTomorrowForecast(weatherRepository)

    @Provides
    fun provideGetWeekForecastUseCase(
        weatherRepository: WeatherRepository
    ): GetWeekForecast = GetWeekForecast(weatherRepository)

    @Provides
    fun provideGetCurrentLocationUseCase(
        locationRepository: LocationRepository
    ): GetCurrentLocation = GetCurrentLocation(locationRepository)
}