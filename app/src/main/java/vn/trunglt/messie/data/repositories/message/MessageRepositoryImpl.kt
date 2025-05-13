package vn.trunglt.messie.data.repositories.message

import com.example.messagingapp.data.source.remote.FirestoreRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import vn.trunglt.messie.data.repositories.message.room.MessageRoomDataSource
import vn.trunglt.messie.domain.models.MessageModel
import vn.trunglt.messie.domain.repositories.MessageRepository
import java.io.IOException

// Đánh dấu class này là Singleton để chỉ có một instance được tạo ra
class MessageRepositoryImpl(
    private val localDataSource: MessageRoomDataSource, // Inject nguồn dữ liệu cục bộ
    private val remoteDataSource: FirestoreRemoteDataSource // Inject nguồn dữ liệu từ xa
) : MessageRepository {

    // Sử dụng Dispatchers.IO để thực hiện các thao tác I/O
    private val ioDispatcher = Dispatchers.IO

    /**
     * Lấy tin nhắn theo trang, theo nguyên tắc offline-first.
     *
     * 1. Lấy dữ liệu từ Room.
     * 2. Nếu không có dữ liệu trong Room, hoặc đã quá cũ, lấy dữ liệu từ Firestore.
     * 3. Lưu dữ liệu từ Firestore vào Room.
     * 4. Trả về dữ liệu từ Room.
     */
    override suspend fun getMessages(
        page: Int,
        pageSize: Int
    ): Flow<List<MessageModel>> = withContext(ioDispatcher) {
        // Lấy dữ liệu từ Room
        val localMessagesFlow = localDataSource.getMessages(page, pageSize)

        // Kiểm tra xem Room có dữ liệu không
        val hasDataInRoom = localMessagesFlow.first().isNotEmpty() // Collect the first value

        if (!hasDataInRoom) {
            // Nếu không có dữ liệu trong Room, lấy từ Firestore
            try {
                var lastMessageTimestamp: Long? = null
                if (page > 1) {
                    //get last message timestamp from previous page
                    localDataSource.getMessages(page - 1, pageSize).first().lastOrNull()?.let {
                        lastMessageTimestamp = it.timestamp
                    }
                }
                val remoteMessagesFlow =
                    remoteDataSource.getMessages(page, pageSize, lastMessageTimestamp)
                val remoteMessages = remoteMessagesFlow.first() // Collect the value

                // Lưu dữ liệu vào Room
                remoteMessages.forEach { message ->
                    localDataSource.saveMessage(message)
                }
            } catch (e: IOException) {
                // Xử lý lỗi IO một cách cụ thể (ví dụ: mất kết nối mạng)
                println("Network error getting data from Firestore: $e")
                // Có thể ném lại lỗi này nếu bạn muốn thông báo cho người dùng
                // throw e
            } catch (e: Exception) {
                // Xử lý các lỗi khác từ Firestore
                println("Error getting data from Firestore: $e")
            }
        }
        // Trả về dữ liệu từ Room
        return@withContext localMessagesFlow
    }

    override suspend fun saveMessage(message: MessageModel) {
        withContext(ioDispatcher) {
            // Gửi tin nhắn lên Firestore
            try {
                remoteDataSource.sendMessage(message)
            } catch (e: IOException) {
                // Xử lý lỗi IO cụ thể (ví dụ: mất kết nối mạng)
                println("Network error sending message: $e")
                // Có thể thông báo cho người dùng hoặc thử lại sau
            } catch (e: Exception) {
                // Xử lý các lỗi khác từ Firestore.
                println("Error sending message to Firestore: $e")
            }
            // Lưu tin nhắn vào Room
            localDataSource.saveMessage(message)
        }
    }
}