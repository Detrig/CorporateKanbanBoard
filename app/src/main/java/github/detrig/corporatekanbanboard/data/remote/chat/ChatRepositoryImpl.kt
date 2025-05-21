package github.detrig.corporatekanbanboard.data.remote.chat

import com.google.firebase.database.FirebaseDatabase
import github.detrig.corporatekanbanboard.domain.model.chat.GlobalChatMessage
import github.detrig.corporatekanbanboard.domain.repository.chat.ChatRepository
import kotlinx.coroutines.tasks.await

class ChatRepositoryImpl(
    private val database: FirebaseDatabase
) : ChatRepository {

    private val messagesRef = database.getReference("chatMessages")

    override suspend fun sendMessage(message: GlobalChatMessage): Result<Unit> {
        return try {
            val newRef = messagesRef.push()
            val messageWithId = message.copy(id = newRef.key ?: "")
            newRef.setValue(messageWithId).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loadLatestMessages(limit: Int): Result<List<GlobalChatMessage>> {
        return try {
            val snapshot = messagesRef
                .orderByChild("timestamp")
                .limitToLast(limit)
                .get()
                .await()

            val messages = snapshot.children.mapNotNull { it.getValue(GlobalChatMessage::class.java) }
                .sortedBy { it.timestamp }

            Result.success(messages)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loadPreviousMessages(beforeMessageId: String, limit: Int): Result<List<GlobalChatMessage>> {
        return try {
            val beforeMessageSnapshot = messagesRef.child(beforeMessageId).get().await()
            val beforeTimestamp = beforeMessageSnapshot.getValue(GlobalChatMessage::class.java)?.timestamp ?: return Result.success(emptyList())

            val snapshot = messagesRef
                .orderByChild("timestamp")
                .endBefore(beforeTimestamp.toDouble())
                .limitToLast(limit)
                .get()
                .await()

            val messages = snapshot.children.mapNotNull { it.getValue(GlobalChatMessage::class.java) }
                .sortedBy { it.timestamp }

            Result.success(messages)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
