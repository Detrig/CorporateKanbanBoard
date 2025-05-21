package github.detrig.corporatekanbanboard.presentation.boards

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import github.detrig.corporatekanbanboard.authentication.domain.utils.CurrentUserLiveDataWrapper
import github.detrig.corporatekanbanboard.core.Communication
import github.detrig.corporatekanbanboard.core.Navigation
import github.detrig.corporatekanbanboard.core.Result
import github.detrig.corporatekanbanboard.domain.model.Board
import github.detrig.corporatekanbanboard.domain.model.User
import github.detrig.corporatekanbanboard.domain.repository.boards.BoardsRepository
import github.detrig.corporatekanbanboard.domain.repository.user.UserBoardRepository
import github.detrig.corporatekanbanboard.presentation.addBoard.AddBoardScreen
import github.detrig.corporatekanbanboard.presentation.boardMain.BoardMainScreen
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BoardsViewModel(
    private val navigation: Navigation,
    private val boardsCommunication: Communication<Board>,
    private val currentUserLiveDataWrapper: CurrentUserLiveDataWrapper,
    private val clickedBoardLiveDataWrapper: ClickedBoardLiveDataWrapper,
    private val clickedBoardUsersLiveDataWrapper: ClickedBoardUsersLiveDataWrapper,
    private val boardsRepository: BoardsRepository,
    private val usersRepository: UserBoardRepository,
    private val viewModelScope: CoroutineScope,
    private val dispatcherMain: CoroutineDispatcher = Dispatchers.Main,
    private val dispatcherIo: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _savedBoards = MutableLiveData<List<Board>>()
    val savedBoard: LiveData<List<Board>> = _savedBoards

//    init {
//        getBoards()
//    }

    fun getBoards() {
        viewModelScope.launch(dispatcherIo) {
            val boardsResult =
                boardsRepository.getBoardsForUser(currentUserLiveDataWrapper.getUserId() ?: "")
            when (boardsResult) {
                is Result.Success -> {
                    _savedBoards.postValue(boardsResult.data)
                    boardsCommunication.setData(boardsResult.data)
                }

                is Result.Error -> _error.postValue(boardsResult.message)
            }
        }
    }

    fun clickedBoardScreen(board: Board) {
        viewModelScope.launch(dispatcherIo) {
            val usersList = mutableListOf<User>()
            board.members.forEach {
                val usersResult = usersRepository.doesUserExistByEmail(it.email)
                when (usersResult) {
                    is Result.Success -> {
                        usersList.add(usersResult.data ?: User())
                    }
                    else -> {}
                }
            }
            withContext(dispatcherMain) {
                clickedBoardUsersLiveDataWrapper.update(usersList)
                Log.d("alz-04", "users: ${usersList.map { it.id }}")
            }
        }
        clickedBoardLiveDataWrapper.update(board)
        navigation.update(BoardMainScreen)
    }

    fun clickedBoardLiveData() = clickedBoardLiveDataWrapper.liveData()

    fun observe(owner: LifecycleOwner, observer: Observer<List<Board>>) =
        boardsCommunication.observe(owner, observer)

    fun addBoardScreen() = navigation.update(AddBoardScreen)
}