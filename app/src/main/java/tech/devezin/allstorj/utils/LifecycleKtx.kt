package tech.devezin.allstorj.utils

import androidx.annotation.MainThread
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.*
import com.google.android.material.snackbar.Snackbar

inline fun <VM : ViewModel> viewModelFactory(crossinline onCreateViewModel: () -> VM) =
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = onCreateViewModel.invoke() as T
    }

@MainThread
inline fun <reified VM : ViewModel> Fragment.viewModels(
    noinline ownerProducer: () -> ViewModelStoreOwner = { this },
    crossinline onCreateViewModel: () -> VM = { VM::class.java.newInstance() }
) = createViewModelLazy(VM::class, { ownerProducer().viewModelStore }, factoryProducer = {
    viewModelFactory {
        onCreateViewModel.invoke()
    }
})

@MainThread
inline fun <reified VM : ViewModel> Fragment.activityViewModels(
    crossinline onCreateViewModel: () -> VM = { VM::class.java.newInstance() }
) = createViewModelLazy(VM::class, { requireActivity().viewModelStore }, factoryProducer = {
    viewModelFactory {
        onCreateViewModel.invoke()
    }
})


open class SingleLiveEvent<out T>(private val content: T) {

    var hasBeenHandled = false

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            content
        }
    }
}

class EventObserver<T>(private val onEventUnhandledContent: (T) -> Unit) :
    Observer<SingleLiveEvent<T>> {
    override fun onChanged(event: SingleLiveEvent<T>?) {
        event?.getContentIfNotHandled()?.let { value ->
            onEventUnhandledContent(value)
        }
    }
}

inline fun <T> LiveData<T>.observe(owner: LifecycleOwner, crossinline observer: (T) -> Unit) {
    observe(owner, Observer {
        observer(it)
    })
}

inline fun <T> LiveData<SingleLiveEvent<T>>.observeEvent(owner: LifecycleOwner, crossinline onEventUnhandledContent: (T) -> Boolean) {
    observe(owner, Observer { event ->
        val data = event
        val content = data?.getContentIfNotHandled()
        content?.let {
            event.hasBeenHandled = onEventUnhandledContent(content)
        }
    })
}

fun <T> MutableLiveData<SingleLiveEvent<T>>.setEvent(event: T) {
    this.value = SingleLiveEvent(event)
}

fun <T> MutableLiveData<SingleLiveEvent<T>>.postEvent(event: T) {
    this.postValue(SingleLiveEvent(event))
}


fun <T> MutableLiveData<T>.postUpdate(newValue: (T) -> T) {
    val currentValue = this.value ?: return
    this.postValue(newValue.invoke(currentValue))
}

fun <T> MutableLiveData<T>.setUpdate(newValue: (T) -> T) {
    val currentValue = this.value ?: return
    this.value = newValue.invoke(currentValue)
}

fun Fragment.showSnackbar(@StringRes message: Int, length: Int = Snackbar.LENGTH_LONG) {
    Snackbar.make(requireView(), message, length).show()
}
