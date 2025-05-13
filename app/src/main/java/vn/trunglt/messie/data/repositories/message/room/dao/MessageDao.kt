package vn.trunglt.messie.data.repositories.message.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import vn.trunglt.messie.data.repositories.message.room.entities.MessageEntity

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages ORDER BY timestamp DESC")
    fun getAllMessages(): Flow<List<MessageEntity>> // Sử dụng MessageEntity

    @Query("SELECT * FROM messages ORDER BY timestamp DESC LIMIT :pageSize OFFSET :offset")
    fun getMessagesByPage(
        pageSize: Int,
        offset: Int
    ): Flow<List<MessageEntity>> // Sử dụng MessageEntity

    /**
     * Lưu một tin nhắn vào cơ sở dữ liệu.
     *
     * @param messageEntity Tin nhắn cần lưu.
     */
    @Insert
    suspend fun saveMessage(messageEntity: MessageEntity)
}