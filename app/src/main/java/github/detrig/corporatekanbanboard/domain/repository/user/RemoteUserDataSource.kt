package github.detrig.corporatekanbanboard.domain.repository.user

import github.detrig.corporatekanbanboard.domain.model.User

interface RemoteUserDataSource {
    suspend fun addBoardToUser(userId: String, boardId: String)
    suspend fun deleteBoard(userId: String, boardId: String)
}