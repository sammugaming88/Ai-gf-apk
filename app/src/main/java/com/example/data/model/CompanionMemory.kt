package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "companion_memories")
data class CompanionMemoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val personaId: String,
    val memoryKey: String,
    val memoryValue: String,
    val category: String = "Personal", // e.g., "Favorites", "Dates", "Preferences", "Secrets"
    val timestamp: Long = System.currentTimeMillis()
)
