package tech.devezin.allstorj.data.sources

import io.storj.*


class LoginSource : BaseSource() {

    fun login(
        satelliteAddress: String,
        apiKey: String,
        encryptionAccess: String
    ): Result<Scope, Exception> {
        val key = ApiKey.parse(apiKey)
        return try {
            val project = Uplink().openProject(satelliteAddress, key)
            val saltedKey = Key.getSaltedKeyFromPassphrase(project, encryptionAccess)
            val access = EncryptionAccess(saltedKey)
            Success(Scope(satelliteAddress, key, access))
        } catch (ex: StorjException) {
            return Error(ex)
        }
    }
}
