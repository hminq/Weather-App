package hminq.dev.weatherapp.domain.usecase

import hminq.dev.weatherapp.domain.entity.UserSetting
import hminq.dev.weatherapp.domain.repository.UserSettingRepository
import jakarta.inject.Inject

class SetUserSetting @Inject constructor(
    val userSettingRepository: UserSettingRepository
) {
    suspend fun invoke(userSetting: UserSetting) {
        userSettingRepository.saveUserSetting(userSetting)
    }
}