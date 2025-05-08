package com.example.disputer.authentication.presentation.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.disputer.R
import com.example.disputer.authentication.presentation.main.MainActivity
import com.example.disputer.core.AbstractFragment
import com.example.disputer.core.ProvideViewModel
import com.example.disputer.databinding.FragmentRegistrationBinding

class RegisterFragment : AbstractFragment<FragmentRegistrationBinding>() {

    private lateinit var viewModel: RegisterViewModel

    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRegistrationBinding =
        FragmentRegistrationBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.hideHeaderBottomNav()
        var isCoach = false
        var isParent = true

        viewModel = (activity as ProvideViewModel).viewModel(RegisterViewModel::class.java)

        viewModel.liveData().observe(viewLifecycleOwner) { uiState ->
            uiState.update(binding)
        }
        viewModel.init()

        binding.registerButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val repeatPassword = binding.repeatPasswordEditText.text.toString()

            viewModel.register(email, password, repeatPassword, isCoach, isParent)
        }

        setUpButtons()

        binding.toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            when (checkedId) {
                R.id.btnParent -> if (isChecked) {
                    isCoach = false
                    isParent = true
                    binding.btnParent.setTextColor(resources.getColor(R.color.blue))
                    binding.btnCoach.setTextColor(resources.getColor(R.color.white))

                    binding.btnParent.setBackgroundColor(resources.getColor(R.color.white))
                    binding.btnCoach.setBackgroundColor(resources.getColor(R.color.blue))
                }

                R.id.btnCoach -> if (isChecked) {
                    isCoach = true
                    isParent = false
                    binding.btnCoach.setTextColor(resources.getColor(R.color.blue))
                    binding.btnParent.setTextColor(resources.getColor(R.color.white))

                    binding.btnCoach.setBackgroundColor(resources.getColor(R.color.white))
                    binding.btnParent.setBackgroundColor(resources.getColor(R.color.blue))
                }
            }
        }
        binding.loginTV.setOnClickListener {
            viewModel.loginScreen()
        }
    }

    private fun setUpButtons() {
        binding.btnParent.setTextColor(resources.getColor(R.color.blue))
        binding.btnParent.setBackgroundColor(resources.getColor(R.color.white))
    }
}