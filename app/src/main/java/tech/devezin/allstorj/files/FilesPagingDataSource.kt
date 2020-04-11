package tech.devezin.allstorj.files

import android.util.Log
import androidx.paging.ItemKeyedDataSource
import io.storj.Bucket
import io.storj.ObjectInfo
import io.storj.ObjectListOption
import io.storj.StorjException

class FilesPagingDataSource(private val bucket: Bucket, private val path: String?) : ItemKeyedDataSource<String, FilePresentable>() {

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<FilePresentable>) {
        val items = try {
            val options = mutableListOf(ObjectListOption.pageSize(PAGE_SIZE))
            path?.let {
                options.add(ObjectListOption.prefix(path.removeSuffix("/")))
            }
            bucket.listObjects(*options.toTypedArray()).toList()
        } catch (ex: StorjException) {
            Log.e(LOG_TAG, ex.localizedMessage)
            listOf<ObjectInfo>()
        }.toFilePresentables()
        callback.onResult(items)
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<FilePresentable>) {
        var items = try {
            bucket.listObjects(ObjectListOption.pageSize(PAGE_SIZE), ObjectListOption.cursor(params.key)).toList()
        } catch (ex: StorjException) {
            Log.e(LOG_TAG, ex.localizedMessage)
            listOf<ObjectInfo>()
        }.toFilePresentables()
        if (items.isNotEmpty()) {
            val lastIndex = items.lastIndex
            items = items.subList(1, items.size)
        }
        callback.onResult(items)
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<FilePresentable>) {
    }

    override fun getKey(item: FilePresentable): String {
        return item.path
    }

    companion object {
        const val PAGE_SIZE = 100
        private val LOG_TAG = FilesPagingDataSource::class.java.simpleName
    }
}
