package github.detrig.corporatekanbanboard.domain.repository.boards

import github.detrig.corporatekanbanboard.domain.model.Board
import github.detrig.corporatekanbanboard.domain.model.BoardMember

interface RemoteBoardsDataSource {
    suspend fun getBoardsForUser(userId: String): List<Board>
    suspend fun addBoard(board: Board): String
    suspend fun updateBoard(board: Board)
    suspend fun deleteBoard(boardId: String)
    suspend fun addUserToBoard(boardId: String, userId: String)
    suspend fun removeUserFromBoard(boardId: String, userId: String)
    suspend fun getMembersForBoard(boardId: String): List<BoardMember>
}