package tech.devezin.allstorj.files

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import io.storj.Bucket

class FilesPagingDataSourceFactory (private val bucket: Bucket, var cursor: String? = null): DataSource.Factory<String, FilePresentable>() {
    val sourceLiveData = MutableLiveData<FilesPagingDataSource>()
    private var latestSource: FilesPagingDataSource? = null
    override fun create(): DataSource<String, FilePresentable> {
        latestSource = FilesPagingDataSource(bucket, cursor)
        sourceLiveData.postValue(latestSource)
        return latestSource!!
    }
}
