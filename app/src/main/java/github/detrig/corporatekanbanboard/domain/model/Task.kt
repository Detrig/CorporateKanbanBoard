package github.detrig.corporatekanbanboard.domain.model

data class Task(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val creator: String = "",
    val workers: List<BoardMember> = emptyList(),
    val dateCreated: String = "",
    val taskProgress: TaskProgress = TaskProgress.IN_WORK,
    val priority: Priority = Priority.LOW_EMERGENCY,
    val photosBase64: List<String> = emptyList(),
    val comments: ArrayList<Comment> = arrayListOf(),
    val columnId: String = "",
    val position: Int = 0
) {
    companion object {
        fun sortedByCustomRules(tasks: List<Task>): List<Task> {
            return tasks.sortedWith(
                compareBy(
                    { task ->
                        when (task.taskProgress) {
                            TaskProgress.NEED_REVIEW -> 0
                            TaskProgress.IN_WORK -> 1
                            TaskProgress.DONE -> 2
                        }
                    },
                    { task ->
                        when (task.priority) {
                            Priority.HIGH_EMERGENCY -> 0
                            Priority.MEDIUM_EMERGENCY -> 1
                            Priority.LOW_EMERGENCY -> 2
                        }
                    }
                ))
        }
    }
}

enum class TaskProgress {
    DONE, NEED_REVIEW, IN_WORK
}

enum class Priority {
    HIGH_EMERGENCY, MEDIUM_EMERGENCY, LOW_EMERGENCY
}

