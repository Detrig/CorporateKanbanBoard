package com.example.disputer.authentication.domain.usecase

import com.example.disputer.authentication.data.PasswordRepository

class ForgotPasswordUseCase(private val passwordRepository: PasswordRepository) {

    suspend operator fun invoke(email : String) = passwordRepository.forgotPassword(email)
}