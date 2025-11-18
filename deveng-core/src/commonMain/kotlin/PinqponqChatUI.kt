package global.deveng.core

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import global.deveng.core.PinqponqChatCore

public object PinqponqChatUI {

    @Composable
    public fun LastConversationsScreen() {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Last Conversations", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)
            Text("• Conversation 1: Hello from customer!")
            Text("• Conversation 2: Ready-to-use screen!")
            Text("• Conversation 3: Just call LastConversationsScreen() and boom!")
        }
    }
    
    @Composable
    public fun ChatScreen() {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Chat Screen", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)
            Text("This is a ready-to-use chat screen")
            Text("Customers just call ChatScreen() and it works!")
        }
    }
}
