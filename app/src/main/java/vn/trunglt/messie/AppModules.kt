package vn.trunglt.messie

import android.app.Application
import androidx.room.Room
import com.example.messagingapp.data.source.remote.FirestoreRemoteDataSource
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import vn.trunglt.messie.data.repositories.message.MessageRepositoryImpl
import vn.trunglt.messie.data.repositories.message.room.MessageDatabase
import vn.trunglt.messie.data.repositories.message.room.MessageRoomDataSource
import vn.trunglt.messie.data.repositories.message.room.MessageRoomPagingSource
import vn.trunglt.messie.domain.repositories.MessageRepository
import vn.trunglt.messie.domain.usecases.GetMessagesUseCase
import vn.trunglt.messie.domain.usecases.SendMessageUseCase
import vn.trunglt.messie.ui.screens.ChatViewModel

fun providesDatabase(application: Application) = Room.databaseBuilder(
    application,
    MessageDatabase::class.java,
    "messie_app_db" // Tên của database
).allowMainThreadQueries().build()

fun providesMessageDao(database: MessageDatabase) = database.messageDao()

// Module Koin cho các dependency của ứng dụng nhắn tin
val messagingAppModule = module {

    // Room Database
    single { providesDatabase(androidApplication()) }
    single { providesMessageDao(get()) } // Provide MessageDao

    // Data Sources
    factory { MessageRoomDataSource(get()) } // Provide MessageRoomDataSource
    factory { FirestoreRemoteDataSource() } // Provide FirestoreRemoteDataSource

    // Repository
    single<MessageRepository> {
        MessageRepositoryImpl(
            get(),
            get()
        )
    } // Provide MessageRepositoryImpl

    single {
        MessageRoomPagingSource(get())
    }

    // Use Cases
    factory { GetMessagesUseCase(get(), get()) } // Provide GetMessagesUseCase
    factory { SendMessageUseCase(get()) } // Provide SendMessageUseCase

    viewModel {
        ChatViewModel(get(), get())
    }
}