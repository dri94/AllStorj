package tech.devezin.allstorj.onboarding.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_login.*
import tech.devezin.allstorj.MainActivity
import tech.devezin.allstorj.R
import tech.devezin.allstorj.utils.observe
import tech.devezin.allstorj.utils.observeEvent
import tech.devezin.allstorj.utils.text
import tech.devezin.allstorj.utils.viewModels

class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels {
        LoginViewModel(resources.getStringArray(R.array.login_satellites_values), requireContext().cacheDir.path)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.viewState.observe(viewLifecycleOwner) {
            setLoadingVisibility(it.isLoading)
            loginErrorText.text = it.error
        }
        viewModel.events.observeEvent(viewLifecycleOwner) {
            return@observeEvent when (it) {
                is LoginViewModel.Events.GoToBuckets -> {
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                    activity?.finishAffinity()
                    true
                }
                is LoginViewModel.Events.GoToRegistration -> {
                    startActivity(Intent(Intent.ACTION_VIEW).also { intent ->
                        intent.data = it.uri
                    })
                    true
                }
            }
        }
        loginSatellitePicker.setAdapter(
            ArrayAdapter(
                requireContext(),
                R.layout.item_satellite_picker,
                resources.getStringArray(R.array.login_satellites_names)
            )
        )
        loginConfirm.setOnClickListener {
            viewModel.onLoginClicked(
                getSelectedSatelliteAddressIndex(),
                loginApiKeyInput.text(),
                loginEncryptionAccessInput.text()
            )
        }
        loginRegister.setOnClickListener {
            viewModel.onRegisterClicked()
        }
    }

    private fun setLoadingVisibility(isLoading: Boolean) {
        loginConfirm.setLoading(isLoading)
        loginSatelliteLayout.isEnabled = !isLoading
        loginApiKeyLayout.isEnabled = !isLoading
        loginEncryptionAccessLayout.isEnabled = !isLoading
        loginErrorText.isEnabled = !isLoading
        loginRegister.isEnabled = !isLoading
    }

    private fun getSelectedSatelliteAddressIndex(): Int {
        return resources.getStringArray(R.array.login_satellites_names).indexOf(loginSatellitePicker.text.toString().trim())
    }

}
