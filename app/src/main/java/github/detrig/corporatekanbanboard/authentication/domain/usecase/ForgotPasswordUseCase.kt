package github.detrig.corporatekanbanboard.authentication.domain.usecase

import github.detrig.corporatekanbanboard.authentication.data.PasswordRepository

class ForgotPasswordUseCase(private val passwordRepository: PasswordRepository) {

    suspend operator fun invoke(email : String) = passwordRepository.forgotPassword(email)
}