package github.detrig.corporatekanbanboard.presentation.addtask

import android.util.Log
import androidx.lifecycle.ViewModel
import github.detrig.corporatekanbanboard.core.App
import github.detrig.corporatekanbanboard.core.Navigation
import github.detrig.corporatekanbanboard.core.Result
import github.detrig.corporatekanbanboard.core.Screen
import github.detrig.corporatekanbanboard.domain.model.Board
import github.detrig.corporatekanbanboard.domain.model.Column
import github.detrig.corporatekanbanboard.domain.model.Task
import github.detrig.corporatekanbanboard.domain.repository.boards.BoardsRepository
import github.detrig.corporatekanbanboard.presentation.boardMain.ColumnToAddLiveDataWrapper
import github.detrig.corporatekanbanboard.presentation.boards.ClickedBoardLiveDataWrapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddTaskViewModel(
    private val navigation: Navigation,
    private val boardsRepository: BoardsRepository,
    private val clickedBoardLiveDataWrapper: ClickedBoardLiveDataWrapper,
    private val columnToAddLiveDataWrapper: ColumnToAddLiveDataWrapper,
    private val viewModelScope: CoroutineScope,
    private val dispatcherMain: CoroutineDispatcher = Dispatchers.Main,
    private val dispatcherIo: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    fun currentColumn() = columnToAddLiveDataWrapper.liveData()

    fun addTasks(task: Task) {
        val column = currentColumn().value ?: Column()
        val board = clickedBoardLiveDataWrapper.liveData().value ?: Board()
        Log.d("alz04", "boardTitle: ${board.title}, columnId: ${column.id}, task: $task")
        viewModelScope.launch(dispatcherIo) {
            val result =
                boardsRepository.updateTaskInBoard(App.currentUserId, board, column.id, task)
            withContext(dispatcherMain) {
                when (result) {
                    is Result.Success -> {
                        clickedBoardLiveDataWrapper.update(result.data)
                        navigation.update(Screen.Pop)
                    }
                    is Result.Error -> Log.d("alz04", "error while adding task ${task.title}")
                }
            }
        }
    }
}