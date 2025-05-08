package com.example.disputer.authentication.presentation.register

import com.example.disputer.core.LiveDataWrapper

interface RegisterUiStateLiveDataWrapper : LiveDataWrapper.Mutable<RegisterUiState> {

    class Base : RegisterUiStateLiveDataWrapper, LiveDataWrapper.Abstract<RegisterUiState>()
}