package tech.devezin.allstorj.data.sources

import io.storj.*

class BucketsSource : BaseSource() {

    fun deleteBucket(name: String) : Result<Unit, Exception> {
        return try {
            Success(project.deleteBucket(name))
        } catch (ex: StorjException) {
            return Error(ex)
        }
    }

    fun getBucketInfo(name: String): Result<BucketInfo, Exception> {
        return try {
            Success(project.getBucketInfo(name))
        } catch (ex: StorjException) {
            return Error(ex)
        }
    }

    fun getBucket(name: String): Result<Bucket, Exception> {
        return try {
            Success(project.openBucket(name, scope))
        } catch (ex: StorjException) {
            return Error(ex)
        }
    }

    fun getBuckets(): Result<List<BucketInfo>, Exception> {
        return try {
            Success(project.listBuckets().toList())
        } catch (ex: StorjException) {
            Error(ex)
        }
    }

    fun createBucket(
        name: String,
        vararg options: BucketCreateOption
    ): Result<BucketInfo, Exception> {
        return try {
            Success(project.createBucket(name, *options))
        } catch (ex: StorjException) {
            Error(ex)
        }
    }
}
