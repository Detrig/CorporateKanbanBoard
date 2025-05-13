package github.detrig.corporatekanbanboard.presentation.boardMain

import github.detrig.corporatekanbanboard.core.LiveDataWrapper
import github.detrig.corporatekanbanboard.domain.model.Task

interface ClickedTaskLiveDataWrapper : LiveDataWrapper.Mutable<Task> {
    class Base : ClickedTaskLiveDataWrapper, LiveDataWrapper.Abstract<Task>()
}