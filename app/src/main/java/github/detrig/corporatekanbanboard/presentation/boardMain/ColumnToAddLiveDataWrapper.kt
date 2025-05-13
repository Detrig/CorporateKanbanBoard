package github.detrig.corporatekanbanboard.presentation.boardMain

import github.detrig.corporatekanbanboard.core.LiveDataWrapper
import github.detrig.corporatekanbanboard.domain.model.Column

interface ColumnToAddLiveDataWrapper: LiveDataWrapper.Mutable<Column> {
    class Base : ColumnToAddLiveDataWrapper, LiveDataWrapper.Abstract<Column>()
}