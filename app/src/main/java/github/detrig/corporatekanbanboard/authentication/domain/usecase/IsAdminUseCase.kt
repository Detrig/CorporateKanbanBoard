package github.detrig.corporatekanbanboard.authentication.domain.usecase

import github.detrig.corporatekanbanboard.authentication.data.UserRepository

class IsCoachUseCase(private val userRepository: UserRepository) {

    suspend operator fun invoke() = userRepository.isCoach()
}