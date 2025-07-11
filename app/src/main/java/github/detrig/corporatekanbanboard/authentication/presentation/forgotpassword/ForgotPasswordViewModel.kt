package github.detrig.corporatekanbanboard.authentication.presentation.forgotpassword

import androidx.lifecycle.ViewModel
import github.detrig.corporatekanbanboard.authentication.domain.usecase.ForgotPasswordUseCase
import github.detrig.corporatekanbanboard.authentication.presentation.login.LoginScreen
import github.detrig.corporatekanbanboard.authentication.presentation.register.RegisterScreen
import github.detrig.corporatekanbanboard.core.ClearViewModel
import github.detrig.corporatekanbanboard.core.Navigation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ForgotPasswordViewModel(
    private val navigation: Navigation,
    private val clearViewModel : ClearViewModel,
    private val forgotPasswordUiStateLiveDataWrapper: ForgotPasswordUiStateLiveDataWrapper,
    private val forgotPasswordUseCase: ForgotPasswordUseCase,
    private val viewModelScope: CoroutineScope,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val dispatcherMain: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    fun init() {
        forgotPasswordUiStateLiveDataWrapper.update(ForgotPasswordUiState.Initial)
    }

    fun resetPassword(email: String) {

        forgotPasswordUiStateLiveDataWrapper.update(ForgotPasswordUiState.Loading)

        viewModelScope.launch(dispatcher) {
            try {
                forgotPasswordUseCase.invoke(email)
                withContext(dispatcherMain) {
                    forgotPasswordUiStateLiveDataWrapper.update(ForgotPasswordUiState.Success("Проверьте почту"))
                    loginScreen()
                }
            } catch (e: Exception) {
                withContext(dispatcherMain) {
                    forgotPasswordUiStateLiveDataWrapper.update(ForgotPasswordUiState.Error(e.message))
                }
            }
        }
    }

    fun liveDataUiState() = forgotPasswordUiStateLiveDataWrapper.liveData()

    fun loginScreen() = navigation.update(LoginScreen)
    fun registerScreen() {
        navigation.update(RegisterScreen)
        //clearViewModel.clearViewModel(RegisterViewModel::class.java)
    }


}