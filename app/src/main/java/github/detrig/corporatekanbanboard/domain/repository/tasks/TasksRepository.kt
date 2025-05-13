package github.detrig.corporatekanbanboard.domain.repository.tasks

import github.detrig.corporatekanbanboard.domain.model.Task

interface TasksRepository {

    suspend fun getTaskById(taskId: String) : Result<Unit>

    suspend fun getTasksForColumn(columnId: String) : Result<List<Task>>

    suspend fun addTask(columnId: String, task: Task) : Result<String> //return task id

    suspend fun deleteTask(columnId: String, taskId: String) : Result<Task>
}