package ai.hermes.mobile.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [LocalMessage::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
}
