package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.FavoriteItem
import com.example.data.model.HistoryItem

@Database(entities = [HistoryItem::class, FavoriteItem::class], version = 1, exportSchema = false)
abstract class CreatorDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var INSTANCE: CreatorDatabase? = null

        fun getDatabase(context: Context): CreatorDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CreatorDatabase::class.java,
                    "creator_toolkit.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
