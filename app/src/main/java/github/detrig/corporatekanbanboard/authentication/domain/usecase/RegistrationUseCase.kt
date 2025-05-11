package github.detrig.corporatekanbanboard.authentication.domain.usecase

import github.detrig.corporatekanbanboard.authentication.data.AuthRepository

class RegistrationUseCase(private val authRepository: AuthRepository) {

    suspend operator fun invoke(email: String, password: String) =
        authRepository.register(email, password)
}