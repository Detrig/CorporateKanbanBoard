package github.detrig.corporatekanbanboard.authentication.presentation.register

import github.detrig.corporatekanbanboard.core.LiveDataWrapper

interface RegisterUiStateLiveDataWrapper : LiveDataWrapper.Mutable<RegisterUiState> {

    class Base : RegisterUiStateLiveDataWrapper, LiveDataWrapper.Abstract<RegisterUiState>()
}