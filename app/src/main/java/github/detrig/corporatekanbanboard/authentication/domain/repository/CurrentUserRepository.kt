package com.example.disputer.authentication.domain.repository

import com.example.disputer.authentication.data.AuthUser
import com.example.disputer.authentication.data.User
import com.example.disputer.core.Resource

interface CurrentUserRepository {
    suspend fun getCurrentUser(): Resource<AuthUser>
}