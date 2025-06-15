package com.example.messagingapp.data.source.remote

import com.google.firebase.firestore.CollectionReference
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

    suspend fun getMessages(
        limit: Int,
        after: Long?,
    ): List<MessageModel> {
        println("call remote: $after")
        val query: Query = messagesCollection
            .orderBy("timestamp")
            .startAfter(after ?: 0L)
            .limit(limit.toLong()) // Giới hạn số lượng tài liệu lấy về

        val snapshot = query.get().await()
        if (snapshot != null) {
            val messageModels = snapshot.documents.map { document ->
                val id = document.id
                val sender = document.getString("sender") ?: ""
                val text = document.getString("text") ?: ""
                val timestamp = document.getLong("timestamp") ?: 0L
                MessageDto(id, sender, text, timestamp).toMessageModel()
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