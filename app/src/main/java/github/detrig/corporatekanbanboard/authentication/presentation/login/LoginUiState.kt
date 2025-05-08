package com.example.disputer.authentication.presentation.login

import android.view.View
import android.widget.Toast
import com.example.disputer.databinding.FragmentLoginBinding

interface LoginUiState {

    fun update(binding: FragmentLoginBinding): Unit = throw IllegalStateException("")

    abstract class Abstract(
        private val emailEnable: Boolean,
        private val passwordEnable: Boolean,
        private val forgotPassEnable: Boolean,
        private val registerEnable: Boolean,
        private val progressVisible: Int,
    ) : LoginUiState {

        override fun update(binding: FragmentLoginBinding) = with(binding) {
            emailEditText.isEnabled = emailEnable
            passwordEditText.isEnabled = passwordEnable
            forgotPasswordTV.isEnabled = forgotPassEnable
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
        override fun update(binding: FragmentLoginBinding) {
            super.update(binding)
            if (errorText != null)
                Toast.makeText(binding.root.context, errorText, Toast.LENGTH_SHORT).show()
        }
    }
}
