package github.detrig.corporatekanbanboard.presentation.boards

import androidx.lifecycle.ViewModel
import github.detrig.corporatekanbanboard.core.BaseCommunication
import github.detrig.corporatekanbanboard.core.Navigation
import github.detrig.corporatekanbanboard.domain.model.Board
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.Dispatcher

class BoardsViewModel(
    private val navigation: Navigation,
    private val boards: BaseCommunication<List<Board>>,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {


}