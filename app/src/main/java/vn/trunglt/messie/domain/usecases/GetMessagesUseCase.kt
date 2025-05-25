package vn.trunglt.messie.domain.usecases

import androidx.paging.Pager
import androidx.paging.PagingConfig
import vn.trunglt.messie.data.repositories.message.Constants
import vn.trunglt.messie.data.repositories.message.MessageRepositoryImpl
import vn.trunglt.messie.data.repositories.message.room.MessageRoomPagingSource
import vn.trunglt.messie.domain.repositories.MessageRepository

// Use case để lấy tin nhắn theo trang
class GetMessagesUseCase(
    private val messageRepository: MessageRepository,
    private val messageRoomPagingSource: MessageRoomPagingSource,
) {
    // Hàm invoke cho phép gọi use case như một hàm thông thường
    operator fun invoke() = Pager(
        PagingConfig(
            pageSize = Constants.PAGE_SIZE,
            maxSize = Constants.MAX_SIZE,
            enablePlaceholders = false,
        )
    ) {
        messageRoomPagingSource
//        messageRepository.getMessages()
    }
}