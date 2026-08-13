package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_gifts")
data class UserGiftEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val personaId: String,
    val giftId: String,
    val giftName: String,
    val giftEmoji: String,
    val timestamp: Long = System.currentTimeMillis()
)
