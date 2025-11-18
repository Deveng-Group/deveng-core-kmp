package global.deveng.core

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

public object PinqponqChatUI {

    @Composable
    public fun LastConversationsScreen() {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("• Conversation 1: Hello from customer!")
            Text("• Conversation 2: Ready-to-use screen!")
            Text("• Conversation 3: Just call LastConversationsScreen() and boom!")
        }
    }
    
    @Composable
    public fun ChatScreen() {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("This is a ready-to-use chat screen")
            Text("Customers just call ChatScreen() and it works!")
        }
    }
}
