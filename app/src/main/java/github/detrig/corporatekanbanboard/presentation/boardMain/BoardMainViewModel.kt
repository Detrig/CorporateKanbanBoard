package github.detrig.corporatekanbanboard.presentation.boardMain

import androidx.lifecycle.ViewModel
import github.detrig.corporatekanbanboard.core.Communication
import github.detrig.corporatekanbanboard.core.Navigation
import github.detrig.corporatekanbanboard.domain.model.Board
import github.detrig.corporatekanbanboard.domain.repository.boards.BoardsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BoardMainViewModel(
    private val navigation: Navigation,
    private val clickedBoard:
    private val boardsRepository: BoardsRepository,
    private val viewModelScope: CoroutineScope,
    private val dispatcherMain: CoroutineDispatcher = Dispatchers.Main,
    private val dispatcherIo: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

}