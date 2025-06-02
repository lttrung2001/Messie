package vn.trunglt.messie.data.repositories.message.room

import androidx.paging.PagingSource
import vn.trunglt.messie.data.repositories.message.room.dao.MessageDao
import vn.trunglt.messie.data.repositories.message.room.entities.MessageEntity
import vn.trunglt.messie.data.repositories.message.toMessageEntity
import vn.trunglt.messie.domain.models.MessageModel

// Class này chịu trách nhiệm tương tác với Room database
class MessageRoomDataSource(
    private val messageDao: MessageDao
) {
    fun getMessagesPaged(limit: Int, offset: Int): List<MessageEntity> {
        return messageDao.getMessagesPaged(limit, offset)
    }

    fun getMessagesPagingSource(): PagingSource<Int, MessageEntity> {
        return messageDao.getMessagesPagingSource()
    }

    suspend fun saveMessage(messageModel: MessageModel) {
        messageDao.saveMessage(messageModel.toMessageEntity())
    }

    suspend fun saveMessages(messages: List<MessageModel>) {
        messageDao.saveMessages(messages.map {
            it.toMessageEntity()
        })
    }
}