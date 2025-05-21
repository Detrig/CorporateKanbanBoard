package github.detrig.corporatekanbanboard.presentation.boardMain

import android.util.Log
import github.detrig.corporatekanbanboard.core.LiveDataWrapper
import github.detrig.corporatekanbanboard.domain.model.Task

interface ClickedTaskLiveDataWrapper : LiveDataWrapper.Mutable<Task> {
    class Base : ClickedTaskLiveDataWrapper, LiveDataWrapper.Abstract<Task>() {
        override fun update(value: Task) {
            super.update(value)
            Log.d("alz-04", "TASK UPDATED PHOTOSIZE: ${value.photosBase64.size}")
        }
    }
}