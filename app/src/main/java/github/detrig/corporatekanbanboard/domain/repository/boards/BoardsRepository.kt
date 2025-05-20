package github.detrig.corporatekanbanboard.domain.repository.boards

import github.detrig.corporatekanbanboard.domain.model.Board
import github.detrig.corporatekanbanboard.core.Result
import github.detrig.corporatekanbanboard.domain.model.BoardMember
import github.detrig.corporatekanbanboard.domain.model.Task

interface BoardsRepository {

    // ===== REMOTE (Firebase) OPERATIONS =====

    /**
     * Загружает доски пользователя с сервера и обновляет локальный кэш.
     */
    suspend fun getBoardsForUser(userId: String): Result<List<Board>>

    /**
     * Добавляет новую доску для пользователя (создателя) и возвращает ID созданной доски.
     */
    suspend fun addBoardRemote(userId: String, board: Board): Result<String>

    /**
     * Обновляет онлайн доску.
     */
    suspend fun updateBoardRemote(userId: String, board: Board): Result<Unit>

    suspend fun updateTaskInBoard(
        userId: String,
        board: Board,
        columnId: String,
        updatedTask: Task
    ): Result<Board>

    suspend fun deleteTask(
        userId: String,
        board: Board,
        columnId: String,
        taskId: String
    ) : Result<Board>
    /**
     * Удаляет доску пользователя.
     */
    suspend fun deleteBoardRemote(userId: String, boardId: String): Result<Unit>

    /**
     * Добавляет пользователя к доске (например, по приглашению).
     */
    suspend fun addUserToBoardRemote(boardId: String, userId: String): Result<Unit>

    /**
     * Удаляет пользователя из доски.
     */
    suspend fun removeUserFromBoardRemote(boardId: String, userId: String): Result<Unit>

    suspend fun getMembersForBoard(boardId: String) : Result<List<BoardMember>>

    // ===== LOCAL (Room cache) OPERATIONS =====

    suspend fun getBoardsLocal(): List<Board>

    suspend fun getBoardByIdLocal(boardId: String): Board?

    suspend fun insertBoardLocal(board: Board)

    suspend fun insertBoardsLocal(boards: List<Board>)

    suspend fun updateBoardLocal(board: Board)

    suspend fun deleteBoardLocal(boardId: String)

    suspend fun clearLocalCache()
}