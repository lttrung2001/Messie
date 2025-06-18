package vn.trunglt.messie.data.repositories.message

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.messagingapp.data.source.remote.MessageRemoteDataSource
import vn.trunglt.messie.data.repositories.message.room.MessageDatabase
import vn.trunglt.messie.data.repositories.message.room.MessageRoomDataSource
import vn.trunglt.messie.data.repositories.message.room.entities.MessageEntity

@OptIn(ExperimentalPagingApi::class)
class MessageRemoteMediator(
    private val database: MessageDatabase,
    private val localDataSource: MessageRoomDataSource,
    private val remoteDataSource: MessageRemoteDataSource
) : RemoteMediator<Int, MessageEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MessageEntity>
    ): MediatorResult {
        return try {
            // The network load method takes an optional after=<user.id>
            // parameter. For every page after the first, pass the last user
            // ID to let it continue from where it left off. For REFRESH,
            // pass null to load the first page.
            val loadKey = when (loadType) {
                LoadType.REFRESH -> null
                // In this example, you never need to prepend, since REFRESH
                // will always load the first page in the list. Immediately
                // return, reporting end of pagination.
                LoadType.PREPEND ->
                    return MediatorResult.Success(endOfPaginationReached = true)

                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    // You must explicitly check if the last item is null when
                    // appending, since passing null to networkService is only
                    // valid for initial load. If lastItem is null it means no
                    // items were loaded after the initial REFRESH and there are
                    // no more items to load.
                    if (lastItem == null) {
                        return MediatorResult.Success(
                            endOfPaginationReached = true
                        )
                    }

                    lastItem.timestamp
                }
            }

            // Suspending network load via Retrofit. This doesn't need to be
            // wrapped in a withContext(Dispatcher.IO) { ... } block since
            // Retrofit's Coroutine CallAdapter dispatches on a worker
            // thread.
            database.withTransaction {
                val roomPage = localDataSource.getMessagesPaged(
                    limit = state.config.pageSize,
                    after = loadKey ?: 0L
                )
                if (roomPage.size < state.config.pageSize) {
                    val response = remoteDataSource.getMessages(
                        limit = state.config.pageSize,
                        after = loadKey
                    )
                    if (loadType == LoadType.REFRESH) {
                        localDataSource.deleteAllMessages()
                    }

                    // Insert new users into database, which invalidates the
                    // current PagingData, allowing Paging to present the updates
                    // in the DB.
                    localDataSource.saveMessages(response)

                    MediatorResult.Success(
                        endOfPaginationReached = response.isEmpty()
                    )
                } else {
                    MediatorResult.Success(endOfPaginationReached = roomPage.isEmpty())
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            MediatorResult.Error(e)
        }
    }
}