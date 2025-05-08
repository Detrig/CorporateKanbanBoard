package com.example.disputer.authentication.presentation.forgotpassword

import android.view.View
import android.widget.Toast
import com.example.disputer.databinding.FragmentForgotPasswordBinding

interface ForgotPasswordUiState {

    fun update(binding: FragmentForgotPasswordBinding)

    abstract class Abstract(
        private val emailEnable: Boolean,
        private val resetPassEnable: Boolean,
        private val loginEnable: Boolean,
        private val registerEnable: Boolean,
        private val progressVisible: Int
    ) : ForgotPasswordUiState {
        override fun update(binding: FragmentForgotPasswordBinding) = with(binding) {
            emailEditText.isEnabled = emailEnable
            recoverPasswordButton.isEnabled = resetPassEnable
            loginTV.isEnabled = loginEnable
            registerTV.isEnabled = registerEnable
            progressBar.visibility = progressVisible
        }
    }

    object Initial :
        Abstract(true, true, true, true, View.GONE)

    object Loading :
        Abstract(false, false, false, false, View.VISIBLE)

    data class Error(private val errorText: String?) :
        Abstract(true, true, true, true, View.GONE) {
        override fun update(binding: FragmentForgotPasswordBinding) {
            super.update(binding)
            if (errorText != null)
                Toast.makeText(binding.root.context, errorText, Toast.LENGTH_SHORT).show()
        }
    }

    data class Success(private val message: String) :
        Abstract(true, true, true, true, View.GONE) {
        override fun update(binding: FragmentForgotPasswordBinding) {
            super.update(binding)
            Toast.makeText(binding.root.context, message, Toast.LENGTH_SHORT).show()
        }
    }
}