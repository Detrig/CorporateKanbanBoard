package github.detrig.corporatekanbanboard.authentication.domain.usecase

import github.detrig.corporatekanbanboard.authentication.domain.repository.CurrentUserRepository
import github.detrig.corporatekanbanboard.core.Resource
import github.detrig.corporatekanbanboard.domain.model.User

class GetCurrentUserRoleUseCase(
    private val currentUserRepository: CurrentUserRepository
) {
    suspend operator fun invoke(): Resource<User> {
        return currentUserRepository.getCurrentUser()
    }
}