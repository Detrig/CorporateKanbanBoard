package github.detrig.corporatekanbanboard.presentation.taskInfo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import github.detrig.corporatekanbanboard.core.App
import github.detrig.corporatekanbanboard.core.Navigation
import github.detrig.corporatekanbanboard.domain.model.Board
import github.detrig.corporatekanbanboard.domain.model.Comment
import github.detrig.corporatekanbanboard.domain.model.Task
import github.detrig.corporatekanbanboard.domain.repository.boards.BoardsRepository
import github.detrig.corporatekanbanboard.presentation.boardMain.ClickedTaskLiveDataWrapper
import github.detrig.corporatekanbanboard.presentation.boards.ClickedBoardLiveDataWrapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import github.detrig.corporatekanbanboard.core.Result
import github.detrig.corporatekanbanboard.core.Screen
import kotlinx.coroutines.withContext
import java.util.UUID

class TaskInfoViewModel(
    private val navigation: Navigation,
    private val boardsRepository: BoardsRepository,
    private val clickedBoardLiveDataWrapper: ClickedBoardLiveDataWrapper,
    private val clickedTaskLiveDataWrapper: ClickedTaskLiveDataWrapper,
    private val viewModelScope: CoroutineScope,
    private val dispatcherMain: CoroutineDispatcher = Dispatchers.Main,
    private val dispatcherIo: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    fun acceptCompletedTask() {
        if (App.currentUserId == currentTask().authorId) {
            val updatedTask = currentTask().copy(taskProgress = TaskProgress.DONE)
            updateTask(updatedTask)
        }
    }

    fun addComment(comment: Comment) {
        viewModelScope.launch(dispatcherIo) {
            val updatedComments = currentTask().comments.plus(
                comment.copy(
                    id = UUID.randomUUID().toString(),
                    authorId = App.currentUserId,
                    authorEmail = App.currentUserEmail,
                    authorName = App.currentUserName,
                    taskId = currentTask().id
                )
            )
            val updatedTask = currentTask().copy(comments = ArrayList(updatedComments))
            updateTask(updatedTask)
        }
    }

    private fun updateTask(task: Task) {
        val result = boardsRepository.updateTaskInBoard(
            App.currentUserId,
            clickedBoardLiveDataWrapper.liveData().value ?: Board(),
            currentTask().columnId,
            task
        )
        when (result) {
            is Result.Success -> {
                withContext(dispatcherMain) {
                    clickedBoardLiveDataWrapper.update(result.data)
                    clickedTaskLiveDataWrapper.update(task)
                }
            }

            is Result.Error -> {
                _message.postValue("На данный момент добавление комментариев недоступно")
            }
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch(dispatcherIo) {
            val result = boardsRepository.deleteTask(
                App.currentUserId,
                clickedBoardLiveDataWrapper.liveData().value ?: Board(),
                currentTask().columnId,
                taskId
            )
            when(result) {
                is Result.Success -> {
                    withContext(dispatcherMain) {
                        _message.postValue("Задача успешно удалена!")
                        clickedBoardLiveDataWrapper.update(result.data)
                        navigation.update(Screen.Pop)
                    }
                }
                is Result.Error -> {
                    _message.postValue("Ошибка удаления задачи: ${result.message}")
                }
            }
        }
    }

    private fun currentTask() = currentTaskLiveData().value ?: Task()

    fun currentTaskLiveData() = clickedTaskLiveDataWrapper.liveData()
}