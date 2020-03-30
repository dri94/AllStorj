package tech.devezin.allstorj.files

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.storj.Bucket
import io.storj.ObjectInfo
import kotlinx.coroutines.launch
import tech.devezin.allstorj.R
import tech.devezin.allstorj.utils.SingleLiveEvent
import tech.devezin.allstorj.utils.setEvent
import java.io.InputStream
import java.text.SimpleDateFormat

class FilesViewModel(
    private val bucketName: String,
    private val repo: FilesRepository = FilesRepositoryImpl()
) : ViewModel(), FilesAdapter.FileClickListener {

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState> = _viewState
    private val _events = MutableLiveData<SingleLiveEvent<Events>>()
    val events: LiveData<SingleLiveEvent<Events>> = _events
    private var listObjects: List<ObjectInfo> = listOf()
    private var bucket: Bucket? = null

    sealed class Events {
        object ShowNewFileDialog : Events()
        class ShowFileDetailBottomSheet(val info: ObjectInfo) : Events()
        class ShowFileMenuBottomSheet(val info: ObjectInfo) : Events()
        object OpenSystemFilePicker : Events()
    }

    data class ViewState(val files: List<FilePresentable> = listOf())

    init {
        getFiles()
    }

    private fun getFiles() = this.viewModelScope.launch {
        repo.getBucket(bucketName).fold({
            bucket = it
            listObjects = it.listObjects().toList()
            _viewState.value = ViewState(listObjects.toFilePresentables())
        }, {

        })
    }

    override fun onClick(presentable: FilePresentable) {
        listObjects.find {
            it.path == presentable.path
        }?.let {
            _events.setEvent(Events.ShowFileDetailBottomSheet(it))
        }
    }

    override fun onMenuClick(presentable: FilePresentable) {
        listObjects.find {
            it.path == presentable.path
        }?.let {
            _events.setEvent(Events.ShowFileMenuBottomSheet(it))
        }
    }

    fun onCreateFileClicked() {
        _events.setEvent(Events.ShowNewFileDialog)
    }

    fun onFileChosen(filePath: String, inputStream: InputStream) = this.viewModelScope.launch {
        bucket?.let {
            repo.createFile(it, filePath, inputStream)
        }
    }
}

fun ObjectInfo.getName(): String {
    return path.substring(path.lastIndexOf('\\') + 1)
}

fun ObjectInfo.getModifiedFormattedDate(): String {
    val formatter = SimpleDateFormat("mm/dd/yyyy")
    return formatter.format(modified)
}

fun ObjectInfo.getMimeTypeDrawable(): Int {
    return when  {
        contentType.startsWith("image") -> R.drawable.ic_file_image
        else -> R.drawable.ic_file
    }
}

fun ObjectInfo.toFilePresentable(): FilePresentable {
    val size = size / (1024*1024).toDouble()
    return FilePresentable(name = getName(), path = path, drawableRes = getMimeTypeDrawable() , description = "$size MB, Modified: ${getModifiedFormattedDate()}")
}

fun List<ObjectInfo>.toFilePresentables(): List<FilePresentable> {
    return map {
        it.toFilePresentable()
    }
}
