package vn.trunglt.messie

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.example.messagingapp.data.source.remote.MessageRemoteDataSource
import com.google.gson.Gson
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import vn.trunglt.messie.data.repositories.message.MessageRemoteMediator
import vn.trunglt.messie.data.repositories.message.MessageRepositoryImpl
import vn.trunglt.messie.data.repositories.message.room.MessageDatabase
import vn.trunglt.messie.data.repositories.message.room.MessageRoomDataSource
import vn.trunglt.messie.data.repositories.user.UserRepoImpl
import vn.trunglt.messie.data.repositories.user.shared_preferences.UserStorage
import vn.trunglt.messie.domain.repositories.MessageRepository
import vn.trunglt.messie.domain.repositories.UserRepository
import vn.trunglt.messie.ui.screens.ChatViewModel

fun providesGson(): Gson = Gson().newBuilder().create()

fun providesUserSharedPreferences(context: Context): SharedPreferences =
    context.getSharedPreferences(
        "user_preferences",
        Context.MODE_PRIVATE,
    )

fun providesDatabase(application: Application) = Room.databaseBuilder(
    application,
    MessageDatabase::class.java,
    "messie_app_db" // Tên của database
).allowMainThreadQueries().build()

fun providesMessageDao(database: MessageDatabase) = database.messageDao()

// Module Koin cho các dependency của ứng dụng nhắn tin
val messagingAppModule = module {
    single { providesGson() }
    single { providesUserSharedPreferences(androidApplication()) }

    // Room Database
    single { providesDatabase(androidApplication()) }
    single { providesMessageDao(get()) } // Provide MessageDao

    // Data Sources
    factory { MessageRoomDataSource(get()) } // Provide MessageRoomDataSource
    factory { MessageRemoteDataSource() } // Provide FirestoreRemoteDataSource

    // Paging
    single {
        MessageRemoteMediator(
            get(),
            get(),
            get(),
        )
    }

    // Repository
    single<MessageRepository> {
        MessageRepositoryImpl(
            get(),
            get(),
        )
    } // Provide MessageRepositoryImpl

    single<UserRepository> {
        UserRepoImpl(get())
    }

    single {
        UserStorage(get(), get())
    }

    viewModel {
        ChatViewModel(get(), get(), get())
    }
}