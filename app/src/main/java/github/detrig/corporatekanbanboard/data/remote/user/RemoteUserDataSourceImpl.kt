package github.detrig.corporatekanbanboard.data.remote.user

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import github.detrig.corporatekanbanboard.domain.repository.user.RemoteUserDataSource
import kotlinx.coroutines.tasks.await

class RemoteUserDataSourceImpl(
    private val firestore: FirebaseFirestore
) : RemoteUserDataSource {

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

    companion object {
        private const val USERS_KEY = "users"
    }
}