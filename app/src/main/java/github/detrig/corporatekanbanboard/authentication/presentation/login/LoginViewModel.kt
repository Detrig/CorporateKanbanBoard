package com.example.disputer.authentication.presentation.login

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.disputer.authentication.data.AuthUser
import com.example.disputer.authentication.domain.usecase.GetCurrentUserRoleUseCase
import com.example.disputer.authentication.domain.usecase.LoginUseCase
import com.example.disputer.authentication.domain.utils.CurrentUserLiveDataWrapper
import com.example.disputer.authentication.presentation.forgotpassword.ForgotPasswordScreen
import com.example.disputer.authentication.presentation.register.RegisterScreen
import com.example.disputer.core.Navigation
import com.example.disputer.core.Resource
import com.example.disputer.training.presentation.training_coach.main.TrainingCoachMainScreen
import com.example.disputer.training.presentation.training_parent.TrainingParentMainScreen
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
                            navigateBasedOnRole(userResource.data)
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

    private fun navigateBasedOnRole(authUser: AuthUser) {
        when (authUser) {
            is AuthUser.CoachUser -> navigation.update(TrainingCoachMainScreen)
            is AuthUser.ParentUser -> navigation.update(TrainingParentMainScreen)
            is AuthUser.Empty -> Log.d("VB-04", "Empty user")
        }
    }

    fun userRoleLiveDataWrapper() = currentUserLiveDataWrapper.liveData()

    fun liveDataUiState() = loginUiStateLiveDataWrapper.liveData()

    fun trainingsMainScreen() = navigation.update(TrainingParentMainScreen)
    fun registerScreen() = navigation.update(RegisterScreen)
    fun forgotPasswordScreen() = navigation.update(ForgotPasswordScreen)
}