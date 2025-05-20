package github.detrig.corporatekanbanboard.data.remote.boards

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import github.detrig.corporatekanbanboard.domain.model.Board
import github.detrig.corporatekanbanboard.domain.model.BoardMember
import github.detrig.corporatekanbanboard.domain.repository.boards.RemoteBoardsDataSource
import kotlinx.coroutines.tasks.await

class RemoteBoardsDataSourceImpl(
    private val firestore: FirebaseFirestore
) : RemoteBoardsDataSource {

    override suspend fun getBoardsForUser(userId: String): List<Board> {
        val userRef = firestore.collection(USER_KEY).document(userId)
        val userSnapshot = userRef.get().await()

        val boardIds = userSnapshot.get("boardIds") as? List<String> ?: emptyList()

        return boardIds.mapNotNull { boardId ->
            val boardRef = firestore.collection(BOARDS_KEY).document(boardId)
            val boardSnapshot = boardRef.get().await()

            boardSnapshot.toObject(Board::class.java)?.copy(id = boardSnapshot.id)
        }
    }

    override suspend fun addBoard(board: Board): String {
        val docRef = firestore.collection(BOARDS_KEY).document() // генерирует новый ID
        board.id = docRef.id
//        Log.d("lfc", "board to add: $board")
        docRef.set(board).await()
        return docRef.id
    }

    override suspend fun updateBoard(board: Board) {
        firestore.collection(BOARDS_KEY)
            .document(board.id)
            .set(board)
            .await()
    }

    override suspend fun deleteBoard(boardId: String) {
        firestore.collection(BOARDS_KEY)
            .document(boardId)
            .delete()
            .await()
    }

    override suspend fun addUserToBoard(boardId: String, userId: String) {
        firestore.collection(BOARDS_KEY)
            .document(boardId)
            .update("userIds", FieldValue.arrayUnion(userId))
            .await()
    }

    override suspend fun removeUserFromBoard(boardId: String, userId: String) {
        firestore.collection(BOARDS_KEY)
            .document(boardId)
            .update("userIds", FieldValue.arrayRemove(userId))
            .await()
    }

    override suspend fun getMembersForBoard(boardId: String): List<BoardMember> {
        val boardSnapshot = firestore.collection(BOARDS_KEY)
            .document(boardId)
            .get()
            .await()

        val members = boardSnapshot.toObject(Board::class.java)?.members ?: emptyList()
        return members
    }

    companion object {
        private const val BOARDS_KEY = "boards"
        private const val USER_KEY = "users"
    }
}