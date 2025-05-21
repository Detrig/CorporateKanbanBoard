package github.detrig.corporatekanbanboard.presentation.boardSettings

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import github.detrig.corporatekanbanboard.core.App
import github.detrig.corporatekanbanboard.core.Navigation
import github.detrig.corporatekanbanboard.core.Result
import github.detrig.corporatekanbanboard.core.Screen
import github.detrig.corporatekanbanboard.domain.model.Board
import github.detrig.corporatekanbanboard.domain.model.BoardMember
import github.detrig.corporatekanbanboard.domain.model.User
import github.detrig.corporatekanbanboard.domain.repository.boards.BoardsRepository
import github.detrig.corporatekanbanboard.domain.repository.user.UserBoardRepository
import github.detrig.corporatekanbanboard.presentation.boards.BoardsScreen
import github.detrig.corporatekanbanboard.presentation.boards.ClickedBoardLiveDataWrapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BoardSettingsViewModel(
    private val navigation: Navigation,
    private val boardsRepository: BoardsRepository,
    private val userRepository: UserBoardRepository,
    private val currentBoardLiveDataWrapper: ClickedBoardLiveDataWrapper,
    private val viewModelScope: CoroutineScope,
    private val dispatcherMain: CoroutineDispatcher = Dispatchers.Main,
    private val dispatcherIo: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _isUserExistLiveData = MutableLiveData<Pair<String, User?>>()
    val isUserExistLiveData : LiveData<Pair<String, User?>> = _isUserExistLiveData

    fun updateBoardAndUsers(members: List<BoardMember>, board: Board) {
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
            members.forEach { userRepository.addBoardToUser(it.user.id, board.id) }
        }
    }

    fun deleteUserFromBoard(user: User) {
        viewModelScope.launch(dispatcherIo) {
            val updatedBoardMemberList = currentBoard().members.filter { it.user.id != user.id }
            val updatedBoard = currentBoard().copy(members = updatedBoardMemberList)

            userRepository.deleteBoard(user.id, currentBoard().id)
            val deleteUserResult = boardsRepository.updateBoardRemote(App.currentUserId, updatedBoard)
            when(deleteUserResult) {
                is Result.Success -> {
                    withContext(dispatcherMain) {
                        currentBoardLiveDataWrapper.update(updatedBoard)
                    }
                }
                is Result.Error -> {
                    Log.e("alz04", "Failed to update board: ${deleteUserResult.message}")
                }
            }
        }
    }

    fun deleteCurrentBoard() {
        viewModelScope.launch(dispatcherIo) {
            if (App.currentUserEmail == currentBoard().creatorEmail) {
                val result = boardsRepository.deleteBoardRemote(App.currentUserId, currentBoard().id)

                when(result) {
                    is Result.Success -> {
                        withContext(dispatcherMain) {
                            navigation.update(BoardsScreen)
                        }
                    }
                    is Result.Error -> {

                    }
                }
            }
        }
    }

    fun isUserExistByEmail(email: String) {
        viewModelScope.launch(dispatcherIo) {
            val exists = userRepository.doesUserExistByEmail(email)
            when(exists) {
                is Result.Success -> {
                    _isUserExistLiveData.postValue(Pair(email, exists.data))
                }
                is Result.Error ->  {

                }
            }

        }
    }

    fun currentBoard() = currentBoardLiveDataWrapper.liveData().value ?: Board()

    fun currentBoardLiveData() = currentBoardLiveDataWrapper.liveData()
}