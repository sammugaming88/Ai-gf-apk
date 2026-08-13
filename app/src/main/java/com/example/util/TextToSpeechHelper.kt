package com.example.util

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class TextToSpeechHelper(context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isInitialized = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val hindiLocale = Locale("hi", "IN")
            val result = tts?.setLanguage(hindiLocale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(Locale("en", "IN"))
            }

            // Find best sweet female voice if available on system
            try {
                val voices = tts?.voices
                if (!voices.isNullOrEmpty()) {
                    val femaleVoice = voices.find { voice ->
                        val name = voice.name.lowercase()
                        (name.contains("hi-in") || name.contains("hi_in") || name.contains("en-in") || name.contains("en_in")) &&
                                (name.contains("female") || name.contains("fem") || name.contains("f0") || name.contains("network"))
                    } ?: voices.find { voice ->
                        val name = voice.name.lowercase()
                        name.contains("female") || name.contains("fem")
                    }

                    if (femaleVoice != null) {
                        tts?.voice = femaleVoice
                    }
                }
            } catch (e: Exception) {
                // Fallback to default engine settings
            }

            isInitialized = true
            tts?.setPitch(1.30f) // Soft, sweet female voice pitch
            tts?.setSpeechRate(0.92f) // Natural, affectionate conversational pace
        }
    }

    fun speak(text: String, pitch: Float = 1.30f) {
        if (isInitialized) {
            tts?.setPitch(pitch)
            // Clean emojis, asterisks, stage directions (e.g. *giggles*, (laughs), [blushes]) for pure sweet human voice
            val cleanText = text
                .replace(Regex("\\*.*?\\*"), "") // remove *action*
                .replace(Regex("\\(.*?\\)"), "") // remove (action)
                .replace(Regex("\\[.*?\\]"), "") // remove [action]
                .replace(Regex("[\\p{So}\\p{Cn}]"), "") // remove emojis
                .trim()

            if (cleanText.isNotBlank()) {
                tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, "companion_voice_id")
            }
        }
    }

    fun stop() {
        if (isInitialized) {
            tts?.stop()
        }
    }

    fun shutdown() {
        if (isInitialized) {
            tts?.stop()
            tts?.shutdown()
            tts = null
            isInitialized = false
        }
    }
}

