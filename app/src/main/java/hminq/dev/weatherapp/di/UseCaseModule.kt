package hminq.dev.weatherapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import hminq.dev.weatherapp.domain.repository.UserSettingRepository
import hminq.dev.weatherapp.domain.usecase.GetUserSetting
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
}