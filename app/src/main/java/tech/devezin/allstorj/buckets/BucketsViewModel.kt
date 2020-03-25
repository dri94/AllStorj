package tech.devezin.allstorj.buckets

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.storj.BucketInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tech.devezin.allstorj.utils.postUpdate
import tech.devezin.allstorj.utils.setUpdate
import java.text.SimpleDateFormat

class BucketsViewModel(private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO, private val repo: BucketsRepository = BucketsRepositoryImpl()): ViewModel() {

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState> = _viewState

    data class ViewState(val isLoading: Boolean, val buckets: List<BucketPresentable> = listOf(), val error: String? = null)
    data class BucketPresentable(val name: String, val description: String)

    init {
        _viewState.value = ViewState(true)
        getBuckets()
    }

    fun onBucketClick(bucket: BucketPresentable) {

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

fun List<BucketInfo>.toBucketPresentables(): List<BucketsViewModel.BucketPresentable> {
    return map {
        it.toBucketPresentable()
    }
}

fun BucketInfo.toBucketPresentable(): BucketsViewModel.BucketPresentable {
    val formatter = SimpleDateFormat("mm/dd/yyyy")
    return BucketsViewModel.BucketPresentable(name, "Created: ${formatter.format(created)}")
}
