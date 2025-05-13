package vn.trunglt.messie.domain.usecases

import kotlinx.coroutines.flow.Flow
import vn.trunglt.messie.domain.models.MessageModel
import vn.trunglt.messie.domain.repositories.MessageRepository

// Use case để lấy tin nhắn theo trang
class GetMessagesUseCase(private val messageRepository: MessageRepository) {
    // Hàm invoke cho phép gọi use case như một hàm thông thường
    suspend operator fun invoke(page: Int, pageSize: Int = 20): Flow<List<MessageModel>> {
        return messageRepository.getMessages(page, pageSize)
    }
}