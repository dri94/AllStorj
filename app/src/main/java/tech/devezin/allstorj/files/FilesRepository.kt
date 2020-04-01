package tech.devezin.allstorj.files

import io.storj.Bucket
import tech.devezin.allstorj.data.sources.BaseSource.Companion.executeCallOnDispatcher
import tech.devezin.allstorj.data.sources.BucketsSource
import tech.devezin.allstorj.data.sources.FilesSource
import tech.devezin.allstorj.data.sources.Result
import java.io.InputStream

interface FilesRepository {
    suspend fun getBucket(name: String): Result<Bucket, Exception>
}

class FilesRepositoryImpl(
    private val bucketsSource: BucketsSource = BucketsSource(),
    private val filesSource: FilesSource = FilesSource()
): FilesRepository {

    override suspend fun getBucket(name: String): Result<Bucket, Exception> {
        return executeCallOnDispatcher {
            bucketsSource.getBucket(name)
        }
    }
}
