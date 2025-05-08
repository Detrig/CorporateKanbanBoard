package com.example.disputer.authentication.domain.usecase

import com.example.disputer.authentication.data.AuthRepository

class RegistrationUseCase(private val authRepository: AuthRepository) {

    suspend operator fun invoke(email: String, password: String, isCoach: Boolean, isParent: Boolean) =
        authRepository.register(email, password, isCoach, isParent)
}