package com.example.disputer.authentication.domain.usecase

import com.example.disputer.authentication.data.AuthRepository

class LoginUseCase(private val authRepository: AuthRepository) {

    suspend operator fun invoke(email : String, password : String) {
        authRepository.login(email, password)
    }
}