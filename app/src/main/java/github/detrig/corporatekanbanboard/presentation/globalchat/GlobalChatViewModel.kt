package github.detrig.corporatekanbanboard.presentation.globalchat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import github.detrig.corporatekanbanboard.domain.model.chat.GlobalChatMessage
import github.detrig.corporatekanbanboard.domain.repository.chat.ChatRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GlobalChatViewModel(
    private val chatRepository: ChatRepository,
    private val database: FirebaseDatabase,
    private val viewModelScope: CoroutineScope,
    private val dispatcherMain: CoroutineDispatcher = Dispatchers.Main,
    private val dispatcherIo: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _messages = MutableLiveData<List<GlobalChatMessage>>(emptyList())
    val messages: LiveData<List<GlobalChatMessage>> = _messages

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val messagesRef = database.getReference("chatMessages")
    private val messagesList = mutableListOf<GlobalChatMessage>()

    private var earliestTimestamp: Long? = null
    private var latestTimestamp: Long? = null
    private var allMessagesLoaded = false
    private var earliestMessageId: String? = null
    private var newMessagesListener: ChildEventListener? = null

    init {
        loadLatestMessages(PAGE_SIZE)
    }

    private fun loadLatestMessages(limit: Int) {
        viewModelScope.launch(dispatcherIo) {
            val result = chatRepository.loadLatestMessages(limit)
            if (result.isSuccess) {
                val loadedMessages = result.getOrNull() ?: emptyList()
                withContext(dispatcherMain) {
                    messagesList.clear()
                    messagesList.addAll(loadedMessages)
                    messagesList.sortBy { it.timestamp }
                    _messages.value = messagesList.toList()

                    earliestTimestamp = messagesList.firstOrNull()?.timestamp
                    latestTimestamp = messagesList.lastOrNull()?.timestamp

                    // Подписываемся на новые сообщения, которые будут добавляться позже latestTimestamp
                    subscribeToNewMessagesAfter(latestTimestamp ?: 0L)
                }
            } else {
                _error.postValue(result.exceptionOrNull()?.message ?: "Ошибка загрузки сообщений")
            }
        }
    }

    fun loadPreviousMessages() {
        val beforeId = earliestMessageId ?: return
        if (allMessagesLoaded) return

        viewModelScope.launch(dispatcherIo) {
            val result = chatRepository.loadPreviousMessages(beforeId, PAGE_SIZE)
            if (result.isSuccess) {
                val prevMessages = result.getOrNull() ?: emptyList()
                if (prevMessages.isEmpty()) {
                    allMessagesLoaded = true
                } else {
                    withContext(dispatcherMain) {
                        messagesList.addAll(0, prevMessages)
                        messagesList.sortBy { it.timestamp }
                        _messages.value = messagesList.toList()
                        earliestMessageId = messagesList.firstOrNull()?.id
                    }
                }
            } else {
                _error.postValue(result.exceptionOrNull()?.message ?: "Ошибка загрузки сообщений")
            }
        }
    }

    private fun subscribeToNewMessagesAfter(timestamp: Long) {
        newMessagesListener?.let { messagesRef.removeEventListener(it) }
        val query = messagesRef.orderByChild("timestamp").startAt(timestamp.toDouble() + 0.001)
        newMessagesListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val newMsg = snapshot.getValue(GlobalChatMessage::class.java) ?: return
                Log.d("alz-04", "onChildAdded: ${newMsg.text}")
                messagesList.add(newMsg)
                messagesList.sortBy { it.timestamp }
                _messages.postValue(messagesList.toList())
                latestTimestamp = newMsg.timestamp.coerceAtLeast(latestTimestamp ?: 0L)
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) { /* ... */ }
            override fun onChildRemoved(snapshot: DataSnapshot) { /* ... */ }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) { /* ... */ }
            override fun onCancelled(error: DatabaseError) {
                _error.postValue(error.message)
            }
        }
        query.addChildEventListener(newMessagesListener!!)
    }

    override fun onCleared() {
        super.onCleared()
        newMessagesListener?.let { messagesRef.removeEventListener(it) }
    }

    fun sendMessage(message: GlobalChatMessage) {
        viewModelScope.launch(dispatcherIo) {
            val result = chatRepository.sendMessage(message)
            result.onFailure {
                _error.postValue(it.message)
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    companion object {
        private const val PAGE_SIZE = 50
    }
}
