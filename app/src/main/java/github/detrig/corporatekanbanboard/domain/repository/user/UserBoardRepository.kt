package github.detrig.corporatekanbanboard.domain.repository.user
import github.detrig.corporatekanbanboard.core.Result
import github.detrig.corporatekanbanboard.domain.model.User

interface UserBoardRepository {
    suspend fun addBoardToUser(userId: String, boardId: String) : Result<Unit>
    suspend fun deleteBoard(userId: String, boardId: String) : Result<Unit>
    suspend fun doesUserExistByEmail(email: String): Result<User?>
}