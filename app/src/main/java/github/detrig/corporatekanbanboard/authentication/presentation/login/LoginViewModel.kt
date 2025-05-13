package github.detrig.corporatekanbanboard.authentication.presentation.login

import androidx.lifecycle.ViewModel
import github.detrig.corporatekanbanboard.authentication.domain.usecase.GetCurrentUserRoleUseCase
import github.detrig.corporatekanbanboard.authentication.domain.usecase.LoginUseCase
import github.detrig.corporatekanbanboard.authentication.domain.utils.CurrentUserLiveDataWrapper
import github.detrig.corporatekanbanboard.authentication.presentation.forgotpassword.ForgotPasswordScreen
import github.detrig.corporatekanbanboard.authentication.presentation.register.RegisterScreen
import github.detrig.corporatekanbanboard.core.App
import github.detrig.corporatekanbanboard.presentation.boards.BoardsScreen
import github.detrig.corporatekanbanboard.core.Navigation
import github.detrig.corporatekanbanboard.core.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(
    private val navigation: Navigation,
    private val loginUiStateLiveDataWrapper: LoginUiStateLiveDataWrapper,
    private val currentUserLiveDataWrapper: CurrentUserLiveDataWrapper,
    private val loginUseCase: LoginUseCase,
    private val getCurrentUserRoleUseCase: GetCurrentUserRoleUseCase,
    private val viewModelScope: CoroutineScope,
    private val dispatcherIo: CoroutineDispatcher = Dispatchers.IO,
    private val dispatcherMain: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    fun init() {
        loginUiStateLiveDataWrapper.update(LoginUiState.Initial)
    }

    fun login(email: String, password: String) {

        loginUiStateLiveDataWrapper.update(LoginUiState.Loading)

        viewModelScope.launch(dispatcherIo) {
            try {
                loginUseCase.invoke(email, password)

                withContext(dispatcherMain) {
                    when (val userResource = getCurrentUserRoleUseCase()) {
                        is Resource.Success -> {
                            currentUserLiveDataWrapper.update(userResource.data!!)
                            App.currentUserId = userResource.data.id
                            navigation.update(BoardsScreen)
                        }

                        is Resource.Error -> {
                            loginUiStateLiveDataWrapper.update(LoginUiState.Error(errorText = userResource.message))
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(dispatcherMain) {
                    loginUiStateLiveDataWrapper.update(LoginUiState.Error(errorText = e.message))
                }
            }
        }
    }

    fun liveDataUiState() = loginUiStateLiveDataWrapper.liveData()

    fun registerScreen() = navigation.update(RegisterScreen)
    fun forgotPasswordScreen() = navigation.update(ForgotPasswordScreen)
}