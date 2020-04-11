package tech.devezin.allstorj.onboarding.login

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tech.devezin.allstorj.utils.SingleLiveEvent
import tech.devezin.allstorj.utils.postUpdate
import tech.devezin.allstorj.utils.setEvent
import tech.devezin.allstorj.utils.setUpdate

class LoginViewModel(
    private val satelliteAddresses: Array<String>,
    private val cacheDir: String,
    private val repo: LoginRepository = LoginRepositoryImpl()
) : ViewModel() {

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState> = _viewState
    private val _events = MutableLiveData<SingleLiveEvent<Events>>()
    val events: LiveData<SingleLiveEvent<Events>> = _events

    sealed class Events {
        class GoToRegistration(val uri: Uri) : Events()
        object GoToBuckets : Events()
    }

    data class ViewState(val error: String?, val isLoading: Boolean)

    init {
        _viewState.value = ViewState(null, true)
        checkIfUserIsLoggedIn()
    }

    private fun checkIfUserIsLoggedIn() = viewModelScope.launch {
        repo.checkLogin(cacheDir).fold({
            _events.setEvent(Events.GoToBuckets)
        }, {
            _viewState.setUpdate {
                it.copy(isLoading = false)
            }
        })
    }

    fun onLoginClicked(satelliteAddressIndex: Int, apiKey: String, encryptionAccess: String) {
        if (satelliteAddressIndex < 0 || satelliteAddressIndex > satelliteAddresses.size) {
            setError("Must select a Satellite Address")
            return
        }
        if (apiKey.isEmpty()) {
            setError("Must specify an API Key")
            return
        }
        if (encryptionAccess.isEmpty()) {
            setError("Must input an encryption key (passphrase)")
            return
        }
        _viewState.setUpdate {
            it.copy(isLoading = true)
        }
        viewModelScope.launch {
            repo.login(satelliteAddresses[satelliteAddressIndex], apiKey, encryptionAccess, cacheDir).fold({
                _events.setEvent(Events.GoToBuckets)
            }, { errorCode ->
                _viewState.setUpdate {
                    it.copy(isLoading = false)
                }
                setError(errorCode.localizedMessage)
            })
        }

    }

    private fun setError(message: String?) {
        _viewState.postUpdate {
            it.copy(error = message)
        }
    }

    fun onRegisterClicked() {
        _events.setEvent(Events.GoToRegistration((Uri.parse("https://tardigrade.io/"))))
    }


}
