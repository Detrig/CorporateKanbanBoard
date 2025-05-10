package github.detrig.corporatekanbanboard.authentication.domain.usecase

import github.detrig.corporatekanbanboard.authentication.data.UserRepository

class IsLoggedInUseCase(private val userRepository: UserRepository) {

    operator fun invoke() = userRepository.isLoggedIn()
}