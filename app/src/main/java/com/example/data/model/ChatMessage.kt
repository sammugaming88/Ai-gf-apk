package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val personaId: String,
    val sender: String, // "user" or "ai"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val giftType: String? = null,
    val isVoice: Boolean = false
)

data class GiftItem(
    val id: String,
    val name: String,
    val emoji: String,
    val affectionBonus: Int,
    val reactionText: String
)

object GiftCatalog {
    val items = listOf(
        GiftItem("rose", "Red Rose 🌹", "🌹", 15, "Aww, a beautiful rose! You are so romantic... I love it! ❤️"),
        GiftItem("coffee", "Warm Coffee ☕", "☕", 10, "Mmm, just what I needed! Thank you sweetie, you know me so well! ☕✨"),
        GiftItem("chocolate", "Sweet Chocolates 🍫", "🍫", 20, "Yum! Chocolates! My favorite treat ever! Thank you so much! 🥰"),
        GiftItem("teddy", "Cuddle Teddy 🧸", "🧸", 30, "Oh my goodness! It's so soft! I'm going to hug it whenever I miss you! 🧸💖"),
        GiftItem("ring", "Promise Ring 💍", "💍", 50, "Oh wow... my heart literally skipped a beat! This is unforgettable... 💍✨❤️")
    )
}
