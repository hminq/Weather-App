package hminq.dev.weatherapp.domain.usecase

import hminq.dev.weatherapp.domain.entity.UserSetting
import hminq.dev.weatherapp.domain.repository.UserSettingRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetUserSetting @Inject constructor(
    val userSettingRepository: UserSettingRepository
) {
    suspend fun invoke(): Flow<UserSetting> {
        return userSettingRepository.getUserSetting()
    }
}