package tech.devezin.allstorj.buckets

import io.storj.Bucket
import io.storj.BucketInfo
import kotlinx.coroutines.CompletableDeferred
import tech.devezin.allstorj.data.sources.BucketsSource
import tech.devezin.allstorj.data.sources.Result

interface BucketsRepository {
    suspend fun getBuckets(): Result<List<BucketInfo>, Exception>
}

class BucketsRepositoryImpl(private val bucketsSource: BucketsSource = BucketsSource()): BucketsRepository {

    override suspend fun getBuckets(): Result<List<BucketInfo>, Exception> {
        return bucketsSource.getBuckets()
    }
}
