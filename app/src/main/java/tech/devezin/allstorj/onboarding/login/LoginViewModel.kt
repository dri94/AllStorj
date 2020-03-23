package tech.devezin.allstorj.onboarding.login

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import tech.devezin.allstorj.utils.SingleLiveEvent
import tech.devezin.allstorj.utils.setEvent
import tech.devezin.allstorj.utils.setUpdate

class LoginViewModel(private val repo: LoginRepository = LoginRepositoryImpl()): ViewModel() {

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState> = _viewState
    private val _events = MutableLiveData<SingleLiveEvent<Events>>()
    val events: LiveData<SingleLiveEvent<Events>> = _events

    sealed class Events {
        class GoToRegistration(val uri: Uri) : Events()
        object GoHome : Events()
    }

    data class ViewState(val error: String?)

    fun onLoginClicked(satelliteAddress: String, apiKey: String, encryptionAccess: String, cacheDir: String) {
        repo.login(satelliteAddress, apiKey, encryptionAccess, cacheDir).fold({
            _events.setEvent(Events.GoHome)
        }, { errorCode ->
            _viewState.setUpdate {
                it.copy(error = errorCode.localizedMessage)
            }
        })
    }

    fun onRegisterClicked() {
        _events.setEvent(Events.GoToRegistration((Uri.parse("https://tardigrade.io/satellites/"))))
    }


}
