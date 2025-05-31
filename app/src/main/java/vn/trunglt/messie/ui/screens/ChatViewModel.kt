package vn.trunglt.messie.ui.screens

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import vn.trunglt.messie.domain.models.MessageModel
import vn.trunglt.messie.domain.usecases.GetMessagesUseCase
import vn.trunglt.messie.domain.usecases.SendMessageUseCase
import java.util.UUID

class ChatViewModel(
    getMessagesUseCase: GetMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase
) : ViewModel() {
    val messagePaged = getMessagesUseCase.invoke().flow.cachedIn(viewModelScope)

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    // Hàm để cập nhật nội dung tin nhắn đang nhập
    fun updateMessageText(text: TextFieldValue) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(textFieldValue = text)
            }
        }
    }

    // Hàm để gửi tin nhắn mới
    fun sendMessage() {
        viewModelScope.launch {
            val text = _uiState.value.textFieldValue.text
            if (text.isNotBlank()) {
                val newMessage = MessageModel(
                    id = UUID.randomUUID().toString(),  // Generate unique ID
                    sender = "Bạn",
                    text = text,
                    timestamp = System.currentTimeMillis()
                )
                sendMessageUseCase.invoke(newMessage) // Use use case
                _uiState.update {
                    it.copy(
                        textFieldValue = TextFieldValue(""),
                        lastSentMessage = text,
                        scrollToLatest = true,
                    )
                }
            }
        }
    }

    fun onMessagesRefresh() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    lastSentMessage = null,
                    scrollToLatest = false,
                )
            }
        }
    }
}

data class ChatUiState(
    val textFieldValue: TextFieldValue = TextFieldValue(""),
    val lastSentMessage: String? = null,
    val scrollToLatest: Boolean = false,
)
