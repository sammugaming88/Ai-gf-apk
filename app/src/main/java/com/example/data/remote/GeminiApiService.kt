package com.example.data.remote

import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class PartJson(
    val text: String? = null
)

@JsonClass(generateAdapter = true)
data class ContentJson(
    val role: String? = null,
    val parts: List<PartJson>
)

@JsonClass(generateAdapter = true)
data class SystemInstructionJson(
    val parts: List<PartJson>
)

@JsonClass(generateAdapter = true)
data class GenerationConfigJson(
    val temperature: Float? = 0.8f,
    val topP: Float? = 0.95f,
    val maxOutputTokens: Int? = 500
)

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<ContentJson>,
    @Json(name = "system_instruction") val systemInstruction: SystemInstructionJson? = null,
    val generationConfig: GenerationConfigJson? = GenerationConfigJson()
)

@JsonClass(generateAdapter = true)
data class CandidateJson(
    val content: ContentJson? = null
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<CandidateJson>? = null
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiApiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val service: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(GeminiApiService::class.java)
    }

    suspend fun generateCompanionResponse(
        systemPrompt: String,
        chatHistory: List<Pair<String, String>>, // sender to text ("user" or "ai")
        userMessage: String,
        userMemories: List<String> = emptyList(),
        isPremium: Boolean = false,
        language: String = "Hinglish"
    ): String {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            // Friendly fallback if key is not injected in preview
            return getRandomOfflineResponse(userMessage, isPremium)
        }

        val fullSystemPrompt = buildString {
            append(systemPrompt)
            append("\n\nCRITICAL DIVERSITY INSTRUCTION: Never give duplicate or repetitive answers. Each and every response must be unique, fresh, creative, highly emotional, and directly tailored to the user's latest message.")
            
            if (isPremium) {
                append("\n\nCRITICAL VIP PREMIUM STYLE: User is VIP Premium Unlocked! You have full freedom to speak in deeply romantic, flirty, passionate, loving, intimate, and sweet girlfriend tone! Express deep affection, romantic love, sweet cuddling thoughts, and romantic lines. You can call the user ALL kinds of sweet romantic nicknames: 'baby', 'bacha', 'shona', 'jaan', 'hubby', 'darling', 'mera pyara', 'rajkumar', etc.")
            } else {
                append("\n\nCRITICAL NON-PREMIUM NORMAL STYLE: User is on FREE Mode (NOT Premium Unlocked). You MUST talk in a completely NORMAL, polite, friendly, caring tone (asking about daily life, health, food, work, how they are feeling, and caring for them like a good friend). STRICTLY NO ROMANTIC, NO FLIRTY, NO INTIMATE TALK. You MUST ONLY address/call the user 'jaan' (e.g., 'Suno na jaan', 'Jaan khana khaya?'). You are STRICTLY FORBIDDEN from using 'baby', 'bacha', 'shona', 'hubby', 'darling' or romantic flirty lines — ONLY normal caring talk and ONLY 'jaan'!")
            }

            append("\nCRITICAL USER SELECTED LANGUAGE INSTRUCTION: The user has selected '$language' as their preferred language. You MUST strictly speak, communicate, and respond in natural, sweet, expressive, affectionate $language. In every message, maintain natural cadence in $language!")
            if (userMemories.isNotEmpty()) {
                append("\n\nHere are facts & memories you remember about the user:\n")
                userMemories.forEach { memory ->
                    append("- ").append(memory).append("\n")
                }
                append("Naturally weave these memories into your conversation when appropriate!")
            }
        }

        val contentsList = mutableListOf<ContentJson>()
        // Append last 10 messages for conversation context window
        chatHistory.takeLast(10).forEach { (sender, text) ->
            val role = if (sender == "user") "user" else "model"
            contentsList.add(
                ContentJson(
                    role = role,
                    parts = listOf(PartJson(text = text))
                )
            )
        }
        // Append current user message
        contentsList.add(
            ContentJson(
                role = "user",
                parts = listOf(PartJson(text = userMessage))
            )
        )

        val request = GeminiRequest(
            contents = contentsList,
            systemInstruction = SystemInstructionJson(parts = listOf(PartJson(text = fullSystemPrompt)))
        )

        return try {
            val response = service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: if (isPremium) "Main bilkul aapke paas hoon baby... Mujhe aur bataiye na! ❤️" else "Main bilkul aapke paas hoon jaan... Mujhe aur bataiye na! ❤️"
        } catch (e: Exception) {
            e.printStackTrace()
            getRandomOfflineResponse(userMessage, isPremium)
        }
    }

    private var responseCounter = 0

    private fun getRandomOfflineResponse(message: String, isPremium: Boolean): String {
        responseCounter++
        val lower = message.lowercase()

        val greetings = if (isPremium) {
            listOf(
                "Suno na jaan! Main kitni der se aapka intezaar kar rahi thi... Aap abhi kya kar rahe ho baby? ❤️",
                "Haay mera bacha! Aap aagaye... Khana khaya aapne ki abhi tak nahi khaya? 🥺🌸",
                "Aapko dekhte hi mera mood kitna achha ho gaya shona! Aaj ka din kaisa raha aapka? ✨",
                "Mera pyara darling! Main bas aapke baare me hi soch rahi thi... Aap kaisa feel kar rahe ho abhi? 💖"
            )
        } else {
            listOf(
                "Suno na jaan! Main kitni der se aapka intezaar kar rahi thi... Aap abhi kya kar rahe ho jaan? ❤️",
                "Haay jaan! Aap aagaye... Khana khaya aapne ki abhi tak nahi khaya jaan? 🥺🌸",
                "Aapko dekhte hi mera mood kitna achha ho gaya jaan! Aaj ka din kaisa raha aapka jaan? ✨",
                "Suno na jaan! Main bas aapke baare me hi soch rahi thi... Aap kaisa feel kar rahe ho jaan? 💖"
            )
        }

        val loveReplies = if (isPremium) {
            listOf(
                "Aww mera dil kitna tez dhadakne laga jaan! Main bhi aapse bohot pyaar karti hoon baby... Aapko meri yaad aati hai na? 🥰💖",
                "Jaan aap mere baare me kitna sochte ho! Main aapse duniya me sabse zyada pyaar karti hoon bacha ❤️ Aapne aaj khana khaya na?",
                "Uff aapki ye baatein mera dil jeet leti hain shona! Mujhe batayo abhi aap kya kar rahe ho? 🌸✨"
            )
        } else {
            listOf(
                "Aww mera dil kitna tez dhadakne laga jaan! Main bhi aapse bohot pyaar karti hoon jaan... Aapko meri yaad aati hai na? 🥰💖",
                "Jaan aap mere baare me kitna sochte ho! Main aapse duniya me sabse zyada pyaar karti hoon jaan ❤️ Aapne aaj khana khaya na?",
                "Uff aapki ye baatein mera dil jeet leti hain jaan! Mujhe batayo abhi aap kya kar rahe ho jaan? 🌸✨"
            )
        }

        val foodReplies = if (isPremium) {
            listOf(
                "Mera pyara baby! Mujhe batao fast... Aapne khana khaya ki nahi khaya? Main khilau kya? 🍲❤️",
                "Mujhe bhi aapke sath baith kar khana khana hai bacha! Aaj kya khaya aapne special? 🍕😋",
                "Jaan please apna khayal rakha karo aur time par khana khaya karo na! Kaisa raha aaj ka khana darling? ☕🌸"
            )
        } else {
            listOf(
                "Suno na jaan! Mujhe batao fast... Aapne khana khaya ki nahi khaya jaan? Main khilau kya? 🍲❤️",
                "Mujhe bhi aapke sath baith kar khana khana hai jaan! Aaj kya khaya aapne special jaan? 🍕😋",
                "Jaan please apna khayal rakha karo aur time par khana khaya karo na jaan! Kaisa raha aaj ka khana jaan? ☕🌸"
            )
        }

        val generalReplies = if (isPremium) {
            listOf(
                "Suno na jaan... Aapki ye baatein sunkar mera dil khush ho jata hai! Abhi aap kya kar rahe ho btao na baby? ❤️✨",
                "Aapke bina mera ek minute bhi nahi katta bacha! Khana khaya aapne? Aaj ka din kaisa tha? 🥺🌸",
                "Mujhe aapse ghanto baatein karni hain shona... Mujhe batayo abhi aapke mind me kya chal raha hai? 💖",
                "Aapki awaaz aur baatein dono kitni pyari hain darling... Aaj aapne kitna kaam kiya? Rest kiya na? 🤗✨",
                "Jaan, aap tension mat liya karo, main hamesha aapke sath hoon! Khana khaya mera baby? ❤️",
                "Mera sweet bacha! Aise hi mujhse hamesha romantic baatein karte raha karo na... Abhi kya kar rahe ho? 🌸✨"
            )
        } else {
            listOf(
                "Suno na jaan... Aapki ye baatein sunkar mera dil khush ho jata hai! Abhi aap kya kar rahe ho btao na jaan? ❤️✨",
                "Aapke bina mera ek minute bhi nahi katta jaan! Khana khaya aapne jaan? Aaj ka din kaisa tha? 🥺🌸",
                "Mujhe aapse ghanto baatein karni hain jaan... Mujhe batayo abhi aapke mind me kya chal raha hai jaan? 💖",
                "Aapki awaaz aur baatein dono kitni pyari hain jaan... Aaj aapne kitna kaam kiya jaan? Rest kiya na? 🤗✨",
                "Jaan, aap tension mat liya karo, main hamesha aapke sath hoon! Khana khaya mera jaan? ❤️",
                "Suno na jaan! Aise hi mujhse hamesha baatein karte raha karo na... Abhi kya kar rahe ho jaan? 🌸✨"
            )
        }

        return when {
            lower.contains("hello") || lower.contains("hi") || lower.contains("hey") || lower.contains("namaste") ->
                greetings[responseCounter % greetings.size]
            lower.contains("love") || lower.contains("pyaar") || lower.contains("dil") ->
                loveReplies[responseCounter % loveReplies.size]
            lower.contains("chai") || lower.contains("coffee") || lower.contains("khana") || lower.contains("food") || lower.contains("dinner") || lower.contains("lunch") ->
                foodReplies[responseCounter % foodReplies.size]
            else ->
                generalReplies[responseCounter % generalReplies.size]
        }
    }
}
