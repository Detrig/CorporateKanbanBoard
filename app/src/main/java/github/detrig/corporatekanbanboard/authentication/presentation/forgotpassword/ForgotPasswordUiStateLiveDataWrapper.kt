package com.example.disputer.authentication.presentation.forgotpassword

import com.example.disputer.core.LiveDataWrapper

interface ForgotPasswordUiStateLiveDataWrapper : LiveDataWrapper.Mutable<ForgotPasswordUiState> {

    class Base : ForgotPasswordUiStateLiveDataWrapper, LiveDataWrapper.Abstract<ForgotPasswordUiState>()
}