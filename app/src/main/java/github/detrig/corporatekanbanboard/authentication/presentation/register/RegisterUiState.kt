package com.example.disputer.authentication.presentation.register

import android.view.View
import android.widget.Toast
import com.example.disputer.R
import com.example.disputer.databinding.FragmentRegistrationBinding

interface RegisterUiState {

    fun update(binding: FragmentRegistrationBinding)

    abstract class Abstract(
        private val emailEnable: Boolean,
        private val passwordEnable: Boolean,
        private val repeatPasswordEnable: Boolean,
        private val sendEmailEnable: Boolean,
        private val loginEnable: Boolean,
        private val registerEnable: Boolean,
        private val progressVisible: Int
    ) : RegisterUiState {

        override fun update(binding: FragmentRegistrationBinding) = with(binding) {
            emailEditText.isEnabled = emailEnable
            passwordEditText.isEnabled = passwordEnable
            passwordEditText.setBackgroundResource(R.drawable.edittesdt_uderline)
            repeatPasswordEditText.isEnabled = repeatPasswordEnable
            repeatPasswordEditText.setBackgroundResource(R.drawable.edittesdt_uderline)
            registerButton.isEnabled = registerEnable
            progressBar.visibility = progressVisible
            loginTV.isEnabled = loginEnable
        }
    }

    object Initial :
        Abstract(true, true, true, true, true, true, View.GONE)

    object Loading :
        Abstract(false, false, false, false, false, false, View.VISIBLE)

    object PasswordDontMatch :
        Abstract(true, true, true, true, true, true, View.GONE) {

        override fun update(binding: FragmentRegistrationBinding) {
            super.update(binding)
            binding.passwordEditText.setBackgroundResource(R.drawable.edterr)
            binding.repeatPasswordEditText.setBackgroundResource(R.drawable.edterr)
        }
    }

    data class Success(private val message : String) : Abstract(true, true, true, true, true, true, View.GONE) {
        override fun update(binding: FragmentRegistrationBinding) {
            super.update(binding)
            Toast.makeText(binding.root.context, message, Toast.LENGTH_SHORT).show()
        }
    }

    data class Error(private val errorText: String?) :
        Abstract(true, true, true, true, true, true, View.GONE) {
        override fun update(binding: FragmentRegistrationBinding) {
            super.update(binding)
            if (errorText != null)
                Toast.makeText(binding.root.context, errorText, Toast.LENGTH_SHORT).show()
        }
    }

    object PasswordMatch :
        Abstract(true, true, true, true, true, true, View.GONE) {
        override fun update(binding: FragmentRegistrationBinding) {
            super.update(binding)
        }
    }
}