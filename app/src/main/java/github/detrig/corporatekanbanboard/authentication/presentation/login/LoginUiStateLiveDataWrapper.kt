package github.detrig.corporatekanbanboard.authentication.presentation.login

import github.detrig.corporatekanbanboard.core.LiveDataWrapper

interface LoginUiStateLiveDataWrapper : LiveDataWrapper.Mutable<LoginUiState> {

    class Base : LoginUiStateLiveDataWrapper, LiveDataWrapper.Abstract<LoginUiState>()
}