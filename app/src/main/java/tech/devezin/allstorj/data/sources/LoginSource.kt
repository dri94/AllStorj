package tech.devezin.allstorj.data.sources

import io.storj.*


class LoginSource : BaseSource() {

    suspend fun login(
        satelliteAddress: String,
        apiKey: String,
        encryptionAccess: String
    ): Result<Scope, Exception> {
        return executeCallOnDispatcher {
            val key = ApiKey.parse(apiKey)
            try {
                val project = Uplink().openProject(satelliteAddress, key)
                val saltedKey = Key.getSaltedKeyFromPassphrase(project, encryptionAccess)
                val access = EncryptionAccess(saltedKey)
                Success<Scope, Exception>(Scope(satelliteAddress, key, access))
            } catch (ex: StorjException) {
                Error<Scope, Exception>(ex)
            }
        }
    }
}
