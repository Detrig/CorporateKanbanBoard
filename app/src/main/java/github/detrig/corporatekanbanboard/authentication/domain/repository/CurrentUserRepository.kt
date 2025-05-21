package github.detrig.corporatekanbanboard.authentication.domain.repository

import github.detrig.corporatekanbanboard.core.Resource
import github.detrig.corporatekanbanboard.domain.model.User

interface CurrentUserRepository {
    suspend fun getCurrentUser(): Resource<User>
    suspend fun updateCurrentUser(user: User) : Resource<Unit>
}