package github.detrig.corporatekanbanboard.presentation.addBoard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import github.detrig.corporatekanbanboard.authentication.domain.utils.CurrentUserLiveDataWrapper
import github.detrig.corporatekanbanboard.core.Result
import github.detrig.corporatekanbanboard.domain.model.Board
import github.detrig.corporatekanbanboard.domain.model.BoardAccess
import github.detrig.corporatekanbanboard.domain.model.BoardMember
import github.detrig.corporatekanbanboard.domain.model.User
import github.detrig.corporatekanbanboard.domain.repository.boards.BoardsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddBoardViewModel(
    private val boardsRepository: BoardsRepository,
    private val currentUserLiveDataWrapper: CurrentUserLiveDataWrapper,
    private val viewModelScope: CoroutineScope,
    private val dispatcherMain: CoroutineDispatcher = Dispatchers.Main,
    private val dispatcherIo: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun addBoard(board: Board) {
        viewModelScope.launch(dispatcherIo) {
            val user = currentUserLiveDataWrapper.liveData().value ?: User()
            val fullBoard = board.copy(
                creatorId = user.id, creatorEmail = user.email, members = listOf(
                    BoardMember(user, BoardAccess.ADMIN)
                )
            )
            val result = boardsRepository.addBoardRemote(user.id, fullBoard)
            when (result) {
                is Result.Success -> {}
                is Result.Error -> _error.postValue(result.message)
            }
        }
    }
}