package vn.trunglt.messie.data.repositories.message.room

import androidx.paging.PagingSource
import androidx.paging.PagingState
import vn.trunglt.messie.data.repositories.message.Constants
import vn.trunglt.messie.data.repositories.message.room.dao.MessageDao
import vn.trunglt.messie.data.repositories.message.room.entities.MessageEntity

@Suppress("Class này đang implement sai page key dẫn đến lấy dữ liệu trùng lặp")
class MessageRoomPagingSource(
    private val dao: MessageDao
) : PagingSource<Int, MessageEntity>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MessageEntity> {
        val page = params.key ?: 0

        return try {
            val entities = dao.getMessagesPaged(params.loadSize, page * params.loadSize)
            println("Load size: ${params.loadSize} Page: $page Entities: ${entities.size}")
            LoadResult.Page(
                data = entities,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (entities.isEmpty()) null else page + (params.loadSize / Constants.PAGE_SIZE)
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, MessageEntity>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}