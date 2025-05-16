package github.detrig.corporatekanbanboard.main

import android.util.Log
import androidx.lifecycle.ViewModel
import github.detrig.corporatekanbanboard.authentication.domain.usecase.GetCurrentUserRoleUseCase
import github.detrig.corporatekanbanboard.authentication.domain.utils.CurrentUserLiveDataWrapper
import github.detrig.corporatekanbanboard.authentication.presentation.login.LoginScreen
import github.detrig.corporatekanbanboard.core.App
import github.detrig.corporatekanbanboard.presentation.boards.BoardsScreen
import github.detrig.corporatekanbanboard.core.Navigation
import github.detrig.corporatekanbanboard.core.Resource
import github.detrig.corporatekanbanboard.presentation.globalchat.GlobalChatScreen
import github.detrig.corporatekanbanboard.presentation.profile.ProfileEditScreen
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(
    private val navigation: Navigation,
    private val currentUserLiveDataWrapper: CurrentUserLiveDataWrapper,
    private val getCurrentUserRoleUseCase: GetCurrentUserRoleUseCase,
    private val viewModelScope: CoroutineScope,
    private val dispatcherMain: CoroutineDispatcher = Dispatchers.Main,
    private val dispatcherIo: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {
    fun login() {
        navigation.update(LoginScreen)
    }

    fun getCurrentUserRole() = currentUserLiveDataWrapper.liveData().value

    fun boardsScreen() {
        navigation.update(BoardsScreen)
    }

    fun globalChatScreen() {
        navigation.update(GlobalChatScreen)
    }

    fun profileEditScreen() {
        navigation.update(ProfileEditScreen)
    }

    fun navigationLiveData() = navigation.liveData()

    fun init(firstRun: Boolean) {
        if (firstRun)
            navigation.update(LoginScreen)
    }

    fun initializeCurrentUserIfLoggedIn(onSuccess: () -> Unit) {
        viewModelScope.launch(dispatcherIo) {
            val result = getCurrentUserRoleUseCase()
            withContext(dispatcherMain) {
                if (result is Resource.Success) {
                    currentUserLiveDataWrapper.update(result.data!!)
                    App.currentUserId = result.data.id
                    App.currentUserEmail = result.data.email
                    App.currentUserName = result.data.name
                    onSuccess()
                } else {
                    navigation.update(LoginScreen)
                }
            }
        }
    }
}