package github.detrig.corporatekanbanboard.presentation.boardMain

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import github.detrig.corporatekanbanboard.core.App
import github.detrig.corporatekanbanboard.core.Navigation
import github.detrig.corporatekanbanboard.domain.model.Board
import github.detrig.corporatekanbanboard.domain.model.Column
import github.detrig.corporatekanbanboard.domain.model.Task
import github.detrig.corporatekanbanboard.domain.repository.boards.BoardsRepository
import github.detrig.corporatekanbanboard.presentation.addtask.AddTaskScreen
import github.detrig.corporatekanbanboard.presentation.boards.ClickedBoardLiveDataWrapper
import github.detrig.corporatekanbanboard.presentation.taskInfo.TaskInfoScreen
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BoardMainViewModel(
    private val navigation: Navigation,
    private val boardsRepository: BoardsRepository,
    private val clickedBoardLiveDataWrapper: ClickedBoardLiveDataWrapper,
    private val clickedTaskLiveDataWrapper: ClickedTaskLiveDataWrapper,
    private val columnToAddLiveDataWrapper : ColumnToAddLiveDataWrapper,
    private val viewModelScope: CoroutineScope,
    private val dispatcherMain: CoroutineDispatcher = Dispatchers.Main,
    private val dispatcherIo: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    fun updateTasks(columnId: String, newTasksList: List<Task>) {
        val currentBoard = currentBoard().value ?: Board()
        val updatedColumns = currentBoard.columns.map { column ->
            if (column.id == columnId) column.copy(tasks = newTasksList)
            else column
        }
        clickedBoardLiveDataWrapper.update(currentBoard.copy(columns = updatedColumns))
        viewModelScope.launch(dispatcherIo) {
            boardsRepository.updateBoardRemote(App.currentUserId, currentBoard.copy(columns = updatedColumns))
        }
    }

    fun clickedTaskScreen(task: Task) {
        navigation.update(TaskInfoScreen)
    }

    fun addTaskScreen(column: Column) {
        columnToAddLiveDataWrapper.update(column)
        navigation.update(AddTaskScreen)
    }

    fun currentBoard() = clickedBoardLiveDataWrapper.liveData()
}