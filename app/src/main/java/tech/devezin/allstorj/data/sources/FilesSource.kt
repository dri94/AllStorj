package tech.devezin.allstorj.data.sources

import io.storj.Bucket
import io.storj.ObjectInfo
import io.storj.ObjectUploadOption
import io.storj.StorjException
import java.io.InputStream

class FilesSource : BaseSource() {

    fun uploadFile(
        bucket: Bucket,
        filePath: String,
        inputStream: InputStream,
        vararg options: ObjectUploadOption
    ): Result<Unit, Exception> {
        return try {
            Success(bucket.uploadObject(filePath, inputStream, *options))
        } catch (ex: StorjException) {
            Error(ex)
        }
    }
}
