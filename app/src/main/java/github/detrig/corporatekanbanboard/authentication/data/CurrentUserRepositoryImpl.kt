package com.example.disputer.authentication.data

import com.example.disputer.authentication.domain.repository.CurrentUserRepository
import com.example.disputer.core.Resource

class CurrentUserRepositoryImpl(
    private val currentUserDataSource: FirebaseCurrentDataSource
) : CurrentUserRepository {
    override suspend fun getCurrentUser(): Resource<AuthUser> =
        currentUserDataSource.getCurrentUser()
}