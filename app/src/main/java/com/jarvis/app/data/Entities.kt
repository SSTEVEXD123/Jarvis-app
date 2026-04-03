package com.jarvis.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val role: String,
    val text: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "prompts")
data class PromptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val content: String,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "pdf_records")
data class PdfRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val filePath: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "music_history")
data class MusicTrackEntity(
    @PrimaryKey val videoId: String,
    val title: String,
    val artist: String,
    val streamUrl: String,
    val queriedAt: Long = System.currentTimeMillis()
)
