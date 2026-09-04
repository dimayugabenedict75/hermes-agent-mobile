package ai.hermes.mobile.ui.chat

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: String = "default",
    val role: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)
