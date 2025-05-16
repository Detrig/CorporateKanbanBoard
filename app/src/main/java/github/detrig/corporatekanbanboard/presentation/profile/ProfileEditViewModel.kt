package github.detrig.corporatekanbanboard.presentation.profile

import androidx.lifecycle.ViewModel
import github.detrig.corporatekanbanboard.authentication.domain.repository.CurrentUserRepository
import github.detrig.corporatekanbanboard.authentication.domain.usecase.LogoutUseCase
import github.detrig.corporatekanbanboard.authentication.domain.utils.CurrentUserLiveDataWrapper
import github.detrig.corporatekanbanboard.authentication.presentation.login.LoginScreen
import github.detrig.corporatekanbanboard.core.App
import github.detrig.corporatekanbanboard.core.Navigation
import github.detrig.corporatekanbanboard.core.Resource
import github.detrig.corporatekanbanboard.core.Result
import github.detrig.corporatekanbanboard.domain.model.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileEditViewModel(
    private val navigation: Navigation,
    private val logoutUseCase: LogoutUseCase,
    private val currentUserRepository: CurrentUserRepository,
    private val currentUserLiveDataWrapper: CurrentUserLiveDataWrapper,
    private val viewModelScope: CoroutineScope,
    private val dispatcherMain: CoroutineDispatcher = Dispatchers.Main,
    private val dispatcherIo: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    fun updateProfile(name: String, photoBase64: String) {
        viewModelScope.launch(dispatcherIo) {
            val currentUser = currentUserRepository.getCurrentUser()
            if (currentUser is Resource.Success) {
                val updatedUser = currentUser.data?.copy(name = name, avatarBase64 = photoBase64)
                updatedUser?.let {
                    val result = currentUserRepository.updateCurrentUser(it)

                    when (result) {
                        is Resource.Success -> {
                            App.currentUserName = name
                        }
                        is Resource.Error -> {

                        }
                    }
                    withContext(dispatcherMain) {
                        currentUserLiveDataWrapper.update(it)
                    }
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch(dispatcherIo) {
            logoutUseCase.invoke()

            withContext(dispatcherMain) {
                currentUserLiveDataWrapper.update(User())
                navigation.update(LoginScreen)
            }
        }
    }

    fun getCurrentUser() = currentUserLiveDataWrapper.liveData().value ?: User()
}