package vn.trunglt.messie.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vn.trunglt.messie.domain.models.MessageModel
import vn.trunglt.messie.domain.usecases.GetMessagesUseCase
import vn.trunglt.messie.domain.usecases.SendMessageUseCase
import vn.trunglt.messie.ui.models.MessageUiModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class ChatViewModel(
    private val getMessagesUseCase: GetMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase
) : ViewModel() {

    // Sử dụng StateFlow để quản lý danh sách tin nhắn
    private val _messages = MutableStateFlow<List<MessageUiModel>>(emptyList())
    val messages: StateFlow<List<MessageUiModel>> = _messages.asStateFlow()

    // Sử dụng StateFlow để quản lý nội dung tin nhắn đang nhập
    private val _currentMessageText = MutableStateFlow(TextFieldValue(""))
    val currentMessageText: StateFlow<TextFieldValue> = _currentMessageText.asStateFlow()

    // Trang hiện tại
    private var currentPage = 1

    // Có đang tải tin nhắn không
    var isFetchingMessages by mutableStateOf(false)
        private set

    // Hàm để cập nhật nội dung tin nhắn đang nhập
    fun updateMessageText(text: TextFieldValue) {
        viewModelScope.launch {
            _currentMessageText.emit(text)
        }
    }

    // Hàm để gửi tin nhắn mới
    fun sendMessage() {
        viewModelScope.launch {
            val text = _currentMessageText.value.text
            if (text.isNotBlank()) {
                val newMessage = MessageModel(
                    id = UUID.randomUUID().toString(),  // Generate unique ID
                    sender = "Bạn",
                    text = text,
                    timestamp = System.currentTimeMillis()
                )
                sendMessageUseCase.invoke(newMessage) // Use use case
                _currentMessageText.emit(TextFieldValue(""))
                loadMessages() // Refresh message list
            }
        }
    }

    // Hàm format thời gian
    private fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    // Hàm lấy tin nhắn cuối cùng
    fun getLastMessage(): MessageUiModel? {
        return _messages.value.lastOrNull()
    }

    // Hàm đếm số tin nhắn
    fun getMessageCount(): Int {
        return _messages.value.size
    }

    // Hàm tải tin nhắn
    fun loadMessages() {
        if (isFetchingMessages) return  // Prevent multiple concurrent loads
        isFetchingMessages = true
        viewModelScope.launch {
            try {
                // Fetch messages using the use case
                getMessagesUseCase.invoke(currentPage).collect { messageModels ->
                    // Map MessageModel to MessageItem for UI
                    val messageItems = messageModels.map { it.toMessageItem() }
                    _messages.value = messageItems.plus(_messages.value)
                    currentPage++ // Increment page for next load
                }
            } finally {
                isFetchingMessages = false
            }
        }
    }

    // Extension function to convert MessageModel to MessageItem
    private fun MessageModel.toMessageItem(): MessageUiModel {
        return MessageUiModel(
            sender = sender,
            text = text,
            timestamp = timestamp
        )
    }

    init {
        loadMessages()  // Load initial messages
    }
}
