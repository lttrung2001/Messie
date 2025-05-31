package vn.trunglt.messie.data.repositories.message.room

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.messagingapp.data.source.remote.FirestoreRemoteDataSource
import vn.trunglt.messie.data.repositories.message.Constants
import vn.trunglt.messie.data.repositories.message.toMessageModel
import vn.trunglt.messie.domain.models.MessageModel

class MessageRoomPagingSource(
    // Inject for remote testing
    private val remoteDataSource: FirestoreRemoteDataSource,
    private val localDataSource: MessageRoomDataSource
) : PagingSource<Int, MessageModel>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MessageModel> {
        return try {
            val page = params.key ?: 0
//            val dtos = remoteDataSource.getMessages(params.loadSize)
//            localDataSource.saveMessages(dtos)
            val entities = localDataSource.getMessagesPaged(params.loadSize, page * params.loadSize)
            val models = entities.map { it.toMessageModel() }
            val prevKey = if (page == 0) null else page - 1
            val nextKey = if (models.isEmpty()) null else page + (params.loadSize / Constants.PAGE_SIZE)
            LoadResult.Page(
                data = models,
                prevKey = prevKey,
                nextKey = nextKey,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, MessageModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}