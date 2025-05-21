package github.detrig.corporatekanbanboard.data.remote.user

import github.detrig.corporatekanbanboard.core.Result
import github.detrig.corporatekanbanboard.domain.model.User
import github.detrig.corporatekanbanboard.domain.repository.user.RemoteUserBoardDataSource
import github.detrig.corporatekanbanboard.domain.repository.user.UserBoardRepository

class UserBoardRepositoryImpl(
    private val remoteUserDataSource: RemoteUserBoardDataSource
) : UserBoardRepository {
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

    override suspend fun doesUserExistByEmail(email: String): Result<User?> {
        return try {
            val user = remoteUserDataSource.getUserByEmail(email)
            Result.Success(user)
        } catch(e : Exception) {
            Result.Error("Failed to check user exist: ${e.message}")
        }
    }

}