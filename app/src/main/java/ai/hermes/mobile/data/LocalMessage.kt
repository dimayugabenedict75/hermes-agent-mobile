package ai.hermes.mobile.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class LocalMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: String,
    val role: String,
    val text: String,
    val audioUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
