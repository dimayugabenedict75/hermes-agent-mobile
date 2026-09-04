package ai.hermes.mobile.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    suspend fun forSession(sessionId: String): List<LocalMessage>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: LocalMessage): Long
}
