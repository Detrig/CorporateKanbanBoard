package com.example.disputer.authentication.presentation.register

import androidx.lifecycle.ViewModel
import com.example.disputer.authentication.domain.usecase.RegistrationUseCase
import com.example.disputer.authentication.presentation.login.LoginScreen
import com.example.disputer.core.Navigation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterViewModel(
    private val navigation: Navigation,
    private val registerUiStateLiveDataWrapper: RegisterUiStateLiveDataWrapper,
    private val registerUseCase: RegistrationUseCase,
    private val viewModelScope: CoroutineScope,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val dispatcherMain: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    fun init() {
        registerUiStateLiveDataWrapper.update(RegisterUiState.Initial)
    }

    fun register(email: String, password: String, repeatPassword: String, isCoach: Boolean, isParent: Boolean) {
        registerUiStateLiveDataWrapper.update(RegisterUiState.Loading)

        if (password == repeatPassword) {
            viewModelScope.launch(dispatcher) {
                try {
                    registerUseCase.invoke(email, password, isCoach, isParent)
                    withContext(dispatcherMain) {
                        registerUiStateLiveDataWrapper.update(RegisterUiState.Success("Подтвердите почту!"))
                        loginScreen()
                    }
                } catch (e: Exception) {
                    withContext(dispatcherMain) {
                        registerUiStateLiveDataWrapper.update(RegisterUiState.Error(e.message))
                    }
                }
            }
        } else {
            registerUiStateLiveDataWrapper.update(RegisterUiState.PasswordDontMatch)
        }
    }

    fun liveData() = registerUiStateLiveDataWrapper.liveData()

    fun loginScreen() = navigation.update(LoginScreen)
}