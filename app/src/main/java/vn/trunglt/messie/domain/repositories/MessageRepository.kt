package vn.trunglt.messie.domain.repositories

import kotlinx.coroutines.flow.Flow
import vn.trunglt.messie.domain.models.MessageModel

interface MessageRepository {
    /**
     * Lấy danh sách tin nhắn theo trang.
     *
     * @param page Số trang (bắt đầu từ 1).
     * @param pageSize Số lượng tin nhắn trên mỗi trang.
     * @return Flow chứa danh sách tin nhắn cho trang được chỉ định.
     */
    suspend fun getMessages(page: Int, pageSize: Int = 10): Flow<List<MessageModel>>

    /**
     * Lưu một tin nhắn vào bộ nhớ.
     *
     * @param message Tin nhắn cần lưu.
     */
    suspend fun saveMessage(message: MessageModel)
}