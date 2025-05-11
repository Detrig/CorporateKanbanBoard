package github.detrig.corporatekanbanboard.domain.repository.user
import github.detrig.corporatekanbanboard.core.Result

interface UserRepository {
    suspend fun addBoardToUser(userId: String, boardId: String) : Result<Unit>
    suspend fun deleteBoard(userId: String, boardId: String) : Result<Unit>
}