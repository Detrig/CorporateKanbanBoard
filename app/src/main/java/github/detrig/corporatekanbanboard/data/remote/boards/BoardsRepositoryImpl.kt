package github.detrig.corporatekanbanboard.data.remote.boards

import android.util.Log
import github.detrig.corporatekanbanboard.core.Result
import github.detrig.corporatekanbanboard.domain.model.Board
import github.detrig.corporatekanbanboard.domain.model.Task
import github.detrig.corporatekanbanboard.domain.repository.boards.BoardsRepository
import github.detrig.corporatekanbanboard.domain.repository.boards.LocalBoardsDataSource
import github.detrig.corporatekanbanboard.domain.repository.boards.RemoteBoardsDataSource
import github.detrig.corporatekanbanboard.domain.repository.user.RemoteUserDataSource
import java.util.UUID

class BoardsRepositoryImpl(
    private val localBoard: LocalBoardsDataSource,
    private val remoteBoard: RemoteBoardsDataSource,
    private val usersDataSource: RemoteUserDataSource,
) : BoardsRepository {


    override suspend fun getBoardsForUser(userId: String): Result<List<Board>> {
        return try {
            val remoteBoards = remoteBoard.getBoardsForUser(userId)
            localBoard.insertBoards(remoteBoards)
            Result.Success(remoteBoards)
        } catch (e: Exception) {
            //getCachedBoards(userId)
            Result.Error("Failed to get boards: ${e.message}")
        }
    }


    override suspend fun addBoardRemote(userId: String, board: Board): Result<String> {
        return try {
            val id = remoteBoard.addBoard(board)
            usersDataSource.addBoardToUser(userId, id)
            Log.d("lfc", "board added success: $id")
            Result.Success(id)
        } catch (e: Exception) {
            Log.d("lfc", "Failed to add board: ${e.message}")
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

    override suspend fun updateTaskInBoard(
        userId: String,
        board: Board,
        columnId: String,
        updatedTask: Task
    ): Result<Board> {
        return try {
            // 1. Валидация входящих данных
            if (columnId.isBlank()) {
                throw IllegalArgumentException("Column ID cannot be empty")
            }

            // 2. Обновляем структуру данных
            val updatedColumns = board.columns.map { column ->
                if (column.id == columnId) {
                    // Для новой задачи (без ID или с пустым ID) генерируем новый ID
                    val taskToAdd = if (updatedTask.id.isBlank()) {
                        updatedTask.copy(id = UUID.randomUUID().toString())
                    } else {
                        updatedTask
                    }

                    // Фильтруем существующие задачи (удаляем если есть дубликат по ID)
                    val filteredTasks = column.tasks.filterNot { it.id == taskToAdd.id }

                    // Добавляем новую/обновленную задачу в начало списка
                    val updatedTasks = listOf(taskToAdd) + filteredTasks

                    column.copy(tasks = updatedTasks)
                } else {
                    column
                }
            }

            // 3. Создаем обновленный Board
            val updatedBoard = board.copy(columns = updatedColumns)

            // 4. Сохраняем в базу данных
            Log.d("DB_UPDATE", "Updating board with ${updatedBoard.columns.find { it.id == columnId }?.tasks?.size} tasks in column $columnId")
            updateBoardRemote(userId, updatedBoard).let {
                Log.d("DB_UPDATE", "Update successful for board ${updatedBoard.id}")
                Result.Success(updatedBoard)
            }
        } catch (e: Exception) {
            Log.e("DB_UPDATE", "Error updating board", e)
            Result.Error("Failed to update task: ${e.message}")
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

    private suspend fun getCachedBoards(userId: String): Result<List<Board>> {
        return try {
            val cachedBoards = localBoard.getBoards()
            if (cachedBoards.isNotEmpty()) {
                Result.Success(cachedBoards)
            } else {
                Result.Error("No cached data")
            }
        } catch (e: Exception) {
            Result.Error("Cache error: ${e.message}")
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