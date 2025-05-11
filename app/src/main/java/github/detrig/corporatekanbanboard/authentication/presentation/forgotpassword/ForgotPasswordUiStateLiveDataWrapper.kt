package github.detrig.corporatekanbanboard.authentication.presentation.forgotpassword

import github.detrig.corporatekanbanboard.core.LiveDataWrapper

interface ForgotPasswordUiStateLiveDataWrapper : LiveDataWrapper.Mutable<ForgotPasswordUiState> {

    class Base : ForgotPasswordUiStateLiveDataWrapper, LiveDataWrapper.Abstract<ForgotPasswordUiState>()
}