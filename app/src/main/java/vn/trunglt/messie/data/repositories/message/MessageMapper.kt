package vn.trunglt.messie.data.repositories.message

import vn.trunglt.messie.data.repositories.message.room.entities.MessageEntity
import vn.trunglt.messie.domain.models.MessageModel

// Extension functions để chuyển đổi giữa các model
fun MessageEntity.toMessageModel(): MessageModel {
    return MessageModel(
        id = id,
        sender = sender,
        text = text,
        timestamp = timestamp
    )
}

fun MessageModel.toMessageEntity(): MessageEntity {
    return MessageEntity(
        id = id,
        sender = sender,
        text = text,
        timestamp = timestamp
    )
}