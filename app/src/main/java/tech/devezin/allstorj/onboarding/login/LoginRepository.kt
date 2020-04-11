package tech.devezin.allstorj.onboarding.login

import io.storj.StorjException
import kotlinx.coroutines.CoroutineDispatcher
import tech.devezin.allstorj.data.sources.*

interface LoginRepository {
    suspend fun login(
        satelliteAddress: String,
        apiKey: String,
        encryptionAccess: String,
        cacheDir: String
    ): Result<Unit, Exception>

    suspend fun checkLogin(cacheDir: String): Result<Unit, Exception>
}

class LoginRepositoryImpl(
    private val loginSource: LoginSource = LoginSource(),
    private val localSource: LocalSource = LocalSourceImpl()
) : LoginRepository {
    override suspend fun login(
        satelliteAddress: String,
        apiKey: String,
        encryptionAccess: String,
        cacheDir: String
    ): Result<Unit, Exception> {
        return loginSource.login(satelliteAddress, apiKey, encryptionAccess).flatMap {
            try {
                localSource.saveScope(it.serialize())
                BaseSource.initStorj(cacheDir, localSource)
                Success<Unit, Exception>(Unit)
            } catch (ex: StorjException) {
                Error<Unit, Exception>(ex)
            }
        }
    }

    override suspend fun checkLogin(cacheDir: String): Result<Unit, Exception> {
        return try {
            BaseSource.initStorj(cacheDir, localSource)
            Success(Unit)
        } catch (ex: StorjException) {
            Error(ex)
        }
    }
}
