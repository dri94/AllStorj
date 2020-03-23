package tech.devezin.allstorj.onboarding.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_login.*
import tech.devezin.allstorj.R
import tech.devezin.allstorj.utils.*

class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.viewState.observe(viewLifecycleOwner) {
            loginEncryptionAccessInput.setText(it.error)
        }
        viewModel.events.observeEvent(viewLifecycleOwner) {
            return@observeEvent when (it) {
                is LoginViewModel.Events.GoHome -> TODO("Go Home")
                is LoginViewModel.Events.GoToRegistration -> {
                    startActivity(Intent(Intent.ACTION_VIEW).also { intent ->
                        intent.data = it.uri
                    })
                    true
                }
            }
        }
        loginConfirm.setOnClickListener {
            viewModel.onLoginClicked("", loginApiKeyInput.text(), loginEncryptionAccessInput.text(), requireContext().cacheDir.path)
        }
        loginRegister.setOnClickListener {
            viewModel.onRegisterClicked()
        }
    }

}
