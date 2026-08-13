package com.example.data.repository

import com.example.data.local.ChatDao
import com.example.data.model.ChatMessageEntity
import com.example.data.model.CompanionMemoryEntity
import com.example.data.model.DefaultPersonas
import com.example.data.model.GiftCatalog
import com.example.data.model.MoodLogEntity
import com.example.data.model.Persona
import com.example.data.model.UserGiftEntity
import com.example.data.remote.GeminiApiClient
import kotlinx.coroutines.flow.Flow

class CompanionRepository(private val chatDao: ChatDao) {

    fun getMessages(personaId: String): Flow<List<ChatMessageEntity>> =
        chatDao.getMessagesForPersona(personaId)

    fun getMemories(personaId: String): Flow<List<CompanionMemoryEntity>> =
        chatDao.getMemoriesForPersona(personaId)

    fun getGifts(personaId: String): Flow<List<UserGiftEntity>> =
        chatDao.getGiftsForPersona(personaId)

    fun getMoodLogs(): Flow<List<MoodLogEntity>> =
        chatDao.getAllMoodLogs()

    suspend fun sendMessage(
        persona: Persona,
        userText: String,
        currentHistory: List<ChatMessageEntity>,
        userMemories: List<CompanionMemoryEntity>,
        isPremium: Boolean = false,
        language: String = "Hinglish"
    ): ChatMessageEntity {
        // Save user message
        val userMsgEntity = ChatMessageEntity(
            personaId = persona.id,
            sender = "user",
            text = userText
        )
        chatDao.insertMessage(userMsgEntity)

        // Convert history for Gemini API
        val historyPairs = currentHistory.map { it.sender to it.text }
        val memoryStrings = userMemories.map { "${it.memoryKey}: ${it.memoryValue}" }

        // Call Gemini API
        val aiReplyText = GeminiApiClient.generateCompanionResponse(
            systemPrompt = persona.systemPrompt,
            chatHistory = historyPairs,
            userMessage = userText,
            userMemories = memoryStrings,
            isPremium = isPremium,
            language = language
        )

        val aiMsgEntity = ChatMessageEntity(
            personaId = persona.id,
            sender = "ai",
            text = aiReplyText
        )
        chatDao.insertMessage(aiMsgEntity)

        // Auto-extract memories if user mentions favorites or nickname
        autoSaveMemoryIfDetected(persona.id, userText)

        return aiMsgEntity
    }

    suspend fun sendGift(personaId: String, giftId: String): ChatMessageEntity {
        val gift = GiftCatalog.items.firstOrNull { it.id == giftId } ?: GiftCatalog.items.first()

        // Log gift
        chatDao.insertUserGift(
            UserGiftEntity(
                personaId = personaId,
                giftId = gift.id,
                giftName = gift.name,
                giftEmoji = gift.emoji
            )
        )

        // Add user gift message
        chatDao.insertMessage(
            ChatMessageEntity(
                personaId = personaId,
                sender = "user",
                text = "Gave you a gift: ${gift.name} ${gift.emoji}",
                giftType = gift.id
            )
        )

        // AI reaction message
        val aiReactionMsg = ChatMessageEntity(
            personaId = personaId,
            sender = "ai",
            text = gift.reactionText
        )
        chatDao.insertMessage(aiReactionMsg)
        return aiReactionMsg
    }

    suspend fun saveMemory(personaId: String, key: String, value: String, category: String = "Personal") {
        chatDao.insertMemory(
            CompanionMemoryEntity(
                personaId = personaId,
                memoryKey = key,
                memoryValue = value,
                category = category
            )
        )
    }

    suspend fun deleteMemory(memoryId: Long) {
        chatDao.deleteMemory(memoryId)
    }

    suspend fun logMood(moodName: String, emoji: String, userNote: String, persona: Persona): String {
        val prompt = "User says they are feeling $moodName today. Their note: '$userNote'. Give a short, ultra-comforting, loving response as his girlfriend."
        val response = GeminiApiClient.generateCompanionResponse(
            systemPrompt = persona.systemPrompt,
            chatHistory = emptyList(),
            userMessage = prompt
        )

        chatDao.insertMoodLog(
            MoodLogEntity(
                moodName = moodName,
                emoji = emoji,
                userNote = userNote,
                companionResponse = response
            )
        )

        // Also add to chat as AI message
        chatDao.insertMessage(
            ChatMessageEntity(
                personaId = persona.id,
                sender = "ai",
                text = response
            )
        )

        return response
    }

    suspend fun clearHistory(personaId: String) {
        chatDao.clearMessagesForPersona(personaId)
    }

    suspend fun insertAiMessage(personaId: String, text: String): ChatMessageEntity {
        val aiMsgEntity = ChatMessageEntity(
            personaId = personaId,
            sender = "ai",
            text = text
        )
        chatDao.insertMessage(aiMsgEntity)
        return aiMsgEntity
    }

    private suspend fun autoSaveMemoryIfDetected(personaId: String, userText: String) {
        val lower = userText.lowercase()
        if (lower.contains("my name is") || lower.contains("call me")) {
            val parts = userText.split("is", "me", ignoreCase = true)
            if (parts.size > 1) {
                val name = parts.last().trim().take(20)
                saveMemory(personaId, "User Nickname", name, "Personal")
            }
        } else if (lower.contains("i love") || lower.contains("my favorite")) {
            saveMemory(personaId, "Favorite Item", userText.take(50), "Favorites")
        }
    }
}
