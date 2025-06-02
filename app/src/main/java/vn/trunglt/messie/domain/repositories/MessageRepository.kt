package vn.trunglt.messie.domain.repositories

import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.PagingSource
import kotlinx.coroutines.flow.Flow
import vn.trunglt.messie.domain.models.MessageModel

interface MessageRepository {
    fun getPagingSource(): PagingSource<Int, MessageModel>
    fun getRoomPagingSource(): Flow<PagingData<MessageModel>>
    suspend fun saveMessage(message: MessageModel)
}