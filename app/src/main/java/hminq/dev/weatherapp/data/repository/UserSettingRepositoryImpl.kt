package hminq.dev.weatherapp.data.repository

import hminq.dev.weatherapp.R
import hminq.dev.weatherapp.data.local.datastore.UserSettingDataSource
import hminq.dev.weatherapp.data.mapper.toData
import hminq.dev.weatherapp.data.mapper.toDomain
import hminq.dev.weatherapp.domain.entity.UserSetting
import hminq.dev.weatherapp.domain.exception.LocalStorageException
import hminq.dev.weatherapp.domain.repository.UserSettingRepository
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.IOException

@Singleton
class UserSettingRepositoryImpl @Inject constructor(
    val dataSource: UserSettingDataSource
) : UserSettingRepository {
    private val saveDataErrorRes: Int = R.string.save_data_err

    override suspend fun saveUserSetting(userSetting: UserSetting) {
        val userSettingModel = userSetting.toData()

        try {
            dataSource.save(userSettingModel)
        } catch (e: IOException) {
            throw LocalStorageException(e.message, e, saveDataErrorRes)
        } catch (ex: Exception) {
            throw LocalStorageException(ex.message, ex, saveDataErrorRes)
        }
    }

    override suspend fun getUserSetting(): Flow<UserSetting> {
        return dataSource.get()
            .map { model ->
                model.toDomain()
            }
    }
}