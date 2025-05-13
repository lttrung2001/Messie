package vn.trunglt.messie.domain.models

/**
 * Lớp này đại diện cho một tin nhắn trong tầng domain.  Nó chứa các thông tin cơ bản
 * của một tin nhắn, độc lập với cách dữ liệu được lưu trữ hoặc truyền tải.
 */
data class MessageModel(
    val id: String,      // ID của tin nhắn (có thể là UUID)
    val sender: String,  // Người gửi tin nhắn
    val text: String,    // Nội dung tin nhắn
    val timestamp: Long  // Dấu thời gian của tin nhắn (Unix timestamp)
)