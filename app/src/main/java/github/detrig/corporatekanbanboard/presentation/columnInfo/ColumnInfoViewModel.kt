package github.detrig.corporatekanbanboard.presentation.columnInfo

import android.util.Log
import androidx.lifecycle.ViewModel
import github.detrig.corporatekanbanboard.core.App
import github.detrig.corporatekanbanboard.core.Navigation
import github.detrig.corporatekanbanboard.core.Result
import github.detrig.corporatekanbanboard.core.Screen
import github.detrig.corporatekanbanboard.domain.model.Board
import github.detrig.corporatekanbanboard.domain.model.Column
import github.detrig.corporatekanbanboard.domain.repository.boards.BoardsRepository
import github.detrig.corporatekanbanboard.presentation.boards.ClickedBoardLiveDataWrapper
import github.detrig.corporatekanbanboard.presentation.boards.ClickedBoardUsersLiveDataWrapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ColumnInfoViewModel(
    private val navigation: Navigation,
    private val boardsRepository: BoardsRepository,
    private val clickedColumnLiveDataWrapper: ClickedColumnLiveDataWrapper,
    private val clickedBoardLiveDataWrapper: ClickedBoardLiveDataWrapper,
    private val clickedBoardUsersLiveDataWrapper: ClickedBoardUsersLiveDataWrapper,
    private val viewModelScope: CoroutineScope,
    private val dispatcherMain: CoroutineDispatcher = Dispatchers.Main,
    private val dispatcherIo: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    fun updateColumn(column: Column) {
        viewModelScope.launch(dispatcherIo) {
            val updatedColumnsList = currentBoard().columns.map { if (it.id == column.id) column else it }
            val updatedBoard = currentBoard().copy(columns = updatedColumnsList)
            val result = boardsRepository.updateBoardRemote(App.currentUserId, updatedBoard)
            Log.d("alz-04", "result: $result")
            when(result) {
                is Result.Success -> {
                    withContext(dispatcherMain) {
                        Log.d("alz-04", "column updated successfully")
                        clickedBoardLiveDataWrapper.update(updatedBoard)
                        clickedColumnLiveDataWrapper.update(column)
                        navigation.update(Screen.Pop)
                    }
                }
                is Result.Error -> {}
            }
        }
    }

    fun currentColumn() = clickedColumnLiveDataWrapper.liveData().value ?: Column()
    fun currentColumnLiveData() = clickedColumnLiveDataWrapper.liveData()

    fun currentBoard() = clickedBoardLiveDataWrapper.liveData().value ?: Board()

    fun getUsers() = clickedBoardUsersLiveDataWrapper.liveData().value ?: emptyList()
}