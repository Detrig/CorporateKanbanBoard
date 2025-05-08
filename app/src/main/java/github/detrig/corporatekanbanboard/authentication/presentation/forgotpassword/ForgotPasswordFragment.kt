package com.example.disputer.authentication.presentation.forgotpassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.disputer.authentication.presentation.main.MainActivity
import com.example.disputer.core.AbstractFragment
import com.example.disputer.core.ProvideViewModel
import com.example.disputer.databinding.FragmentForgotPasswordBinding

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