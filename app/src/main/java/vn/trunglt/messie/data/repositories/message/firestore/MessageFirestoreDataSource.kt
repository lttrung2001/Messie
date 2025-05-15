package com.example.messagingapp.data.source.remote

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import vn.trunglt.messie.data.constants.Constants
import vn.trunglt.messie.data.repositories.message.firestore.dtos.MessageDto
import vn.trunglt.messie.domain.models.MessageModel

// Hằng số cho tên collection trên Firestore
private const val MESSAGES_COLLECTION = "messages"

// Class này chịu trách nhiệm tương tác với Firestore
class FirestoreRemoteDataSource {

    // Lấy reference đến collection "messages" trên Firestore
    private val messagesCollection: CollectionReference =
        FirebaseFirestore.getInstance().collection(MESSAGES_COLLECTION)

    /**
     * Lấy tin nhắn từ Firestore theo trang.
     *
     * @param page Số trang (bắt đầu từ 1).
     * @param pageSize Số lượng tin nhắn trên mỗi trang.
     * @param lastMessageTimestamp Timestamp của tin nhắn cuối cùng của trang trước đó.
     * @return Flow chứa danh sách tin nhắn.
     */
    fun getMessages(
        lastMessageTimestamp: Long
    ): Flow<List<MessageModel>> =
        callbackFlow { // Thay đổi kiểu trả về thành Flow<List<MessageModel>>
            // Thực hiện truy vấn Firestore
            var query = messagesCollection.orderBy("timestamp") // Sắp xếp theo thời gian

            // Sử dụng startAfter nếu có lastMessageTimestamp
            query = query.startAfter(lastMessageTimestamp)

            query = query.limit(Constants.PAGE_SIZE)

            val subscription =
                query.addSnapshotListener { snapshot, error -> // Sử dụng addSnapshotListener để theo dõi thay đổi
                    if (error != null) {
                        // Nếu có lỗi, gửi lỗi qua channel và đóng channel
                        close(error)
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        // Nếu có dữ liệu, chuyển đổi dữ liệu Firestore thành danh sách các đối tượng Message
                        val messageDtos =
                            snapshot.documents.map { document -> // Đổi tên biến thành messageDtos
                                // Chuyển đổi các trường của document sang kiểu dữ liệu tương ứng.  Nếu có lỗi, trả về giá trị mặc định.
                                val id = document.id
                                val sender = document.getString("sender") ?: ""
                                val text = document.getString("text") ?: ""
                                val timestamp = document.getLong("timestamp") ?: 0L
                                // Tạo đối tượng MessageDto từ dữ liệu Firestore
                                MessageDto(id, sender, text, timestamp)
                            }
                        // Chuyển đổi MessageDto thành MessageModel
                        messageDtos.map { it.toMessageModel() }
                    }
                }

            // Đóng channel khi Flow bị hủy
            awaitClose { subscription.remove() }
        }

    /**
     * Gửi một tin nhắn lên Firestore.
     *
     * @param messageModel Tin nhắn cần gửi.
     */
    suspend fun sendMessage(messageModel: MessageModel) { // Đổi tên tham số thành messageModel
        // Tạo một Map để chứa dữ liệu tin nhắn
        val messageMap = hashMapOf(
            "sender" to messageModel.sender,
            "text" to messageModel.text,
            "timestamp" to messageModel.timestamp
        )

        // Sử dụng add() để Firestore tự động tạo ID cho document
        messagesCollection.add(messageMap).await() // Sử dụng await() để đợi thao tác hoàn thành
    }
}