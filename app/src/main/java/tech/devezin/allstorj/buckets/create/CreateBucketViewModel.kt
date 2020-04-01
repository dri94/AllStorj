package tech.devezin.allstorj.buckets.create

import android.view.View
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
import tech.devezin.allstorj.utils.setEvent
import tech.devezin.allstorj.utils.setUpdate

class CreateBucketViewModel(private val repo: CreateBucketRepository = CreateBucketRepositoryImpl()) :
    ViewModel() {

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState> = _viewState
    private val _events = MutableLiveData<SingleLiveEvent<Events>>()
    val events: LiveData<SingleLiveEvent<Events>> = _events

    data class ViewState(val error: String? = null, val showAdvancedOptions: Boolean)

    sealed class Events {
        class GoToBucketsList(val info: BucketInfo) : Events()
    }

    init {
        _viewState.value = ViewState(showAdvancedOptions = false)
    }

    private fun buildEncryptionOption(@IdRes id: Int, blockSize: Int?): BucketCreateOption? {
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
            return BucketCreateOption.encryptionParameters(encryptionParameters.build())
        } else null
    }

    private fun buildPathCipherOption(@IdRes id: Int): BucketCreateOption? {
        return when (id) {
            R.id.createBucketPathCipherChipNone -> CipherSuite.NONE
            R.id.createBucketPathCipherChipAes -> CipherSuite.AESGCM
            R.id.createBucketPathCipherChipSecretBox -> CipherSuite.SECRET_BOX
            else -> null
        }?.let {
            BucketCreateOption.pathCipher(it)
        }
    }

    private fun buildRedundancyOption(
        totalShares: Short?,
        successShares: Short?,
        shareSize: Int?,
        requiredShares: Short?,
        repairShares: Short?
    ): BucketCreateOption? {
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
            BucketCreateOption.redundancyScheme(redundancyScheme.build())
        } else null
    }

    fun onAdvancedOptionsClicked() {
        _viewState.setUpdate {
            it.copy(showAdvancedOptions = !it.showAdvancedOptions)
        }
    }

    fun onCreateGroupClicked(
        bucketName: String,
        @IdRes cipherChipId: Int,
        segmentSizeString: String,
        blockSizeString: String,
        @IdRes encryptionChipId: Int,
        repairSharesString: String,
        requiredSharesString: String,
        shareSizeString: String,
        successSharesString: String,
        totalSharesString: String
    ) {
        if (bucketName.isEmpty()) {
            _viewState.setUpdate {
                it.copy(error = "Bucket name must not be empty")
            }
            return
        }
        val params = mutableListOf<BucketCreateOption>()
        buildPathCipherOption(cipherChipId)?.let {
            params.add(it)
        }
        buildEncryptionOption(encryptionChipId, blockSizeString.toIntOrNull())?.let {
            params.add(it)
        }
        buildRedundancyOption(
            totalShares = totalSharesString.toShortOrNull(),
            successShares = successSharesString.toShortOrNull(),
            shareSize = shareSizeString.toIntOrNull(),
            requiredShares = requiredSharesString.toShortOrNull(),
            repairShares = repairSharesString.toShortOrNull()
        )?.let {
            params.add(it)
        }
        segmentSizeString.toLongOrNull()?.let {
            params.add(BucketCreateOption.segmentsSize(it))
        }
        val formattedBucketName: String = if (bucketName[0].isUpperCase()) {
            bucketName[0].toLowerCase() + bucketName.substring(1)
        } else bucketName
        this.viewModelScope.launch {
            repo.createBucket(formattedBucketName, *params.toTypedArray()).fold({
                _events.setEvent(Events.GoToBucketsList(it))
            }, {
                _viewState.setUpdate { viewState ->
                    viewState.copy(error = it.localizedMessage)
                }
            })
        }
    }
}
