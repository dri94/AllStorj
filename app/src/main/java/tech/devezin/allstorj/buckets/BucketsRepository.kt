package tech.devezin.allstorj.buckets

import io.storj.Bucket
import tech.devezin.allstorj.data.sources.Result

interface BucketsRepository {
    fun getBuckets(): Result<List<Bucket>, Exception>
}
