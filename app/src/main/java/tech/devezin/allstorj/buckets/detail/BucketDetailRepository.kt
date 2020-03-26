package tech.devezin.allstorj.buckets.detail

import io.storj.BucketInfo
import tech.devezin.allstorj.data.sources.BucketsSource
import tech.devezin.allstorj.data.sources.Result

interface BucketDetailRepository {
    fun deleteBucket(name: String): Result<Unit, Exception>
    fun getBucket(name: String): Result<BucketInfo, Exception>
}

class BucketDetailRepositoryImpl(private val bucketsSource: BucketsSource = BucketsSource()): BucketDetailRepository {
    override fun deleteBucket(name: String): Result<Unit, Exception> {
        return bucketsSource.deleteBucket(name)
    }

    override fun getBucket(name: String): Result<BucketInfo, Exception> {
        return bucketsSource.getBucketInfo(name)
    }
}
