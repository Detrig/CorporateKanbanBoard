package github.detrig.corporatekanbanboard.domain.repository.chat

import github.detrig.corporatekanbanboard.domain.model.chat.GlobalChatMessage

interface ChatRepository {
    suspend fun sendMessage(message: GlobalChatMessage): Result<Unit>
    suspend fun loadLatestMessages(limit: Int = 50): Result<List<GlobalChatMessage>>
    suspend fun loadPreviousMessages(beforeMessageId: String, limit: Int = 50): Result<List<GlobalChatMessage>>
}