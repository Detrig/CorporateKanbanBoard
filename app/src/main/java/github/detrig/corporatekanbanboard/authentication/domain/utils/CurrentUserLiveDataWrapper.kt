package github.detrig.corporatekanbanboard.authentication.domain.utils

import github.detrig.corporatekanbanboard.core.LiveDataWrapper
import github.detrig.corporatekanbanboard.domain.model.User

interface CurrentUserLiveDataWrapper : LiveDataWrapper.Mutable<User> {

    fun getUserId() : String?

    class Base : CurrentUserLiveDataWrapper, LiveDataWrapper.Abstract<User>() {
        override fun getUserId() = liveData.value?.id
    }
}