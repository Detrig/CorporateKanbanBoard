package github.detrig.corporatekanbanboard.presentation.boards

import github.detrig.corporatekanbanboard.core.LiveDataWrapper
import github.detrig.corporatekanbanboard.domain.model.Board

interface ClickedBoardLiveDataWrapper : LiveDataWrapper.Mutable<Board> {
    class Base : ClickedBoardLiveDataWrapper, LiveDataWrapper.Abstract<Board>()
}