package github.detrig.corporatekanbanboard.data.local.datasource

import github.detrig.corporatekanbanboard.data.local.dao.BoardsDao
import github.detrig.corporatekanbanboard.domain.model.Board
import github.detrig.corporatekanbanboard.domain.repository.boards.LocalBoardsDataSource

class LocalBoardsDataSourceImpl(
    private val dao: BoardsDao
) : LocalBoardsDataSource {

    override suspend fun getBoards(): List<Board> = dao.getAllBoards().map { it.toDomain() }

    override suspend fun getBoardById(id: String): Board? = dao.getBoardById(id)?.toDomain()

    override suspend fun insertBoard(board: Board) = dao.insertBoard(board.toEntity())

    override suspend fun insertBoards(boards: List<Board>) = dao.insertBoards(boards.map { it.toEntity() })

    override suspend fun updateBoard(board: Board) = dao.updateBoard(board.toEntity())

    override suspend fun deleteBoard(id: String) = dao.deleteBoardById(id)

    override suspend fun clearAll() = dao.clearAll()
}