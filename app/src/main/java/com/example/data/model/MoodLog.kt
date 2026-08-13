package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mood_logs")
data class MoodLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val moodName: String, // "Happy", "Sad", "Stressed", "Romantic", "Lonely", "Excited"
    val emoji: String,
    val userNote: String,
    val companionResponse: String,
    val timestamp: Long = System.currentTimeMillis()
)
