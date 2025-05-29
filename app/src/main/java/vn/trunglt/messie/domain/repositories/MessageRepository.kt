package vn.trunglt.messie.domain.repositories

import androidx.paging.PagingSource
import vn.trunglt.messie.domain.models.MessageModel

interface MessageRepository {
    /**
     * Lấy danh sách tin nhắn theo trang.
     *
     * @param page Số trang (bắt đầu từ 1).
     * @param pageSize Số lượng tin nhắn trên mỗi trang.
     * @return Flow chứa danh sách tin nhắn cho trang được chỉ định.
     */
    fun getMessagesPagingSource(): PagingSource<Int, MessageModel>

    /**
     * Lưu một tin nhắn vào bộ nhớ.
     *
     * @param message Tin nhắn cần lưu.
     */
    suspend fun saveMessage(message: MessageModel)
}