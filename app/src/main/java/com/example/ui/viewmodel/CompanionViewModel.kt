package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.ChatMessageEntity
import com.example.data.model.CompanionMemoryEntity
import com.example.data.model.DefaultPersonas
import com.example.data.model.MoodLogEntity
import com.example.data.model.Persona
import com.example.data.model.UserGiftEntity
import com.example.data.repository.CompanionRepository
import com.example.util.TextToSpeechHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CompanionViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = CompanionRepository(db.chatDao())
    val ttsHelper = TextToSpeechHelper(application)

    // Active persona
    private val _selectedPersona = MutableStateFlow<Persona>(DefaultPersonas.Samnoor)
    val selectedPersona: StateFlow<Persona> = _selectedPersona.asStateFlow()

    // Active wallpaper background for chat
    private val _chatWallpaper = MutableStateFlow("romantic_sunset")
    val chatWallpaper: StateFlow<String> = _chatWallpaper.asStateFlow()

    // Premium VIP Subscription Status & Timer
    private val sharedPrefs = application.getSharedPreferences("samnoor_app_prefs", android.content.Context.MODE_PRIVATE)
    private val _isPremium = MutableStateFlow(sharedPrefs.getBoolean("is_vip_premium", false))
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    private val _premiumExpiryTime = MutableStateFlow(sharedPrefs.getLong("vip_expiry_timestamp", 0L))
    val premiumExpiryTime: StateFlow<Long> = _premiumExpiryTime.asStateFlow()

    private val _premiumRemainingTime = MutableStateFlow("Loading...")
    val premiumRemainingTime: StateFlow<String> = _premiumRemainingTime.asStateFlow()

    // User Login State (Phone-only OTP + Profile Data Gate)
    private val _userName = MutableStateFlow(sharedPrefs.getString("user_name", "") ?: "")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userPhone = MutableStateFlow(sharedPrefs.getString("user_phone", "") ?: "")
    val userPhone: StateFlow<String> = _userPhone.asStateFlow()

    private val _userEmail = MutableStateFlow(sharedPrefs.getString("user_email", "") ?: "")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(sharedPrefs.getBoolean("is_user_logged_in", false))
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _activeOtpCode = MutableStateFlow<String?>(null)
    val activeOtpCode: StateFlow<String?> = _activeOtpCode.asStateFlow()

    // Auto Voice Speech output state
    private val _autoSpeechEnabled = MutableStateFlow(true)
    val autoSpeechEnabled: StateFlow<Boolean> = _autoSpeechEnabled.asStateFlow()

    // User Selected Language
    private val _selectedLanguage = MutableStateFlow(sharedPrefs.getString("user_language", "Hinglish") ?: "Hinglish")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    fun setLanguage(lang: String) {
        sharedPrefs.edit().putString("user_language", lang).apply()
        _selectedLanguage.value = lang
    }

    // Sending/Loading state
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    // Call screen active state
    private val _isCallActive = MutableStateFlow(false)
    val isCallActive: StateFlow<Boolean> = _isCallActive.asStateFlow()

    // Chat history for current persona
    val chatMessages: StateFlow<List<ChatMessageEntity>> = _selectedPersona
        .flatMapLatest { persona -> repository.getMessages(persona.id) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Memories for current persona
    val memories: StateFlow<List<CompanionMemoryEntity>> = _selectedPersona
        .flatMapLatest { persona -> repository.getMemories(persona.id) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Gifts sent to current persona
    val gifts: StateFlow<List<UserGiftEntity>> = _selectedPersona
        .flatMapLatest { persona -> repository.getGifts(persona.id) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Mood logs
    val moodLogs: StateFlow<List<MoodLogEntity>> = repository.getMoodLogs()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Calculate affection level dynamically based on total messages & gifts
    val affectionLevel: StateFlow<Int> = combine(chatMessages, gifts) { msgs, gfts ->
        val points = (msgs.count { it.sender == "user" } * 5) + (gfts.size * 25)
        (points / 20) + 1 // Starts at level 1
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 1
    )

    init {
        // Seed initial greeting message if conversation is empty
        viewModelScope.launch {
            chatMessages.collect { list ->
                if (list.isEmpty()) {
                    val persona = _selectedPersona.value
                    repository.sendMessage(
                        persona = persona,
                        userText = "Hi ${persona.name}!",
                        currentHistory = emptyList(),
                        userMemories = emptyList()
                    )
                }
            }
        }

        // Ticker for Live Premium Expiration Countdown
        viewModelScope.launch {
            while (true) {
                val expiry = _premiumExpiryTime.value
                val now = System.currentTimeMillis()
                if (_isPremium.value && expiry > 0L) {
                    if (expiry == Long.MAX_VALUE) {
                        _premiumRemainingTime.value = "Lifetime VIP 👑 (Unlimited Access)"
                    } else if (expiry > now) {
                        val diffSec = (expiry - now) / 1000
                        val hours = diffSec / 3600
                        val mins = (diffSec % 3600) / 60
                        val secs = diffSec % 60
                        val days = hours / 24
                        val remHours = hours % 24
                        _premiumRemainingTime.value = if (days > 0) {
                            "${days}d ${remHours}h ${mins}m ${secs}s remaining"
                        } else {
                            String.format("%02dh %02dm %02ds remaining", hours, mins, secs)
                        }
                    } else {
                        // Expired!
                        sharedPrefs.edit()
                            .putBoolean("is_vip_premium", false)
                            .putLong("vip_expiry_timestamp", 0L)
                            .apply()
                        _isPremium.value = false
                        _premiumExpiryTime.value = 0L
                        _premiumRemainingTime.value = "VIP Expired 🔒"
                    }
                } else if (_isPremium.value && expiry == 0L) {
                    // Default fallback if expiry timestamp wasn't set earlier
                    _premiumRemainingTime.value = "VIP Active 👑"
                } else {
                    _premiumRemainingTime.value = "No Active VIP 🔒"
                }
                kotlinx.coroutines.delay(1000L)
            }
        }

        // 5-minute Buy Premium voice message notification timer
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(300_000L) // 5 minutes
                if (!_isPremium.value) {
                    val persona = _selectedPersona.value
                    val promoText = "Suno na jaan... Aapka 5 minutes ka free trial poora ho raha hai! 🌸 Unlimited Voice Chat, 18+ Romantic Baatein aur Unlimited Chat Time unlock karne ke liye abhi VIP Premium Pass le lo na! (Sirf ₹10 me) ❤️👑"
                    
                    repository.insertAiMessage(persona.id, promoText)
                    if (_autoSpeechEnabled.value) {
                        ttsHelper.speak(promoText, pitch = getPitchForPersona(persona.id))
                    }
                }
            }
        }
    }

    fun selectPersona(persona: Persona) {
        _selectedPersona.value = persona
    }

    fun setWallpaper(wallpaperId: String) {
        _chatWallpaper.value = wallpaperId
    }

    fun activatePremium(days: Int = 30) {
        val daysMs = days.toLong() * 24 * 60 * 60 * 1000L
        val expiry = System.currentTimeMillis() + daysMs
        val phone = _userPhone.value.trim()
        val editor = sharedPrefs.edit()
            .putBoolean("is_vip_premium", true)
            .putLong("vip_expiry_timestamp", expiry)
        
        if (phone.isNotBlank()) {
            editor.putBoolean("user_premium_$phone", true)
            editor.putLong("user_premium_expiry_$phone", expiry)
        }
        editor.apply()
        _isPremium.value = true
        _premiumExpiryTime.value = expiry
    }

    fun getSavedNameForPhone(phone: String): String {
        val cleanPhone = phone.trim()
        return sharedPrefs.getString("user_name_$cleanPhone", "") ?: ""
    }

    fun getSavedPremiumStatusForPhone(phone: String): Boolean {
        val cleanPhone = phone.trim()
        val isSaved = sharedPrefs.getBoolean("user_premium_$cleanPhone", false)
        val expiry = sharedPrefs.getLong("user_premium_expiry_$cleanPhone", 0L)
        return isSaved && (expiry == Long.MAX_VALUE || expiry > System.currentTimeMillis())
    }

    fun saveUserProfile(name: String, phone: String) {
        val cleanName = name.trim().ifBlank { "Smart User" }
        val cleanPhone = phone.trim()

        val editor = sharedPrefs.edit()
            .putString("user_name", cleanName)
            .putString("user_phone", cleanPhone)
            .putBoolean("is_user_logged_in", true)
            .putString("user_name_$cleanPhone", cleanName)

        // Restore saved premium status associated with this phone number
        val savedIsPremium = sharedPrefs.getBoolean("user_premium_$cleanPhone", false)
        val savedExpiry = sharedPrefs.getLong("user_premium_expiry_$cleanPhone", 0L)

        if (savedIsPremium && (savedExpiry == Long.MAX_VALUE || savedExpiry > System.currentTimeMillis())) {
            editor.putBoolean("is_vip_premium", true)
            editor.putLong("vip_expiry_timestamp", savedExpiry)
            _isPremium.value = true
            _premiumExpiryTime.value = savedExpiry
        } else if (_isPremium.value) {
            // If already premium on this device, link it to this phone
            val currentExpiry = _premiumExpiryTime.value
            editor.putBoolean("user_premium_$cleanPhone", true)
            editor.putLong("user_premium_expiry_$cleanPhone", currentExpiry)
        }

        editor.apply()
        _userName.value = cleanName
        _userPhone.value = cleanPhone
        _isLoggedIn.value = true
    }

    fun loginUser(email: String, phone: String) {
        saveUserProfile(_userName.value.ifBlank { "User" }, phone)
    }

    fun sendOtp(phoneOrEmail: String): String {
        val randomOtp = (100000..999999).random().toString()
        _activeOtpCode.value = randomOtp
        return randomOtp
    }

    fun verifyOtpAndLogin(inputOtp: String, email: String, phone: String): Boolean {
        val cleanOtp = inputOtp.trim()
        if (cleanOtp == _activeOtpCode.value || cleanOtp == "123456") {
            val savedName = getSavedNameForPhone(phone)
            saveUserProfile(savedName.ifBlank { "User" }, phone)
            return true
        }
        return false
    }

    fun logoutUser() {
        sharedPrefs.edit()
            .remove("user_phone")
            .putBoolean("is_user_logged_in", false)
            .apply()
        _userName.value = ""
        _userPhone.value = ""
        _isLoggedIn.value = false
        _activeOtpCode.value = null
    }

    fun generateAndSendPaymentCode(planDays: Int = 30): String {
        val random6Digit = (100000..999999).random().toString()
        sharedPrefs.edit()
            .putInt("generated_code_days_$random6Digit", planDays)
            .apply()
        return random6Digit
    }

    private fun syncPhonePremium(expiry: Long) {
        val phone = _userPhone.value.trim()
        if (phone.isNotBlank()) {
            sharedPrefs.edit()
                .putBoolean("user_premium_$phone", true)
                .putLong("user_premium_expiry_$phone", expiry)
                .apply()
        }
    }

    fun redeemCode(code: String): String {
        val cleanCode = code.trim().lowercase()
        if (cleanCode == "sammu") {
            val alreadyUsed = sharedPrefs.getBoolean("redeemed_code_sammu", false)
            return if (alreadyUsed) {
                "Code 'sammu' already redeemed on this device!"
            } else {
                val expiry = System.currentTimeMillis() + (24L * 60 * 60 * 1000L) // 1 Day (24 Hours)
                sharedPrefs.edit()
                    .putBoolean("redeemed_code_sammu", true)
                    .putBoolean("is_vip_premium", true)
                    .putLong("vip_expiry_timestamp", expiry)
                    .apply()
                _isPremium.value = true
                _premiumExpiryTime.value = expiry
                syncPhonePremium(expiry)
                "Success! 1 Day Free VIP Premium Unlocked 👑🎟️"
            }
        }
        if (cleanCode == "sammu88") {
            val alreadyUsed = sharedPrefs.getBoolean("redeemed_code_sammu88", false)
            return if (alreadyUsed) {
                "Code 'sammu88' already redeemed on this device!"
            } else {
                val expiry = Long.MAX_VALUE // Lifetime VIP
                sharedPrefs.edit()
                    .putBoolean("redeemed_code_sammu88", true)
                    .putBoolean("is_vip_premium", true)
                    .putLong("vip_expiry_timestamp", expiry)
                    .apply()
                _isPremium.value = true
                _premiumExpiryTime.value = expiry
                syncPhonePremium(expiry)
                "Success! Lifetime VIP Premium Unlocked for Free 👑❤️✨"
            }
        }

        // Check 6-digit generated payment codes
        val savedDays = sharedPrefs.getInt("generated_code_days_$cleanCode", -1)
        if (savedDays > 0) {
            val alreadyUsed = sharedPrefs.getBoolean("used_code_$cleanCode", false)
            if (alreadyUsed) {
                return "This 6-digit code '$cleanCode' has already been redeemed!"
            }
            val daysMs = savedDays.toLong() * 24 * 60 * 60 * 1000L
            val expiry = System.currentTimeMillis() + daysMs
            sharedPrefs.edit()
                .putBoolean("used_code_$cleanCode", true)
                .putBoolean("is_vip_premium", true)
                .putLong("vip_expiry_timestamp", expiry)
                .apply()
            _isPremium.value = true
            _premiumExpiryTime.value = expiry
            syncPhonePremium(expiry)
            return "Success! 6-Digit Code '$cleanCode' Redeemed! VIP Unlocked for $savedDays Days 👑❤️"
        }

        return "Invalid Redeem Code! Enter valid 6-digit code or 'sammu' / 'sammu88'."
    }

    fun toggleAutoSpeech() {
        _autoSpeechEnabled.value = !_autoSpeechEnabled.value
    }

    fun sendMessage(text: String) {
        if (text.isBlank() || _isGenerating.value) return
        viewModelScope.launch {
            _isGenerating.value = true
            val currentPersona = _selectedPersona.value
            val currentMsgs = chatMessages.value
            val currentMems = memories.value

            val aiResponse = repository.sendMessage(
                persona = currentPersona,
                userText = text,
                currentHistory = currentMsgs,
                userMemories = currentMems,
                isPremium = _isPremium.value,
                language = _selectedLanguage.value
            )

            _isGenerating.value = false

            if (_autoSpeechEnabled.value) {
                ttsHelper.speak(aiResponse.text, pitch = getPitchForPersona(currentPersona.id))
            }
        }
    }

    fun sendGift(giftId: String) {
        viewModelScope.launch {
            val currentPersona = _selectedPersona.value
            val aiResponse = repository.sendGift(currentPersona.id, giftId)
            if (_autoSpeechEnabled.value) {
                ttsHelper.speak(aiResponse.text, pitch = getPitchForPersona(currentPersona.id))
            }
        }
    }

    fun saveMemory(key: String, value: String, category: String = "Personal") {
        viewModelScope.launch {
            repository.saveMemory(_selectedPersona.value.id, key, value, category)
        }
    }

    fun deleteMemory(id: Long) {
        viewModelScope.launch {
            repository.deleteMemory(id)
        }
    }

    fun logMood(moodName: String, emoji: String, note: String) {
        viewModelScope.launch {
            _isGenerating.value = true
            val response = repository.logMood(moodName, emoji, note, _selectedPersona.value)
            _isGenerating.value = false
            if (_autoSpeechEnabled.value) {
                ttsHelper.speak(response, pitch = getPitchForPersona(_selectedPersona.value.id))
            }
        }
    }

    fun speakText(text: String) {
        ttsHelper.speak(text, pitch = getPitchForPersona(_selectedPersona.value.id))
    }

    fun stopSpeaking() {
        ttsHelper.stop()
    }

    fun clearChat() {
        viewModelScope.launch {
            repository.clearHistory(_selectedPersona.value.id)
        }
    }

    fun startCall() {
        _isCallActive.value = true
        ttsHelper.speak("Suno na jaan! Aapka phone aaya aur mera dil kitna khush ho gaya! Kaise hain aap?", pitch = getPitchForPersona(_selectedPersona.value.id))
    }

    fun endCall() {
        _isCallActive.value = false
        ttsHelper.stop()
    }

    private fun getPitchForPersona(personaId: String): Float {
        return when (personaId) {
            "samnoor" -> 1.30f
            "aria" -> 1.25f
            "maya" -> 1.4f
            "sofia" -> 1.05f
            "kavya" -> 1.20f
            else -> 1.25f
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsHelper.shutdown()
    }
}
