package vn.trunglt.messie.data.repositories.message.room

import androidx.room.Database
import androidx.room.RoomDatabase
import vn.trunglt.messie.data.repositories.message.room.dao.MessageDao
import vn.trunglt.messie.data.repositories.message.room.entities.MessageEntity

@Database(entities = [MessageEntity::class], version = 1)
abstract class MessageDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao

//    companion object {
//        // Đảm bảo chỉ có một instance của database được tạo ra.
//        @Volatile
//        var INSTANCE: AppDatabase? = null
//
//        fun getInstance(context: Context): AppDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    AppDatabase::class.java,
//                    "messaging_app_db" // Tên của database
//                ).build()
//                INSTANCE = instance
//                instance
//            }
//        }
//    }
}