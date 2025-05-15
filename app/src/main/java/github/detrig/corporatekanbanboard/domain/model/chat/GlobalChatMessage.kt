package github.detrig.corporatekanbanboard.domain.model.chat

data class GlobalChatMessage(
    val id: String = "",
    val text: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val timestamp: Long = 0L
)