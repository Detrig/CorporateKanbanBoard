package github.detrig.corporatekanbanboard.core

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

interface Communication<T> {
    fun showData(data: T)
    fun observe(owner: LifecycleOwner, observer: Observer<T>)
}

class BaseCommunication<T> : Communication<T> {
    private val liveData = SingleLiveEvent<T>()

    override fun showData(data: T) {
        liveData.value = data
    }

    override fun observe(owner: LifecycleOwner, observer: Observer<T>) {
        liveData.observe(owner, observer)
    }
}