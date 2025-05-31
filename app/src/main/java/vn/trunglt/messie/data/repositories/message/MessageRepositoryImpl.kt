package vn.trunglt.messie.data.repositories.message

import androidx.paging.PagingSource
import com.example.messagingapp.data.source.remote.FirestoreRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import vn.trunglt.messie.data.repositories.message.room.MessageRoomDataSource
import vn.trunglt.messie.data.repositories.message.room.MessageRoomPagingSource
import vn.trunglt.messie.domain.models.MessageModel
import vn.trunglt.messie.domain.repositories.MessageRepository
import java.io.IOException

class MessageRepositoryImpl(
    private val localDataSource: MessageRoomDataSource, // Inject nguồn dữ liệu cục bộ
    private val remoteDataSource: FirestoreRemoteDataSource, // Inject nguồn dữ liệu từ xa
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
    override fun getMessagesPagingSource(): PagingSource<Int, MessageModel> {
        return MessageRoomPagingSource(remoteDataSource, localDataSource)
    }

    @Suppress("Cần notify cho paging biết có thay đổi")
    override suspend fun saveMessage(message: MessageModel) {
        withContext(ioDispatcher) {
            try {
                // Lưu tin nhắn vào Room
                localDataSource.saveMessage(message)
            } catch (e: Exception) {
                // Xử lý lỗi lưu tin nhắn vào Room
                println("Error saving message to Room: $e")
            }
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
        }
    }
}