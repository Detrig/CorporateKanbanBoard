package com.example.disputer.authentication.domain.usecase

import com.example.disputer.authentication.data.AuthUser
import com.example.disputer.authentication.domain.repository.CurrentUserRepository
import com.example.disputer.core.Resource

class GetCurrentUserRoleUseCase(
    private val currentUserRepository: CurrentUserRepository
) {
    suspend operator fun invoke(): Resource<AuthUser> {
        return currentUserRepository.getCurrentUser()
    }
}