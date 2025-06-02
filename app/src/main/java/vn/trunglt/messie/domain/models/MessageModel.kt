package vn.trunglt.messie.domain.models

data class MessageModel(
    val id: String,      // ID của tin nhắn (có thể là UUID)
    val sender: String,  // Người gửi tin nhắn
    val text: String,    // Nội dung tin nhắn
    val timestamp: Long  // Dấu thời gian của tin nhắn (Unix timestamp)
)