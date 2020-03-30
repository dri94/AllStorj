package tech.devezin.allstorj.buckets.create

import io.storj.BucketCreateOption
import io.storj.BucketInfo
import kotlinx.coroutines.CoroutineDispatcher
import tech.devezin.allstorj.data.sources.BaseSource.Companion.executeCallOnDispatcher
import tech.devezin.allstorj.data.sources.BucketsSource
import tech.devezin.allstorj.data.sources.Result

interface CreateBucketRepository {
    suspend fun createBucket(bucketName: String, vararg options: BucketCreateOption): Result<BucketInfo, Exception>
}

class CreateBucketRepositoryImpl(private val bucketsSource: BucketsSource = BucketsSource()): CreateBucketRepository {
    override suspend fun createBucket(
        bucketName: String,
        vararg options: BucketCreateOption
    ): Result<BucketInfo, Exception> {
        return executeCallOnDispatcher {
            bucketsSource.createBucket(bucketName, *options)
        }
    }
}
