package tech.devezin.allstorj.files.upload

import io.storj.Bucket
import io.storj.ObjectUploadOption
import tech.devezin.allstorj.data.sources.BaseSource
import tech.devezin.allstorj.data.sources.BaseSource.Companion.executeCallOnDispatcher
import tech.devezin.allstorj.data.sources.BucketsSource
import tech.devezin.allstorj.data.sources.FilesSource
import tech.devezin.allstorj.data.sources.Result
import java.io.InputStream

interface FileUploadOptionsRepository {
    suspend fun uploadFile(bucket: Bucket, filePath: String, inputStream: InputStream, vararg options: ObjectUploadOption): Result<Unit, Exception>
    suspend fun getBucket(name: String) : Result<Bucket, Exception>
}

class FileUploadOptionsRepositoryImpl(private val filesSource: FilesSource = FilesSource(), private val bucketsSource: BucketsSource = BucketsSource()): FileUploadOptionsRepository {
    override suspend fun uploadFile(bucket: Bucket, filePath: String, inputStream: InputStream, vararg options: ObjectUploadOption): Result<Unit, Exception> {
        return executeCallOnDispatcher {
            filesSource.uploadFile(bucket, filePath, inputStream, *options)
        }
    }

    override suspend fun getBucket(name: String): Result<Bucket, Exception> {
        return executeCallOnDispatcher {
            bucketsSource.getBucket(name)
        }
    }
}
