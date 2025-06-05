package vn.trunglt.messie

import android.os.Build
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
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.util.Scanner


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
    fun checkCustomOS(): Boolean {
        val buildTags = Build.TAGS
        println(buildTags)
        return buildTags != null && buildTags.contains("test-keys")
    }

    override fun onResume() {
        super.onResume()
        println(checkCustomOS())
        println(checkModifiedProps())
        println(isRootedByFileCheck())
        println(execute("whoami"))
        println(execute("pwd"))
        println(execute("cd /root"))
    }

    fun checkModifiedProps(): Boolean {
        try {
            val inputstream = Runtime.getRuntime().exec("getprop").inputStream
            if (inputstream == null) return false
            val propVal = Scanner(inputstream).useDelimiter("\\A").next()
            val lines = propVal.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            // Ví dụ: kiểm tra ro.debuggable hoặc ro.secure
            for (line in lines) {
                if (line.contains("ro.debuggable") && line.contains("[1]")) {
                    return true // Debuggable thường là 0 trên stock ROM
                }
                if (line.contains("ro.secure") && line.contains("[0]")) {
                    return true // Secure thường là 1 trên stock ROM
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }

    fun isRootedByFileCheck(): Boolean {
        val paths = arrayOf<String?>(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su",
            "/sbin/.magisk/" // Magisk
        )
        for (path in paths) {
            if (File(path).exists()) {
                println(path)
                println("canRead ${File(path).canRead()} ${File(path).readLines().joinToString()}")
                println("canWrite ${File(path).canWrite()}")
//                return true
            }
        }
        return false
    }

    fun execute(cmd: String): String? {
        val p = Runtime.getRuntime().exec(cmd)
        val reader = BufferedReader(InputStreamReader(p.inputStream))
        val line = reader.readLine()
        reader.close()
        return line
    }
}