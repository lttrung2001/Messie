package vn.trunglt.messie

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import vn.trunglt.messie.ui.models.Routes
import vn.trunglt.messie.ui.screens.ChatScreen
import vn.trunglt.messie.ui.screens.MainScreen
import vn.trunglt.messie.ui.theme.MessieTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MessieTheme {
                // Khởi tạo NavController
                val navController = rememberNavController()
                // Sử dụng KoinApplication để khởi tạo Koin và thiết lập Navigation

                NavHost(navController = navController, startDestination = Routes.MAIN_SCREEN) {
                    composable(Routes.MAIN_SCREEN) {
                        MainScreen(navController = navController)
                    }
                    composable(Routes.CHAT_SCREEN) {
                        ChatScreen()
                    }
                }
            }
        }
    }
}