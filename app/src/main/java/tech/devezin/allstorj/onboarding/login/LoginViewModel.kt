package tech.devezin.allstorj.onboarding.login

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import tech.devezin.allstorj.utils.SingleLiveEvent
import tech.devezin.allstorj.utils.setEvent

class LoginViewModel(private val satelliteAddresses: Array<String>, val repo: LoginRepository = LoginRepositoryImpl()): ViewModel() {

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState> = _viewState
    private val _events = MutableLiveData<SingleLiveEvent<Events>>()
    val events: LiveData<SingleLiveEvent<Events>> = _events

    sealed class Events {
        class GoToRegistration(val uri: Uri) : Events()
        object GoHome : Events()
    }

    data class ViewState(val error: String?)

    fun onLoginClicked(satelliteAddressIndex: Int, apiKey: String, encryptionAccess: String, cacheDir: String) {
        if (satelliteAddressIndex < 0 || satelliteAddressIndex > satelliteAddresses.size) {
            _viewState.value = ViewState("Must select a Satellite Address")
            return
        }
        if (apiKey.isEmpty()) {
            _viewState.value = ViewState("Must specify an API Key")
            return
        }
        if (encryptionAccess.isEmpty()) {
            _viewState.value = ViewState("Must input an encryption key (passphrase)")
            return
        }
        repo.login(satelliteAddresses[satelliteAddressIndex], apiKey, encryptionAccess, cacheDir).fold({
            _events.setEvent(Events.GoHome)
        }, { errorCode ->
            _viewState.value = ViewState(errorCode.localizedMessage)
        })
    }

    fun onRegisterClicked() {
        _events.setEvent(Events.GoToRegistration((Uri.parse("https://tardigrade.io/"))))
    }


}
