package tech.devezin.allstorj.buckets

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.storj.BucketInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tech.devezin.allstorj.utils.SingleLiveEvent
import tech.devezin.allstorj.utils.postUpdate
import tech.devezin.allstorj.utils.setEvent
import tech.devezin.allstorj.utils.setUpdate
import java.text.SimpleDateFormat

class BucketsViewModel(private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO, private val repo: BucketsRepository = BucketsRepositoryImpl()): ViewModel(), BucketsAdapter.BucketClickListener {

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState> = _viewState
    private val _events = MutableLiveData<SingleLiveEvent<Events>>()
    val events: LiveData<SingleLiveEvent<Events>> = _events

    data class ViewState(val isLoading: Boolean, val buckets: List<BucketPresentable> = listOf(), val error: String? = null)


    sealed class Events {
        class GoToBucketScreen(val bucketPresentable: BucketPresentable) : Events()
        class ShowBucketDetailDialog(val bucket: BucketPresentable) : Events()
    }

    init {
        _viewState.value = ViewState(true)
        getBuckets()
    }

    fun onRefresh() {
        getBuckets()
    }

    override fun onBucketClick(bucketPresentable: BucketPresentable) {
        _events.setEvent(Events.GoToBucketScreen(bucketPresentable))
    }

    override fun onBucketMenuClick(bucketPresentable: BucketPresentable) {
        _events.setEvent(Events.ShowBucketDetailDialog(bucketPresentable))
    }

    private fun getBuckets() = viewModelScope.launch(ioDispatcher) {
        repo.getBuckets().fold({ bucketInfos ->
            val presentables = bucketInfos.toBucketPresentables()
            _viewState.postUpdate {
                it.copy(isLoading = false, buckets = presentables, error = null)
            }
        }, { exception ->
            _viewState.postUpdate {
                it.copy(isLoading = false, error = exception.localizedMessage)
            }
        })
    }
}

fun List<BucketInfo>.toBucketPresentables(): List<BucketPresentable> {
    return map {
        it.toBucketPresentable()
    }
}

fun BucketInfo.getFormattedDate(): String {
    val formatter = SimpleDateFormat("mm/dd/yyyy")
    return formatter.format(created)
}

fun BucketInfo.toBucketPresentable(): BucketPresentable {
    return BucketPresentable(name, "Created: ${getFormattedDate()}")
}
