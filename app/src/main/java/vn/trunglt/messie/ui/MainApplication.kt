package vn.trunglt.messie.ui

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import vn.trunglt.messie.messagingAppModule

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            androidLogger(Level.DEBUG)
            modules(messagingAppModule) // Khai báo module Koin của bạn
        }
    }
}