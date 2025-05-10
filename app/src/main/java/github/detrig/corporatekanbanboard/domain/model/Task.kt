package github.detrig.corporatekanbanboard.domain.model

data class Task(
    val id: String,
    val title: String,
    val description: String,
    val photosBase64: List<String>,
    val comments: List<Comment>,
)