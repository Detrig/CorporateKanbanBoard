package github.detrig.corporatekanbanboard.data.remote.user

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import github.detrig.corporatekanbanboard.domain.model.User
import github.detrig.corporatekanbanboard.domain.repository.user.RemoteUserBoardDataSource
import kotlinx.coroutines.tasks.await

class RemoteUserBoardDataSourceImpl(
    private val firestore: FirebaseFirestore
) : RemoteUserBoardDataSource {

    override suspend fun addBoardToUser(userId: String, boardId: String) {
        firestore.collection(USERS_KEY)
            .document(userId)
            .update("boardIds", FieldValue.arrayUnion(boardId))
            .await()
    }

    override suspend fun deleteBoard(userId: String, boardId: String) {
        firestore.collection(USERS_KEY)
            .document(userId)
            .update("boardIds", FieldValue.arrayRemove(boardId))
            .await()
    }

    override suspend fun getUserByEmail(email: String): User? {
        return try {
            val querySnapshot = firestore.collection(USERS_KEY)
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .await()

            val document = querySnapshot.documents.firstOrNull()
            document?.toObject(User::class.java)
        } catch (e: Exception) {
            Log.e("FIRESTORE", "Error getting user by email", e)
            null
        }
    }

    companion object {
        private const val USERS_KEY = "users"
    }
}