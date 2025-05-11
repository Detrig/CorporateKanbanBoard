package github.detrig.corporatekanbanboard.domain.model

data class Task(
    val id: String,
    val title: String,
    val description: String,
    val creator: String,
    val dateCreated: String,
    val taskProgress: TaskProgress = TaskProgress.IN_WORK,
    val priority: Priority = Priority.LOW_EMERGENCY,
    val photosBase64: List<String>,
    val comments: List<Comment>,
)

enum class TaskProgress {
    DONE, NEED_REVIEW, IN_WORK
}

enum class Priority {
    EMERGENCY, MEDIUM_EMERGENCY, LOW_EMERGENCY
}