package tech.devezin.allstorj.buckets.create

import io.storj.BucketCreateOption
import io.storj.BucketInfo
import io.storj.EncryptionParameters
import kotlinx.coroutines.CoroutineDispatcher
import tech.devezin.allstorj.data.sources.BucketsSource
import tech.devezin.allstorj.data.sources.Result

interface CreateBucketRepository {
    fun createBucket(bucketName: String, vararg options: BucketCreateOption): Result<BucketInfo, Exception>
}

class CreateBucketRepositoryImpl(private val bucketsSource: BucketsSource = BucketsSource()): CreateBucketRepository {
    override fun createBucket(
        bucketName: String,
        vararg options: BucketCreateOption
    ): Result<BucketInfo, Exception> {
        return bucketsSource.createBucket(bucketName, *options)
    }
}
