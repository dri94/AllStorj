package tech.devezin.allstorj.onboarding.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_login.*
import tech.devezin.allstorj.R
import tech.devezin.allstorj.buckets.BucketsFragment
import tech.devezin.allstorj.utils.*

class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private val viewModel: LoginViewModel by viewModels {
        LoginViewModel(resources.getStringArray(R.array.login_satellites_values), requireContext().cacheDir.path)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.viewState.observe(viewLifecycleOwner) {
            setLoadingVisibility(it.isLoading)
            loginErrorText.text = it.error
        }
        viewModel.events.observeEvent(viewLifecycleOwner) {
            return@observeEvent when (it) {
                is LoginViewModel.Events.GoToBuckets -> {
                    (activity as AppCompatActivity).supportFragmentManager.beginTransaction().replace(R.id.mainContainer, BucketsFragment.newInstance()).commit()
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
        loginProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        val inputVisibility = if (isLoading) View.GONE else View.VISIBLE
        loginSatelliteLayout.visibility = inputVisibility
        loginApiKeyLayout.visibility = inputVisibility
        loginEncryptionAccessLayout.visibility = inputVisibility
        loginErrorText.visibility = inputVisibility
        loginConfirm.visibility = inputVisibility
        loginRegister.visibility = inputVisibility
    }

    private fun getSelectedSatelliteAddressIndex(): Int {
        return resources.getStringArray(R.array.login_satellites_names)
            .indexOf(loginSatellitePicker.text.toString().trim())
    }

}
