package hminq.dev.weatherapp.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import hminq.dev.weatherapp.data.repository.LocationManager
import hminq.dev.weatherapp.data.repository.UserSettingRepositoryImpl
import hminq.dev.weatherapp.data.repository.WeatherRepositoryImpl
import hminq.dev.weatherapp.domain.repository.LocationRepository
import hminq.dev.weatherapp.domain.repository.UserSettingRepository
import hminq.dev.weatherapp.domain.repository.WeatherRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindUserSettingRepository(
        impl: UserSettingRepositoryImpl
    ): UserSettingRepository

    @Binds
    abstract fun bindWeatherRepository(
        impl: WeatherRepositoryImpl
    ): WeatherRepository

    @Binds
    abstract fun bindLocationRepository(
        impl: LocationManager
    ): LocationRepository
}