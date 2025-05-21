package github.detrig.corporatekanbanboard.authentication.data

import android.util.Log
import github.detrig.corporatekanbanboard.authentication.domain.repository.CurrentUserRepository
import github.detrig.corporatekanbanboard.core.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import github.detrig.corporatekanbanboard.domain.model.User
import kotlinx.coroutines.tasks.await

class FirebaseCurrentDataSource(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : CurrentUserRepository {

    override suspend fun getCurrentUser(): Resource<User> {
        return try {
            val uid = auth.currentUser?.uid ?: return Resource.Error("User not authenticated")

            val document = firestore.collection(USER_COLLECTION)
                .document(uid)
                .get()
                .await()
            if (document.exists()) {
                document.toObject(User::class.java)?.let { user ->
                    Resource.Success(user.copy(id = document.id))
                } ?: Resource.Error("Failed to parse user data")
            } else {
                Resource.Error("User not found in database")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch user data")
        }
    }

    override suspend fun updateCurrentUser(user: User): Resource<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Resource.Error("User not authenticated")

            firestore.collection(USER_COLLECTION)
                .document(uid)
                .set(user.copy(id = uid))
                .await()

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update user")
        }
    }

    private companion object {
        const val USER_COLLECTION = "users"
    }
}