package github.detrig.corporatekanbanboard.data.remote.boards

import github.detrig.corporatekanbanboard.core.Result
import github.detrig.corporatekanbanboard.domain.model.Board
import github.detrig.corporatekanbanboard.domain.repository.boards.BoardsRepository
import github.detrig.corporatekanbanboard.domain.repository.boards.LocalBoardsDataSource
import github.detrig.corporatekanbanboard.domain.repository.boards.RemoteBoardsDataSource
import github.detrig.corporatekanbanboard.domain.repository.user.RemoteUserDataSource

class BoardsRepositoryImpl(
    private val localBoard: LocalBoardsDataSource,
    private val remoteBoard: RemoteBoardsDataSource,
    private val usersDataSource: RemoteUserDataSource
) : BoardsRepository {

    override suspend fun getBoardsForUser(userId: String): Result<List<Board>> {
        return try {
            val remoteBoards = remoteBoard.getBoardsForUser(userId)

            // Сохраняем их локально (опционально, если нужно синхронизировать)
            localBoard.clearAll()
            localBoard.insertBoards(remoteBoards)

            Result.Success(remoteBoards)
        } catch (e: Exception) {
            Result.Error("Failed to sync boards: ${e.message}")
        }
    }

    override suspend fun addBoardRemote(userId: String, board: Board): Result<String> {
        return try {
            val id = remoteBoard.addBoard(board)
            usersDataSource.addBoardToUser(userId, id)
            Result.Success(id)
        } catch (e: Exception) {
            Result.Error("Failed to add board: ${e.message}")
        }
    }

    override suspend fun updateBoardRemote(
        userId: String,
        board: Board
    ): Result<Unit> {
        return try {
            remoteBoard.updateBoard(board)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to update board: ${e.message}")
        }
    }

    override suspend fun deleteBoardRemote(
        userId: String,
        boardId: String
    ): Result<Unit> {
        return try {
            remoteBoard.deleteBoard(boardId)
            usersDataSource.deleteBoard(userId, boardId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to delete board: ${e.message}")
        }
    }

    override suspend fun addUserToBoardRemote(
        boardId: String,
        userId: String
    ): Result<Unit> {
        return try {
            remoteBoard.addUserToBoard(boardId, userId)
            usersDataSource.addBoardToUser(userId, boardId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to add user to board: ${e.message}")
        }
    }

    override suspend fun removeUserFromBoardRemote(
        boardId: String,
        userId: String
    ): Result<Unit> {
        return try {
            remoteBoard.removeUserFromBoard(boardId, userId)
            usersDataSource.deleteBoard(userId, boardId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to remove user from board: ${e.message}")
        }
    }

    override suspend fun getBoardsLocal(): List<Board> {
        return localBoard.getBoards()
    }

    override suspend fun getBoardByIdLocal(boardId: String): Board? {
        return localBoard.getBoardById(boardId)
    }

    override suspend fun insertBoardLocal(board: Board) {
        localBoard.insertBoard(board)
    }

    override suspend fun insertBoardsLocal(boards: List<Board>) {
        localBoard.insertBoards(boards)
    }

    override suspend fun updateBoardLocal(board: Board) {
        localBoard.updateBoard(board)
    }

    override suspend fun deleteBoardLocal(boardId: String) {
        localBoard.deleteBoard(boardId)
    }

    override suspend fun clearLocalCache() {
        localBoard.clearAll()
    }
}