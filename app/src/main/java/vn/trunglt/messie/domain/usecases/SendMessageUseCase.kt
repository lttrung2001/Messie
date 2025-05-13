package vn.trunglt.messie.domain.usecases

import vn.trunglt.messie.domain.models.MessageModel
import vn.trunglt.messie.domain.repositories.MessageRepository

// Use case để gửi tin nhắn
class SendMessageUseCase(private val messageRepository: MessageRepository) {
    // Hàm invoke cho phép gọi use case như một hàm thông thường
    suspend operator fun invoke(message: MessageModel) {
        messageRepository.saveMessage(message) // Đã sửa từ sendMessage thành saveMessage
    }
}