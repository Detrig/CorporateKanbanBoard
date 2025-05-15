package github.detrig.corporatekanbanboard.domain.model

data class Comment(
    val id: String = "",
    val taskId: String = "",
    val authorId: String = "",
    val authorEmail: String = "",
    val authorName: String = "",
    val content: String = "",
    val dateCreated: String = ""
)
