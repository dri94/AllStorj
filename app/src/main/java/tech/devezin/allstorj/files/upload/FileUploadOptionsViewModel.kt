package tech.devezin.allstorj.files.upload

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.storj.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tech.devezin.allstorj.R
import tech.devezin.allstorj.utils.SingleLiveEvent
import tech.devezin.allstorj.utils.postEvent
import tech.devezin.allstorj.utils.setUpdate
import java.util.*


class FileUploadOptionsViewModel(
    private val bucketName: String,
    private val uri: Uri,
    private val contentResolver: ContentResolver,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val repository: FileUploadOptionsRepository = FileUploadOptionsRepositoryImpl()
) : ViewModel() {

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState> = _viewState
    private val _events = MutableLiveData<SingleLiveEvent<Events>>()
    val events: LiveData<SingleLiveEvent<Events>> = _events
    private var bucket: Bucket? = null
    private var selectedDate: Date? = null

    data class ViewState(val fileName: String, val mimeType: String, val showAdvancedOptions: Boolean, val error: String? = null)

    sealed class Events {
        object DismissDialogOnSuccess: Events()
    }

    init {
        loadData()
    }

    private fun loadData() = viewModelScope.launch {
        _viewState.value = ViewState(getFileName(), getMimeType(), false)
        repository.getBucket(bucketName).fold({
            bucket = it
        }, {

        })
    }

    private fun getMimeType(): String {
        return contentResolver.getType(uri) ?: ""
    }

    private fun getFileName(): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor.use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.path ?: ""
            val cut = result?.lastIndexOf('/')
            if (cut != null && cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result ?: ""
    }

    private fun buildEncryptionOption(@IdRes id: Int, blockSize: Int?): ObjectUploadOption? {
        return if (id != View.NO_ID || blockSize != null) {
            val encryptionParameters = EncryptionParameters.Builder()
            when (id) {
                R.id.createBucketEncryptionCipherChipNone -> CipherSuite.NONE
                R.id.createBucketEncryptionCipherChipAes -> CipherSuite.AESGCM
                R.id.createBucketEncryptionCipherChipSecretBox -> CipherSuite.SECRET_BOX
                else -> null
            }?.let {
                encryptionParameters.setCipher(it)
            }
            blockSize?.let {
                encryptionParameters.setBlockSize(it)
            }
            return ObjectUploadOption.encryptionParameters(encryptionParameters.build())
        } else null
    }

    private fun buildDateObjectUploadOption(date: Date?): ObjectUploadOption? {
        return date?.let {
            return ObjectUploadOption.expires(date)
        }
    }

    private fun buildRedundancyOption(
        totalShares: Short?,
        successShares: Short?,
        shareSize: Int?,
        requiredShares: Short?,
        repairShares: Short?
    ): ObjectUploadOption? {
        return if (totalShares != null || successShares != null || shareSize != null || requiredShares != null || repairShares != null) {
            val redundancyScheme = RedundancyScheme.Builder()
            totalShares?.let {
                redundancyScheme.setTotalShares(it)
            }
            successShares?.let {
                redundancyScheme.setSuccessShares(it)
            }
            shareSize?.let {
                redundancyScheme.setShareSize(it)
            }
            requiredShares?.let {
                redundancyScheme.setRequiredShares(it)
            }
            repairShares?.let {
                redundancyScheme.setRepairShares(it)
            }
            ObjectUploadOption.redundancyScheme(redundancyScheme.build())
        } else null
    }

    fun onExpirationDateSelected(date: Date) {
        selectedDate = date
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    fun onCreateFileClicked(
        path: String,
        mimeType: String,
        blockSizeString: String,
        @IdRes encryptionChipId: Int,
        repairSharesString: String,
        requiredSharesString: String,
        shareSizeString: String,
        successSharesString: String,
        totalSharesString: String
    ) = this.viewModelScope.launch(ioDispatcher) {
        val inputStream = contentResolver.openInputStream(uri)
        val currentBucket = bucket
        if (currentBucket != null && inputStream != null) {
            val params = mutableListOf<ObjectUploadOption>()

            buildRedundancyOption(
                totalShares = totalSharesString.toShortOrNull(),
                successShares = successSharesString.toShortOrNull(),
                shareSize = shareSizeString.toIntOrNull(),
                repairShares = repairSharesString.toShortOrNull(),
                requiredShares = requiredSharesString.toShortOrNull()
            )?.let {
                params.add(it)
            }

            buildDateObjectUploadOption(selectedDate)?.let {
                params.add(it)
            }

            buildEncryptionOption(encryptionChipId, blockSizeString.toIntOrNull())?.let {
                params.add(it)
            }

            if (mimeType.isNotEmpty()) {
                params.add(ObjectUploadOption.contentType(mimeType))
            }

            repository.uploadFile(currentBucket, formatPath(path), inputStream, *params.toTypedArray()).fold({
                _events.postEvent(Events.DismissDialogOnSuccess)
            }, {
                Log.e("", it.message)
            })
        }

    }

    fun onAdvancedOptionsClick() {
        _viewState.setUpdate {
            it.copy(showAdvancedOptions = !it.showAdvancedOptions)
        }
    }

    private fun formatPath(input: String) : String {
        var path = input
        if (path.isNotEmpty() && path[path.lastIndex] != '/') {
            path += "/"
        }
        return path + viewState.value?.fileName
    }
}
