package github.detrig.corporatekanbanboard.authentication.data

import github.detrig.corporatekanbanboard.authentication.domain.repository.CurrentUserRepository
import github.detrig.corporatekanbanboard.core.Resource
import github.detrig.corporatekanbanboard.domain.model.User

class CurrentUserRepositoryImpl(
    private val currentUserDataSource: FirebaseCurrentDataSource
) : CurrentUserRepository {
    override suspend fun getCurrentUser(): Resource<User> =
        currentUserDataSource.getCurrentUser()
}