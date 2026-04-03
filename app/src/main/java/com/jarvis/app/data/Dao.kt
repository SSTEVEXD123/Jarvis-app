package com.jarvis.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface JarvisDao {
    @Insert
    suspend fun insertMessage(message: ChatMessageEntity)

    @Query("SELECT * FROM messages ORDER BY createdAt ASC")
    fun observeMessages(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrompt(prompt: PromptEntity)

    @Query("SELECT * FROM prompts ORDER BY updatedAt DESC")
    fun observePrompts(): Flow<List<PromptEntity>>

    @Insert
    suspend fun insertPdf(record: PdfRecordEntity)

    @Query("SELECT * FROM pdf_records ORDER BY createdAt DESC")
    fun observePdfs(): Flow<List<PdfRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: MusicTrackEntity)

    @Query("SELECT * FROM music_history ORDER BY queriedAt DESC LIMIT 50")
    fun observeTracks(): Flow<List<MusicTrackEntity>>
}
