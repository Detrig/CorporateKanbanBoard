package com.example.disputer.authentication.presentation.main

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.disputer.authentication.data.AuthUser
import com.example.disputer.authentication.domain.usecase.GetCurrentUserRoleUseCase
import com.example.disputer.authentication.domain.utils.CurrentUserLiveDataWrapper
import com.example.disputer.authentication.presentation.login.LoginScreen
import com.example.disputer.core.Navigation
import com.example.disputer.coach.presentation.list.CoachScreen
import com.example.disputer.core.Resource
import com.example.disputer.info.InfoScreen
import com.example.disputer.notification.data.NotificationData
import com.example.disputer.notification.domain.utils.NotificationHelper
import com.example.disputer.training.presentation.training_parent.TrainingParentMainScreen
import com.example.disputer.schedule.presentation.ScheduleScreen
import com.example.disputer.training.presentation.training_coach.main.TrainingCoachMainScreen
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
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

    fun mainScreen() {
        if (getCurrentUserRole() is AuthUser.ParentUser)
            navigation.update(TrainingParentMainScreen)
        else if (getCurrentUserRole() is AuthUser.CoachUser)
            navigation.update(TrainingCoachMainScreen)
    }

    fun scheduleScreen() {
        navigation.update(ScheduleScreen)
    }

    fun coachScreen() {
        navigation.update(CoachScreen)
    }

    fun infoScreen() {
        navigation.update(InfoScreen)
    }

    fun init(firstRun: Boolean) {
        if (firstRun)
            navigation.update(LoginScreen)
    }

    fun liveData() = navigation.liveData()

    fun initializeCurrentUserIfLoggedIn(onSuccess: () -> Unit) {
        viewModelScope.launch(dispatcherIo) {
            val result = getCurrentUserRoleUseCase()
            withContext(dispatcherMain) {
                if (result is Resource.Success) {
                    currentUserLiveDataWrapper.update(result.data!!)
                    onSuccess()
                } else {
                    navigation.update(LoginScreen) // если ошибка, покажем логин
                }
            }
        }
    }
}