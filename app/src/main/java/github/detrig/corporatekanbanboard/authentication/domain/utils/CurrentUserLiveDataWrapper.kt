package com.example.disputer.authentication.domain.utils

import com.example.disputer.authentication.data.AuthUser
import com.example.disputer.core.LiveDataWrapper

interface CurrentUserLiveDataWrapper : LiveDataWrapper.Mutable<AuthUser> {

    class Base : CurrentUserLiveDataWrapper, LiveDataWrapper.Abstract<AuthUser>()
}