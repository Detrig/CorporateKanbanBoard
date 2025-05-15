package github.detrig.corporatekanbanboard.presentation.boardSettings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import github.detrig.corporatekanbanboard.core.App
import github.detrig.corporatekanbanboard.core.Navigation
import github.detrig.corporatekanbanboard.core.Result
import github.detrig.corporatekanbanboard.core.Screen
import github.detrig.corporatekanbanboard.domain.model.Board
import github.detrig.corporatekanbanboard.domain.repository.boards.BoardsRepository
import github.detrig.corporatekanbanboard.presentation.boards.ClickedBoardLiveDataWrapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BoardSettingsViewModel(
    private val navigation: Navigation,
    private val boardsRepository: BoardsRepository,
    private val currentBoardLiveDataWrapper: ClickedBoardLiveDataWrapper,
    private val viewModelScope: CoroutineScope,
    private val dispatcherMain: CoroutineDispatcher = Dispatchers.Main,
    private val dispatcherIo: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    fun updateBoard(board: Board) {
        viewModelScope.launch(dispatcherIo) {
            val result = boardsRepository.updateBoardRemote(App.currentUserId, board)

            withContext(dispatcherMain) {
                when (result) {
                    is Result.Success -> {
                        currentBoardLiveDataWrapper.update(board)
                        navigation.update(Screen.Pop)
                    }

                    is Result.Error -> {
                        _message.postValue("Невозможно сохранить изменения")
                    }
                }
            }
        }
    }

    fun currentBoardLiveData() = currentBoardLiveDataWrapper.liveData()
}