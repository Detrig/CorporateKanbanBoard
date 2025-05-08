package com.example.disputer.authentication.domain.usecase

import com.example.disputer.authentication.data.AuthRepository

class LogoutUseCase(private val authRepository: AuthRepository) {

    operator fun invoke() = authRepository.logout()
}