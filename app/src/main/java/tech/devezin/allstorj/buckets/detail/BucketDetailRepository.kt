package tech.devezin.allstorj.buckets.detail

import io.storj.BucketInfo
import kotlinx.coroutines.CoroutineDispatcher
import tech.devezin.allstorj.data.sources.BaseSource.Companion.executeCallOnDispatcher
import tech.devezin.allstorj.data.sources.BucketsSource
import tech.devezin.allstorj.data.sources.Result

interface BucketDetailRepository {
    suspend fun getBucket(name: String): Result<BucketInfo, Exception>
}

class BucketDetailRepositoryImpl(private val bucketsSource: BucketsSource = BucketsSource()): BucketDetailRepository {

    override suspend fun getBucket(name: String): Result<BucketInfo, Exception> {
        return executeCallOnDispatcher {
            bucketsSource.getBucketInfo(name)
        }
    }
}
