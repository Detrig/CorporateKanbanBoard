package github.detrig.corporatekanbanboard.domain.repository.boards

import github.detrig.corporatekanbanboard.domain.model.Board

interface LocalBoardsDataSource {
    suspend fun getBoards(): List<Board>
    suspend fun getBoardById(id: String): Board?
    suspend fun insertBoard(board: Board)
    suspend fun insertBoards(boards: List<Board>)
    suspend fun updateBoard(board: Board)
    suspend fun deleteBoard(id: String)
    suspend fun clearAll()
}