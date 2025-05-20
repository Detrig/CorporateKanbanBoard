package github.detrig.corporatekanbanboard.presentation.taskInfo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import github.detrig.corporatekanbanboard.authentication.domain.utils.CurrentUserLiveDataWrapper
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
import github.detrig.corporatekanbanboard.domain.model.BoardAccess
import github.detrig.corporatekanbanboard.domain.model.Priority
import github.detrig.corporatekanbanboard.domain.model.TaskProgress
import github.detrig.corporatekanbanboard.domain.model.User
import kotlinx.coroutines.withContext
import java.util.UUID

class TaskInfoViewModel(
    private val navigation: Navigation,
    private val boardsRepository: BoardsRepository,
    private val clickedBoardLiveDataWrapper: ClickedBoardLiveDataWrapper,
    private val clickedTaskLiveDataWrapper: ClickedTaskLiveDataWrapper,
    private val currentUserLiveDataWrapper: CurrentUserLiveDataWrapper,
    private val viewModelScope: CoroutineScope,
    private val dispatcherMain: CoroutineDispatcher = Dispatchers.Main,
    private val dispatcherIo: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    fun currentUser() = currentUserLiveDataWrapper.liveData().value ?: User()

    fun currentBoard() = clickedBoardLiveDataWrapper.liveData().value ?: Board()

    fun acceptCompletedTask() {
        if (App.currentUserId == currentTask().creator) {
            val updatedTask = currentTask().copy(taskProgress = TaskProgress.DONE)
            viewModelScope.launch(dispatcherIo) {
                updateTask(updatedTask)
            }
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

    private suspend fun updateTask(task: Task) {
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
                    Log.d("alz-04", "board updated with workers: ${result.data.columns[0].tasks[0].workers.map { it.user.email }}")
                    //navigation.update(Screen.Pop)
                }
            }

            is Result.Error -> {
                _message.postValue("На данный момент добавление комментариев недоступно")
            }
        }
    }

    fun updateTaskUniversal(task: Task) {
        viewModelScope.launch(dispatcherIo) {
            updateTask(task)
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

    fun getUserRoleForCurrentBoard() : BoardAccess {
        var userRoleForCurrentBoard = BoardAccess.VIEWER
        clickedBoardLiveDataWrapper.liveData().value?.members?.forEach {
            if (it.user.id == App.currentUserId)
                userRoleForCurrentBoard = it.access
        }
        return userRoleForCurrentBoard
    }

    fun currentTask() = currentTaskLiveData().value ?: Task()

    fun currentTaskLiveData() = clickedTaskLiveDataWrapper.liveData()
}