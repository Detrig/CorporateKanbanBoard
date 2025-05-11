package github.detrig.corporatekanbanboard.authentication.presentation.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import com.google.android.material.bottomnavigation.BottomNavigationView
import github.detrig.corporatekanbanboard.R
import github.detrig.corporatekanbanboard.core.AbstractFragment
import github.detrig.corporatekanbanboard.core.ProvideViewModel
import github.detrig.corporatekanbanboard.databinding.FragmentLoginBinding
import github.detrig.corporatekanbanboard.main.MainActivity


class LoginFragment : AbstractFragment<FragmentLoginBinding>() {

    private lateinit var viewModel : LoginViewModel
    override fun bind(inflater: LayoutInflater, container: ViewGroup?): FragmentLoginBinding =
        FragmentLoginBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.hideHeaderBottomNav()
        viewModel = (activity as ProvideViewModel).viewModel(LoginViewModel::class.java)

        viewModel.liveDataUiState().observe(viewLifecycleOwner) { uiState ->
            uiState.update(binding)
        }

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password= binding.passwordEditText.text.toString()

            hideKeyBoard()

            viewModel.login(email, password)
        }

        binding.registerTV.setOnClickListener {
            viewModel.registerScreen()
        }

        binding.forgotPasswordTV.setOnClickListener {
            viewModel.forgotPasswordScreen()
        }
    }
}