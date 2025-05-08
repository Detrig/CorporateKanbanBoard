package com.example.disputer.authentication.presentation.login

import com.example.disputer.core.LiveDataWrapper

interface LoginUiStateLiveDataWrapper : LiveDataWrapper.Mutable<LoginUiState> {

    class Base : LoginUiStateLiveDataWrapper, LiveDataWrapper.Abstract<LoginUiState>()
}