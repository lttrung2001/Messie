package vn.trunglt.messie.data.repositories.message.room.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import vn.trunglt.messie.data.repositories.message.room.entities.MessageEntity
import vn.trunglt.messie.data.repositories.message.toMessageEntity
import vn.trunglt.messie.domain.models.MessageModel

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages")
    fun getMessages(): List<MessageEntity>

    @Query("SELECT * FROM messages ORDER BY timestamp")
    fun getMessagesPagingSource(): PagingSource<Int, MessageEntity> // Sử dụng MessageEntity

    @Query("SELECT * FROM messages ORDER BY timestamp DESC LIMIT :limit OFFSET :offset")
    fun getMessagesPaged(limit: Int, offset: Int): List<MessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMessage(messageEntity: MessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMessages(messageEntity: List<MessageEntity>)

    suspend fun saveMessageModels(messageModels: List<MessageModel>) {
        return saveMessages(messageModels.map { it.toMessageEntity() })
    }

    @Query("DELETE FROM messages")
    suspend fun deleteAllMessages()
}