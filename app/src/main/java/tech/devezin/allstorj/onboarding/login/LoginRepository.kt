package tech.devezin.allstorj.onboarding.login

import io.storj.StorjException
import tech.devezin.allstorj.data.sources.*

interface LoginRepository {
    fun login(satelliteAddress: String, apiKey: String, encryptionAccess: String, cacheDir: String) : Result<Unit, Exception>
    fun checkLogin(cacheDir: String): Result<Unit, Exception>
}

class LoginRepositoryImpl(private val loginSource: LoginSource = LoginSource(), private val localSource: LocalSource = LocalSourceImpl()): LoginRepository {
    override fun login(satelliteAddress: String, apiKey: String, encryptionAccess: String, cacheDir: String): Result<Unit, Exception> {
        return loginSource.login(satelliteAddress, apiKey, encryptionAccess).flatMap {
            try {
                localSource.saveScope(it.serialize())
                BaseSource.initSources(cacheDir, localSource)
                Success<Unit, Exception>(Unit)
            } catch (ex: StorjException) {
                Error<Unit, Exception>(ex)
            }
        }
    }

    override fun checkLogin(cacheDir: String): Result<Unit, Exception> {
        return try {
            BaseSource.initSources(cacheDir, localSource)
            Success(Unit)
        }catch (ex: StorjException) {
            Error(ex)
        }
    }
}
