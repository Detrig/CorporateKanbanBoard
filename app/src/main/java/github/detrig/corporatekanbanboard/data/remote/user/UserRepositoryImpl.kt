package github.detrig.corporatekanbanboard.data.remote.user

import github.detrig.corporatekanbanboard.domain.repository.user.RemoteUserDataSource
import github.detrig.corporatekanbanboard.domain.repository.user.UserRepository
import github.detrig.corporatekanbanboard.core.Result

class UserRepositoryImpl(
    private val remoteUserDataSource: RemoteUserDataSource
) : UserRepository {
    override suspend fun addBoardToUser(
        userId: String,
        boardId: String
    ): Result<Unit> {
        return try {
            remoteUserDataSource.addBoardToUser(userId, boardId)
            Result.Success(Unit)
        } catch (e : Exception) {
            Result.Error("Failed to add board to user: ${e.message}")
        }
    }

    override suspend fun deleteBoard(
        userId: String,
        boardId: String
    ): Result<Unit> {
        return try {
            remoteUserDataSource.deleteBoard(userId, boardId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to delete board: ${e.message}")
        }
    }

}