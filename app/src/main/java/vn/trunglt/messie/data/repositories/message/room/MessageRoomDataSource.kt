package vn.trunglt.messie.data.repositories.message.room

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import vn.trunglt.messie.data.repositories.message.room.dao.MessageDao
import vn.trunglt.messie.data.repositories.message.room.entities.MessageEntity
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
    fun getMessages(page: Int, pageSize: Int = 10): Flow<List<MessageModel>> {
        val offset = (page - 1) * pageSize
        return messageDao.getMessagesByPage(pageSize, offset).map { messageEntities ->
            messageEntities.map { it.toMessageModel() }
        }
    }

    /**
     * Lưu một tin nhắn vào Room.
     *
     * @param messageModel Tin nhắn cần lưu.
     */
    suspend fun saveMessage(messageModel: MessageModel) {
        messageDao.saveMessage(messageModel.toMessageEntity())
    }

    // Extension functions để chuyển đổi giữa các model
    private fun MessageEntity.toMessageModel(): MessageModel {
        return MessageModel(
            id = id,
            sender = sender,
            text = text,
            timestamp = timestamp
        )
    }

    private fun MessageModel.toMessageEntity(): MessageEntity {
        return MessageEntity(
            id = id,
            sender = sender,
            text = text,
            timestamp = timestamp
        )
    }
}