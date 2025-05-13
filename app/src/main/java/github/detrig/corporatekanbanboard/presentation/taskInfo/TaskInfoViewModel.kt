package github.detrig.corporatekanbanboard.presentation.taskInfo

import androidx.lifecycle.ViewModel
import github.detrig.corporatekanbanboard.core.Navigation
import github.detrig.corporatekanbanboard.domain.model.Comment
import github.detrig.corporatekanbanboard.domain.repository.boards.BoardsRepository
import github.detrig.corporatekanbanboard.presentation.boardMain.ClickedTaskLiveDataWrapper

class TaskInfoViewModel(
    private val boardsRepository: BoardsRepository,
    private val clickedTaskLiveDataWrapper: ClickedTaskLiveDataWrapper,
) : ViewModel() {

    fun addComment(comment: Comment) {

    }

}