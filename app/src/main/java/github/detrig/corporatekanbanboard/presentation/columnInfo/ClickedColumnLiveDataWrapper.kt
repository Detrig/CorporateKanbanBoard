package github.detrig.corporatekanbanboard.presentation.columnInfo

import github.detrig.corporatekanbanboard.core.LiveDataWrapper
import github.detrig.corporatekanbanboard.domain.model.Column

interface ClickedColumnLiveDataWrapper : LiveDataWrapper.Mutable<Column> {
    class Base : ClickedColumnLiveDataWrapper, LiveDataWrapper.Abstract<Column>()
}