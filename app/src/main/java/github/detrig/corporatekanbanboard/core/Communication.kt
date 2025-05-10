package github.detrig.corporatekanbanboard.core

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

interface Communication<T> {
    fun setData(data: T)
    fun observe(owner: LifecycleOwner, observer: Observer<T>)
}

class BaseCommunication<T> : Communication<T> {
    private val liveData = SingleLiveEvent<T>()

    override fun setData(data: T) {
        liveData.postValue(data)
    }

    override fun observe(owner: LifecycleOwner, observer: Observer<T>) {
        liveData.observe(owner, observer)
    }
}