package vn.trunglt.messie.ui.screens

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.map
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import vn.trunglt.messie.data.repositories.message.Constants
import vn.trunglt.messie.data.repositories.message.MessageRemoteMediator
import vn.trunglt.messie.data.repositories.message.toMessageModel
import vn.trunglt.messie.domain.models.MessageModel
import vn.trunglt.messie.domain.repositories.MessageRepository
import vn.trunglt.messie.domain.repositories.UserRepository
import java.util.UUID

class ChatViewModel(
    messageRemoteMediator: MessageRemoteMediator,
    private val messageRepository: MessageRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    val safeScope = viewModelScope + CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    @OptIn(ExperimentalPagingApi::class)
    val messagePaged = Pager(
        config = PagingConfig(
            pageSize = Constants.PAGE_SIZE,
        ), remoteMediator = messageRemoteMediator, pagingSourceFactory = {
            messageRepository.getRoomPagingSource()
        }).flow.map { pagingData ->
        pagingData.map {
            it.toMessageModel()
        }
    }.cachedIn(viewModelScope)

    init {
        safeScope.launch {
            val user = try {
                userRepository.findUser()
            } catch (e: Exception) {
                userRepository.createUser()
            }
            _uiState.update {
                it.copy(
                    currentUserId = user.id
                )
            }
        }
    }

    // Hàm để cập nhật nội dung tin nhắn đang nhập
    fun updateMessageText(text: TextFieldValue) {
        safeScope.launch {
            _uiState.update {
                it.copy(textFieldValue = text)
            }
        }
    }

    // Hàm để gửi tin nhắn mới
    fun sendMessage() {
        if (_uiState.value.currentUserId.isEmpty()) return
        safeScope.launch {
            val text = _uiState.value.textFieldValue.text
            if (text.isNotBlank()) {
                val newMessage = MessageModel(
                    id = UUID.randomUUID().toString(),
                    sender = _uiState.value.currentUserId,
                    text = text,
                    timestamp = System.currentTimeMillis()
                )
                messageRepository.saveMessage(newMessage)
                _uiState.update {
                    it.copy(
                        textFieldValue = TextFieldValue(""),
                    )
                }
            }
        }
    }
}

data class ChatUiState(
    val textFieldValue: TextFieldValue = TextFieldValue(""),
    val currentUserId: String = "",
)
