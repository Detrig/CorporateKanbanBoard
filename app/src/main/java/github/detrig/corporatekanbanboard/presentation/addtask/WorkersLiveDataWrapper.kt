package github.detrig.corporatekanbanboard.presentation.addtask

import github.detrig.corporatekanbanboard.core.LiveDataWrapper
import github.detrig.corporatekanbanboard.domain.model.BoardMember

interface WorkersLiveDataWrapper : LiveDataWrapper.Mutable<Map<BoardMember, Boolean>> {
    class Base : WorkersLiveDataWrapper, LiveDataWrapper.Abstract<Map<BoardMember, Boolean>>()
}