package github.detrig.corporatekanbanboard.presentation.boards

import github.detrig.corporatekanbanboard.core.ListLiveDataWrapper
import github.detrig.corporatekanbanboard.domain.model.User

interface ClickedBoardUsersLiveDataWrapper : ListLiveDataWrapper.All<User> {
    class Base : ClickedBoardUsersLiveDataWrapper, ListLiveDataWrapper.Abstract<User>()
}