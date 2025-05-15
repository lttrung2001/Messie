package vn.trunglt.messie.ui.screens

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import vn.trunglt.messie.data.repositories.message.toMessageModel
import vn.trunglt.messie.domain.models.MessageModel
import vn.trunglt.messie.domain.usecases.GetMessagesUseCase
import vn.trunglt.messie.domain.usecases.SendMessageUseCase
import java.util.UUID

class ChatViewModel(
    private val getMessagesUseCase: GetMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase
) : ViewModel() {
    val messagePaged = getMessagesUseCase.invoke().flow.map {
        it.map { entity ->
            entity.toMessageModel()
        }
    }.cachedIn(viewModelScope)

    // Sử dụng StateFlow để quản lý nội dung tin nhắn đang nhập
    private val _currentMessageText = MutableStateFlow(TextFieldValue(""))
    val currentMessageText: StateFlow<TextFieldValue> = _currentMessageText.asStateFlow()

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
            }
        }
    }
}
