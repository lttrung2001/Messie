package vn.trunglt.messie.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.trunglt.messie.domain.models.MessageModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MessageItem(message: MessageModel, currentUserId: String) {
    val isCurrentUser = message.sender == currentUserId // Check if the message is from the current user
    val bubbleShape = if (isCurrentUser) RoundedCornerShape(
        20.dp,
        4.dp,
        20.dp,
        20.dp
    ) else RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)
    val bubbleColor =
        if (isCurrentUser) Color(0xFFDCF8C6) else Color(0xFFE0E0E0) // Green for user, gray for others
    val alignment = if (isCurrentUser) Alignment.End else Alignment.Start

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        horizontalAlignment = alignment
    ) {
        Surface( // Using Surface for message bubble
            color = bubbleColor,
            shape = bubbleShape,
            modifier = Modifier.padding(2.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = message.text,
                    color = Color.Black, // Ensure text is readable on both backgrounds
                    style = TextStyle(fontSize = 16.sp)
                )
                val formattedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(
                    Date(
                        message.timestamp
                    )
                )
                Text(
                    text = formattedTime,
                    color = Color.Gray,
                    style = TextStyle(fontSize = 12.sp)
                )
            }
        }
    }
}