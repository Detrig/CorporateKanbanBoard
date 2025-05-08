package com.example.disputer.authentication.domain.usecase

import com.example.disputer.authentication.data.UserRepository
import com.example.disputer.authentication.domain.repository.CurrentUserRepository

class CurrentUserUseCase(private val currentUserRepository: CurrentUserRepository) {
    suspend operator fun invoke() = currentUserRepository.getCurrentUser()
}