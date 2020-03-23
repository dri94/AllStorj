package tech.devezin.allstorj.onboarding.login

import tech.devezin.allstorj.data.sources.*

interface LoginRepository {
    fun login(satelliteAddress: String, apiKey: String, encryptionAccess: String, cacheDir: String) : Result<Unit, Exception>
}

class LoginRepositoryImpl(private val loginSource: LoginSource = LoginSource(), private val localSource: LocalSource = LocalSourceImpl()): LoginRepository {
    override fun login(satelliteAddress: String, apiKey: String, encryptionAccess: String, cacheDir: String): Result<Unit, Exception> {
        return loginSource.login(satelliteAddress, apiKey, encryptionAccess).flatMap {
            localSource.saveScope(it.serialize())
            BaseSource.initSources(cacheDir)
            Success(Unit)
        }
    }
}
