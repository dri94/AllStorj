package tech.devezin.allstorj.files

import android.net.Uri
import androidx.lifecycle.*
import androidx.paging.PagedList
import androidx.paging.toLiveData
import io.storj.Bucket
import io.storj.ObjectInfo
import io.storj.ObjectListOption
import kotlinx.coroutines.launch
import tech.devezin.allstorj.R
import tech.devezin.allstorj.utils.SingleLiveEvent
import tech.devezin.allstorj.utils.setEvent
import tech.devezin.allstorj.utils.setUpdate
import java.text.CharacterIterator
import java.text.SimpleDateFormat
import java.text.StringCharacterIterator

class FilesViewModel(
    private val bucketName: String,
    private val repo: FilesRepository = FilesRepositoryImpl()
) : ViewModel(), FilesPagingAdapter.FileClickListener {

    private var dataSourceFactory: FilesPagingDataSourceFactory? = null
    private var dataSourceLiveData: LiveData<PagedList<FilePresentable>>? = null
    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState> = _viewState
    private val _events = MutableLiveData<SingleLiveEvent<Events>>()
    val events: LiveData<SingleLiveEvent<Events>> = _events
    private var listObjects: List<ObjectInfo> = listOf()
    private var bucket: Bucket? = null
    private val pagedObserver = Observer<PagedList<FilePresentable>> { list ->
        _viewState.setUpdate {
            it.copy(isLoading = false, items = list)
        }
    }


    sealed class Events {
        class ShowNewFileDialog(val bucketName: String, val uri: Uri) : Events()
        class ShowFileDetailBottomSheet(val info: ObjectInfo) : Events()
        class ShowFileMenuBottomSheet(val info: ObjectInfo) : Events()
        object OpenSystemFilePicker : Events()
    }

    data class ViewState(val isLoading: Boolean, val items: PagedList<FilePresentable>? = null)

    init {
        _viewState.value = (ViewState(true))
        getFiles()
    }

    override fun onCleared() {
        super.onCleared()
        dataSourceLiveData?.removeObserver(pagedObserver)
    }

    private fun getFiles() = this.viewModelScope.launch {
        _viewState.setUpdate {
            it.copy(isLoading = true)
        }
        repo.getBucket(bucketName).fold({
            bucket = it
            dataSourceFactory = FilesPagingDataSourceFactory(it)
            dataSourceLiveData = dataSourceFactory?.toLiveData(FilesPagingDataSource.PAGE_SIZE)
            dataSourceLiveData?.observeForever(pagedObserver)
        }, {
            _viewState.setUpdate {
                it.copy(isLoading = false)
            }
        })
    }

    fun onRefresh() {
        invalidateDataSource()
    }

    private fun invalidateDataSource() = dataSourceFactory?.sourceLiveData?.value?.invalidate()

    override fun onClick(presentable: FilePresentable) {
        if (presentable.isPrefix) {
            dataSourceFactory?.cursor = presentable.path
            invalidateDataSource()
        } else {
            listObjects.find {
                it.path == presentable.name
            }?.let {
                _events.setEvent(Events.ShowFileDetailBottomSheet(it))
            }
        }
    }

    override fun onMenuClick(presentable: FilePresentable) {
        listObjects.find {
            it.getName() == presentable.name
        }?.let {
            _events.setEvent(Events.ShowFileMenuBottomSheet(it))
        }
    }

    fun onCreateFileClicked() {
        _events.setEvent(Events.OpenSystemFilePicker)
    }

    fun onFileChosen(uri: Uri) {
        bucket?.let {
            _events.setEvent(Events.ShowNewFileDialog(it.name, uri))
        }
    }
}

fun ObjectInfo.getName(): String {
    return path.substring(path.lastIndexOf('\\') + 1)
}

fun ObjectInfo.getModifiedFormattedDate(): String {
    val formatter = SimpleDateFormat("MM/dd/yyyy")
    return formatter.format(modified)
}

fun ObjectInfo.getMimeTypeDrawable(): Int {
    return when {
        contentType.startsWith("image") -> R.drawable.ic_file_image
        else -> R.drawable.ic_file
    }
}

fun ObjectInfo.toFilePresentable(): FilePresentable {
    val size = humanReadableByteCountSI(size)
    return FilePresentable(name = getName(), path = path, isPrefix = isPrefix, drawableRes = getMimeTypeDrawable(), description = "$size, Modified: ${getModifiedFormattedDate()}")
}

private fun humanReadableByteCountSI(bytes: Long): String? {
    var bytes = bytes
    if (-1000 < bytes && bytes < 1000) {
        return "$bytes B"
    }
    val ci: CharacterIterator = StringCharacterIterator("kMGTPE")
    while (bytes <= -999950 || bytes >= 999950) {
        bytes /= 1000
        ci.next()
    }
    return java.lang.String.format("%.1f %cB", bytes / 1000.0, ci.current())
}

fun List<ObjectInfo>.toFilePresentables(): List<FilePresentable> {
    return map {
        it.toFilePresentable()
    }
}
