package github.detrig.corporatekanbanboard.authentication.presentation.forgotpassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import github.detrig.corporatekanbanboard.core.AbstractFragment
import github.detrig.corporatekanbanboard.core.ProvideViewModel
import github.detrig.corporatekanbanboard.databinding.FragmentForgotPasswordBinding
import github.detrig.corporatekanbanboard.main.MainActivity

class ForgotPasswordFragment : AbstractFragment<FragmentForgotPasswordBinding>() {

    private lateinit var viewModel: ForgotPasswordViewModel

    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentForgotPasswordBinding =
        FragmentForgotPasswordBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.hideHeaderBottomNav()

        viewModel = (activity as ProvideViewModel).viewModel(ForgotPasswordViewModel::class.java)

        viewModel.liveDataUiState().observe(viewLifecycleOwner) { uiState ->
            uiState.update(binding)
        }
        viewModel.init()

        binding.recoverPasswordButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()

            viewModel.resetPassword(email)
        }

        binding.loginTV.setOnClickListener {
            viewModel.loginScreen()
        }

        binding.registerTV.setOnClickListener {
            viewModel.registerScreen()
        }
    }
}