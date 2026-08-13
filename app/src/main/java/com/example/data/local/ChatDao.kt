package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.ChatMessageEntity
import com.example.data.model.CompanionMemoryEntity
import com.example.data.model.MoodLogEntity
import com.example.data.model.UserGiftEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages WHERE personaId = :personaId ORDER BY timestamp ASC")
    fun getMessagesForPersona(personaId: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity): Long

    @Query("DELETE FROM chat_messages WHERE personaId = :personaId")
    suspend fun clearMessagesForPersona(personaId: String)

    @Query("SELECT * FROM companion_memories WHERE personaId = :personaId ORDER BY timestamp DESC")
    fun getMemoriesForPersona(personaId: String): Flow<List<CompanionMemoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: CompanionMemoryEntity): Long

    @Query("DELETE FROM companion_memories WHERE id = :id")
    suspend fun deleteMemory(id: Long)

    @Query("SELECT * FROM mood_logs ORDER BY timestamp DESC")
    fun getAllMoodLogs(): Flow<List<MoodLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoodLog(moodLog: MoodLogEntity): Long

    @Query("SELECT * FROM user_gifts WHERE personaId = :personaId ORDER BY timestamp DESC")
    fun getGiftsForPersona(personaId: String): Flow<List<UserGiftEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserGift(gift: UserGiftEntity): Long
}
