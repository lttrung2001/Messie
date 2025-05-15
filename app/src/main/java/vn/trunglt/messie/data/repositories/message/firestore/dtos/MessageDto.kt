package vn.trunglt.messie.data.repositories.message.firestore.dtos

import vn.trunglt.messie.domain.models.MessageModel

// Định nghĩa lớp DTO cho tin nhắn Firestore
data class MessageDto(val id: String, val sender: String, val text: String, val timestamp: Long) {
    fun toMessageModel(): MessageModel {
        return MessageModel(
            id = id, // Firestore không trả về ID trong snapshot listener, bạn có thể xử lý nó ở nơi khác nếu cần
            sender = sender,
            text = text,
            timestamp = timestamp
        )
    }
}