package com.jarvis.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ChatMessageEntity::class, PromptEntity::class, PdfRecordEntity::class, MusicTrackEntity::class],
    version = 1,
    exportSchema = false
)
abstract class JarvisDatabase : RoomDatabase() {
    abstract fun dao(): JarvisDao

    companion object {
        @Volatile
        private var instance: JarvisDatabase? = null

        fun get(context: Context, dbAbsolutePath: String): JarvisDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(context, JarvisDatabase::class.java, dbAbsolutePath)
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
        }
    }
}
