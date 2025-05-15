package vn.trunglt.messie.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import org.koin.androidx.compose.koinViewModel
import vn.trunglt.messie.ui.components.MessageItem
import vn.trunglt.messie.ui.theme.MessieTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(viewModel: ChatViewModel = koinViewModel()) { // Use koinViewModel()
    // Sử dụng State từ ViewModel
    val messagePaged = viewModel.messagePaged.collectAsLazyPagingItems()
    val currentMessageText by viewModel.currentMessageText.collectAsState()
    val lazyListState = rememberLazyListState()

    // Use MessagingAppTheme to apply the theme
    MessieTheme {
        // A scaffold with голубой background
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Trò chuyện",
                            style = TextStyle(
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                            )
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2196F3)), // Set голубой color
                )
            },
            containerColor = Color.White // Set the background color of the Scaffold
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                // Danh sách tin nhắn
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    reverseLayout = true, // Start from the bottom
                    state = lazyListState
                ) {
                    items(
                        count = messagePaged.itemCount,
                        key = messagePaged.itemKey { messageModel ->
                            messageModel.id
                        },
                        contentType = messagePaged.itemContentType {
                            "message"
                        },
                        itemContent = { index ->
                            val message = messagePaged[index]
                            if (message != null) {
                                MessageItem(message = message)
                            }
                        }
                    )
                }
                // Ô nhập tin nhắn và nút gửi
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = currentMessageText,
                        onValueChange = viewModel::updateMessageText,
                        modifier = Modifier.weight(1f),
                        label = { Text("Tin nhắn") },
                        shape = RoundedCornerShape(24.dp), // Rounded edges
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.Black, // Text color when focused
                            unfocusedTextColor = Color.Black, // Text color when unfocused
                            focusedContainerColor = Color(0xFFE0E0E0), // Background color when focused
                            unfocusedContainerColor = Color(0xFFE0E0E0), // Background color when unfocused
                            cursorColor = Color.Black, // Cursor color
                            focusedLabelColor = Color(0xFF2196F3), // Label color when focused
                            unfocusedLabelColor = Color.Gray, // Label color when unfocused
                        ),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = { viewModel.sendMessage() }) {
                        Icon(
                            imageVector = Icons.Filled.Send,
                            contentDescription = "Gửi",
                            tint = Color(0xFF2196F3) // голубой color
                        )
                    }
                }
            }
        }
    }
}