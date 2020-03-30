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
    private val ioDispatcher: CoroutineDispatcher,
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
                BaseSource.initSources(cacheDir, localSource, ioDispatcher)
                Success<Unit, Exception>(Unit)
            } catch (ex: StorjException) {
                Error<Unit, Exception>(ex)
            }
        }
    }

    override suspend fun checkLogin(cacheDir: String): Result<Unit, Exception> {
        return try {
            BaseSource.initSources(cacheDir, localSource, ioDispatcher)
            Success(Unit)
        } catch (ex: StorjException) {
            Error(ex)
        }
    }
}
