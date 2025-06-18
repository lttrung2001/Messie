package vn.trunglt.messie.domain.repositories

import androidx.paging.PagingSource
import vn.trunglt.messie.data.repositories.message.room.entities.MessageEntity
import vn.trunglt.messie.domain.models.MessageModel

interface MessageRepository {
    fun getCustomRoomPagingSource(): PagingSource<Int, MessageModel>
    fun getRoomPagingSource(): PagingSource<Int, MessageEntity>
    suspend fun saveMessage(message: MessageModel)
}