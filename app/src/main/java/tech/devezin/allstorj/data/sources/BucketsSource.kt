package tech.devezin.allstorj.data.sources

import io.storj.*

class BucketsSource : BaseSource() {

    fun getBuckets(): Result<List<BucketInfo>, Exception> {
        return try {
            Success(project.listBuckets().toList())
        } catch (ex: StorjException) {
            Error(ex)
        }
    }

    fun createBucket(name: String, vararg options: BucketCreateOption): Result<BucketInfo, Exception> {
        return try {
            Success(
                project.createBucket(
                    name,
                    *options
                )
            )
        } catch (ex: StorjException) {
            Error(ex)
        }
    }
}
