package tech.devezin.allstorj.buckets.detail

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.storj.CipherSuite
import kotlinx.coroutines.launch
import tech.devezin.allstorj.R
import tech.devezin.allstorj.buckets.BucketPresentable
import tech.devezin.allstorj.buckets.getFormattedDate
import tech.devezin.allstorj.utils.SingleLiveEvent
import tech.devezin.allstorj.utils.setEvent
import tech.devezin.allstorj.utils.setUpdate

class BucketDetailViewModel(
    private val bucketPresentable: BucketPresentable,
    private val repo: BucketDetailRepository = BucketDetailRepositoryImpl()
) : ViewModel() {

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState> = _viewState
    private val _events = MutableLiveData<SingleLiveEvent<Events>>()
    val events: LiveData<SingleLiveEvent<Events>> = _events

    data class ViewState(
        val name: String = "",
        val created: String = "",
        val encryptionArgs: Array<Any>? = null,
        @StringRes val pathCipher: Int? = null,
        val segment: String = "",
        val redundancy: Array<Any>? = null,
        val error: String? = null
    )

    sealed class Events {
        object GoToBucketList : Events()
    }

    init {
        getBucketInfo()
    }

    private fun getBucketInfo() = this.viewModelScope.launch {
        repo.getBucket(bucketPresentable.name).fold({
            _viewState.value = ViewState(
                name = it.name,
                created = it.getFormattedDate(),
                encryptionArgs = arrayOf(
                    getEncryptionCipherStringRes(it.encryptionParameters.cipher),
                    it.encryptionParameters.blockSize
                ),
                segment = it.segmentsSize.toString(),
                pathCipher = getEncryptionCipherStringRes(it.pathCipher),
                redundancy = arrayOf(
                    it.redundancyScheme.repairThreshold,
                    it.redundancyScheme.requiredShares,
                    it.redundancyScheme.successShares,
                    it.redundancyScheme.totalShares,
                    it.redundancyScheme.shareSize
                )
            )
        }, {
            _viewState.value = ViewState(error = it.localizedMessage)
        })
    }

    private fun getEncryptionCipherStringRes(cipher: CipherSuite): Int {
        return when (cipher) {
            CipherSuite.SECRET_BOX -> R.string.encryption_secret_box
            CipherSuite.AESGCM -> R.string.encryption_aes
            else -> R.string.encryption_none
        }
    }
}
