package github.detrig.corporatekanbanboard.authentication.domain.usecase

import github.detrig.corporatekanbanboard.authentication.data.UserRepository
import github.detrig.corporatekanbanboard.authentication.domain.repository.CurrentUserRepository

class CurrentUserUseCase(private val currentUserRepository: CurrentUserRepository) {
    suspend operator fun invoke() = currentUserRepository.getCurrentUser()
}