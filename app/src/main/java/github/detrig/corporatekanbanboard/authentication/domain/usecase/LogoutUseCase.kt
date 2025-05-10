package github.detrig.corporatekanbanboard.authentication.domain.usecase

import github.detrig.corporatekanbanboard.authentication.data.AuthRepository

class LogoutUseCase(private val authRepository: AuthRepository) {

    operator fun invoke() = authRepository.logout()
}