package com.example.disputer.authentication.data

import android.util.Log
import com.example.disputer.authentication.domain.repository.CurrentUserRepository
import com.example.disputer.coach.domain.repo.CoachDataSource
import com.example.disputer.core.Resource
import com.example.disputer.parent.domain.repository.ParentDataSource
import com.google.firebase.auth.FirebaseAuth

class FirebaseCurrentDataSource(
    private val auth: FirebaseAuth,
    private val coachesDataSource: CoachDataSource,
    private val parentsDataSource: ParentDataSource
) : CurrentUserRepository {

    override suspend fun getCurrentUser(): Resource<AuthUser> {
        return try {
            val uid = auth.currentUser?.uid ?: return Resource.Error("User not authenticated")

            coachesDataSource.getCoachById(uid).let { coachResource ->
                if (coachResource is Resource.Success && coachResource.data != null) {
                    val authUser = AuthUser.CoachUser(coachResource.data)
                    return Resource.Success(authUser)
                }
            }

            parentsDataSource.getParent(uid).let { parentResource ->
                if (parentResource is Resource.Success && parentResource.data != null) {
                    val authUser = AuthUser.ParentUser(parentResource.data)
                    Log.d("VG-01", "parent: $authUser")
                    return Resource.Success(authUser)
                }
            }
            Resource.Error("User not found in database")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch user data")
        }
    }
}