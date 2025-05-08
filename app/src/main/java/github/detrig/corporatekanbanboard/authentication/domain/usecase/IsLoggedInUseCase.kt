package com.example.disputer.authentication.domain.usecase

import com.example.disputer.authentication.data.UserRepository

class IsLoggedInUseCase(private val userRepository: UserRepository) {

    operator fun invoke() = userRepository.isLoggedIn()
}