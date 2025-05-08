package com.example.disputer.authentication.presentation.forgotpassword

import androidx.lifecycle.ViewModel
import com.example.disputer.authentication.domain.usecase.ForgotPasswordUseCase
import com.example.disputer.authentication.presentation.login.LoginScreen
import com.example.disputer.authentication.presentation.register.RegisterScreen
import com.example.disputer.core.ClearViewModel
import com.example.disputer.core.Navigation
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