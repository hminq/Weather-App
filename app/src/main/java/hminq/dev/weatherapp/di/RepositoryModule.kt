package hminq.dev.weatherapp.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import hminq.dev.weatherapp.data.repository.UserSettingRepositoryImpl
import hminq.dev.weatherapp.domain.repository.UserSettingRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindUserSettingRepository(
        impl: UserSettingRepositoryImpl
    ): UserSettingRepository
}