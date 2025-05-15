package github.detrig.corporatekanbanboard.domain.model

data class Task(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val creator: String = "",
    val dateCreated: String = "",
    val taskProgress: TaskProgress = TaskProgress.IN_WORK,
    val priority: Priority = Priority.LOW_EMERGENCY,
    val photosBase64: List<String> = emptyList(),
    val comments: ArrayList<Comment> = arrayListOf(),
    val columnId: String = "",
    val position: Int = 0
)

enum class TaskProgress {
    DONE, NEED_REVIEW, IN_WORK
}

enum class Priority {
    HIGH_EMERGENCY, MEDIUM_EMERGENCY, LOW_EMERGENCY
}