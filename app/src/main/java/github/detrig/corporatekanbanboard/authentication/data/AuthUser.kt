package com.example.disputer.authentication.data

import com.example.disputer.coach.data.Coach
import com.example.disputer.parent.data.Parent

sealed class AuthUser {
    data class CoachUser(val coach: Coach) : AuthUser()
    data class ParentUser(val parent: Parent) : AuthUser()
    object Empty : AuthUser()
}