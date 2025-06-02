package com.example.messagingapp.data.source.remote

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import vn.trunglt.messie.data.repositories.message.firestore.dtos.MessageDto
import vn.trunglt.messie.domain.models.MessageModel

// Hằng số cho tên collection trên Firestore
private const val MESSAGES_COLLECTION = "messages"

// Class này chịu trách nhiệm tương tác với Firestore
class FirestoreRemoteDataSource {

    // Lấy reference đến collection "messages" trên Firestore
    private val messagesCollection: CollectionReference =
        FirebaseFirestore.getInstance().collection(MESSAGES_COLLECTION)

    // Trong Firestore, offset được mô phỏng bằng cách bắt đầu sau một tài liệu cụ thể.
    // Thay vì truyền offset dạng số, chúng ta sẽ truyền document cuối cùng của trang trước đó.
    // Nếu lastDocumentOfPreviousPage là null, có nghĩa là bạn đang lấy trang đầu tiên.
    private var lastDocumentOfPreviousPage: DocumentSnapshot? = null

    suspend fun getMessages(
        limit: Int,
    ): List<MessageModel> {
        var query: Query = messagesCollection.orderBy("timestamp") // Sắp xếp theo thời gian

        // Nếu có tài liệu cuối cùng của trang trước đó, bắt đầu truy vấn sau tài liệu đó
        lastDocumentOfPreviousPage?.let { query = query.startAfter(it.get("timestamp")) }

        query = query.limit(limit.toLong()) // Giới hạn số lượng tài liệu lấy về

        val snapshot = query.get().await()
        if (snapshot != null) {
            val messageModels = snapshot.documents.map { document ->
                val id = document.id
                val sender = document.getString("sender") ?: ""
                val text = document.getString("text") ?: ""
                val timestamp = document.getLong("timestamp") ?: 0L
                MessageDto(id, sender, text, timestamp).toMessageModel()
            }

            // Cập nhật lastVisibleDocument cho lần truy vấn tiếp theo
            if (snapshot.documents.isNotEmpty()) {
                lastDocumentOfPreviousPage = snapshot.documents.first()
            } else {
                lastDocumentOfPreviousPage = null // Không có thêm dữ liệu
            }
            return messageModels
        } else {
            throw Exception("Snapshot is null")
        }
    }

    suspend fun sendMessage(messageModel: MessageModel) { // Đổi tên tham số thành messageModel
        // Tạo một Map để chứa dữ liệu tin nhắn
        val messageMap = hashMapOf(
            "sender" to messageModel.sender,
            "text" to messageModel.text,
            "timestamp" to messageModel.timestamp
        )

        messagesCollection.document(messageModel.id).set(messageMap).await()
    }
}