package vn.trunglt.messie.data.repositories.message

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.map
import com.example.messagingapp.data.source.remote.FirestoreRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import vn.trunglt.messie.data.repositories.message.room.MessageDatabase
import vn.trunglt.messie.data.repositories.message.room.MessageRoomDataSource
import vn.trunglt.messie.data.repositories.message.room.MessageRoomPagingSource
import vn.trunglt.messie.data.repositories.message.room.dao.MessageDao
import vn.trunglt.messie.domain.models.MessageModel
import vn.trunglt.messie.domain.repositories.MessageRepository

class MessageRepositoryImpl(
    private val localDataSource: MessageRoomDataSource,
    private val remoteDataSource: FirestoreRemoteDataSource,
    private val messageDatabase: MessageDatabase,
    private val messageDao: MessageDao,
) : MessageRepository {
    private val ioDispatcher = Dispatchers.IO
    override fun getPagingSource(): PagingSource<Int, MessageModel> {
        return MessageRoomPagingSource(localDataSource)
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getRoomPagingSource(): Flow<PagingData<MessageModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = Constants.PAGE_SIZE,
            ),
            remoteMediator = MessageRemoteMediator(
                database = messageDatabase,
                messageDao = messageDao,
                networkService = remoteDataSource,
            ), pagingSourceFactory = {
                localDataSource.getMessagesPagingSource()
            }).flow.map { pagingData ->
            pagingData.map { entity ->
                entity.toMessageModel()
            }
        }
    }

    override suspend fun saveMessage(message: MessageModel) {
        withContext(ioDispatcher) {
            try {
                localDataSource.saveMessage(message)
            } catch (e: Exception) {
                println("Error saving message to Room: $e")
            }
            try {
                remoteDataSource.sendMessage(message)
            } catch (e: Exception) {
                println("Error sending message to Firestore: $e")
            }
        }
    }
}