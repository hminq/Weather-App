package hminq.dev.weatherapp.domain.repository

import hminq.dev.weatherapp.domain.entity.UserSetting
import kotlinx.coroutines.flow.Flow

interface UserSettingRepository {
    suspend fun saveUserSetting(userSetting: UserSetting)
    suspend fun getUserSetting(): Flow<UserSetting>
}