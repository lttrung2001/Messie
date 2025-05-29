package vn.trunglt.messie.data.repositories.message.room

import vn.trunglt.messie.data.repositories.message.room.dao.MessageDao
import vn.trunglt.messie.data.repositories.message.room.entities.MessageEntity
import vn.trunglt.messie.data.repositories.message.toMessageEntity
import vn.trunglt.messie.domain.models.MessageModel

// Class này chịu trách nhiệm tương tác với Room database
class MessageRoomDataSource(
    private val messageDao: MessageDao
) {

    /**
     * Lấy tin nhắn từ Room theo trang.
     *
     * @param page Số trang (bắt đầu từ 1).
     * @param pageSize Số lượng tin nhắn trên mỗi trang.
     * @return Flow chứa danh sách MessageModel.
     */
    fun getMessagesPaged(limit: Int, offset: Int): List<MessageEntity> {
        return messageDao.getMessagesPaged(limit, offset)
    }

    /**
     * Lưu một tin nhắn vào Room.
     *
     * @param messageModel Tin nhắn cần lưu.
     */
    suspend fun saveMessage(messageModel: MessageModel) {
        messageDao.saveMessage(messageModel.toMessageEntity())
    }

    suspend fun saveMessages(messages: List<MessageModel>) {
        messageDao.saveMessages(messages.map {
            it.toMessageEntity()
        })
    }
}