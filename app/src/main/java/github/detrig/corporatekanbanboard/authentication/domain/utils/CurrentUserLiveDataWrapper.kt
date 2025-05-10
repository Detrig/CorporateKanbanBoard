package github.detrig.corporatekanbanboard.authentication.domain.utils

import github.detrig.corporatekanbanboard.core.LiveDataWrapper
import github.detrig.corporatekanbanboard.domain.model.User

interface CurrentUserLiveDataWrapper : LiveDataWrapper.Mutable<User> {

    class Base : CurrentUserLiveDataWrapper, LiveDataWrapper.Abstract<User>()
}