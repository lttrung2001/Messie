package vn.trunglt.messie.data.repositories.message.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Lớp này đại diện cho một tin nhắn trong tầng domain. Nó chứa các thông tin cơ bản
 * của một tin nhắn, độc lập với cách dữ liệu được lưu trữ hoặc truyền tải.
 */
@Entity(tableName = "messages") // Đánh dấu đây là một entity của Room
data class MessageEntity( // Đổi tên thành MessageEntity
    @PrimaryKey val id: String,      // ID của tin nhắn (có thể là UUID).  @PrimaryKey đánh dấu đây là khóa chính.
    val sender: String,  // Người gửi tin nhắn
    val text: String,    // Nội dung tin nhắn
    val timestamp: Long  // Dấu thời gian của tin nhắn (Unix timestamp)
)