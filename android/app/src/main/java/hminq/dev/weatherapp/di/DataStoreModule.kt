package hminq.dev.weatherapp.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import hminq.dev.weatherapp.data.local.datastore.UserSettingDataSource
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Singleton
    @Provides
    fun provideSettingDataStore(
        @ApplicationContext context: Context
    ): UserSettingDataSource {
        return UserSettingDataSource(context)
    }
}