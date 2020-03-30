package tech.devezin.allstorj.data.sources

import io.storj.Bucket
import io.storj.StorjException
import java.io.InputStream

class FilesSource : BaseSource() {

    fun createFile(
        bucket: Bucket,
        filePath: String,
        inputStream: InputStream
    ): Result<Unit, Exception> {
        return try {
            Success(bucket.uploadObject(filePath, inputStream))
        } catch (ex: StorjException) {
            Error(ex)
        }
    }
}
