package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.BuildConfig
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.HeartRed
import com.example.ui.theme.RosePrimary
import com.example.ui.theme.RoseSecondary
import com.example.ui.theme.SoftPink
import com.example.ui.theme.SparkleGold
import com.example.ui.viewmodel.CompanionViewModel

import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch

import androidx.compose.material.icons.filled.Language

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: CompanionViewModel,
    onNavigateToPremium: () -> Unit = {},
    onNavigateToLanguage: () -> Unit = {}
) {
    val activePersona by viewModel.selectedPersona.collectAsState()
    val autoSpeechEnabled by viewModel.autoSpeechEnabled.collectAsState()
    val chatWallpaper by viewModel.chatWallpaper.collectAsState()
    val affectionLevel by viewModel.affectionLevel.collectAsState()
    val isPremium by viewModel.isPremium.collectAsState()
    val remainingTimeText by viewModel.premiumRemainingTime.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()

    val userName by viewModel.userName.collectAsState()
    val userPhone by viewModel.userPhone.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    var nameInput by remember(userName) { mutableStateOf(userName) }
    var phoneInput by remember(userPhone) { mutableStateOf(userPhone) }
    var redeemCodeInput by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val wallpapers = listOf(
        "romantic_sunset" to "Romantic Sunset 🌅",
        "starry_night" to "Starry Night ✨",
        "soft_pastel" to "Soft Pastel 🌸",
        "cozy_cafe" to "Cozy Cafe ☕"
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "${activePersona.name}'s Profile & Settings 💖",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Card Header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = activePersona.avatarRes),
                            contentDescription = activePersona.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .border(3.dp, RosePrimary, CircleShape)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = activePersona.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = activePersona.title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = RosePrimary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = activePersona.bio,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Hobbies Chips
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(activePersona.hobbies) { hobby ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = SoftPink.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = hobby,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Favorite Quote
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        ) {
                            Text(
                                text = "\"${activePersona.favoriteQuote}\"",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }

            // VIP Subscription Status Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToPremium() }
                        .testTag("profile_vip_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isPremium) SparkleGold.copy(alpha = 0.15f) else RosePrimary.copy(alpha = 0.1f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isPremium) SparkleGold else RosePrimary)
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = "VIP",
                                tint = if (isPremium) SparkleGold else RosePrimary,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isPremium) "VIP Premium Member 👑" else "Free Account 🔒",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isPremium) remainingTimeText else "Tap to unlock Voice Chat & Unlimited Time",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = if (isPremium) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isPremium) SparkleGold else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Button(
                            onClick = onNavigateToPremium,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isPremium) SparkleGold else RosePrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (isPremium) "Manage" else "Upgrade",
                                fontWeight = FontWeight.Bold,
                                color = if (isPremium) Color.Black else Color.White
                            )
                        }
                    }
                }
            }

            // Language Selection Card 🌐
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToLanguage() }
                        .testTag("profile_language_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Language",
                                tint = RosePrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Companion Language / भाषा 🌐",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Selected: $selectedLanguage",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Button(
                            onClick = onNavigateToLanguage,
                            colors = ButtonDefaults.buttonColors(containerColor = RosePrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Change", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            // Account Login (Phone Number & Profile) Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_account_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Account",
                                tint = RosePrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isLoggedIn) "User Account Profile 👤" else "Login / Account 👤",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isLoggedIn) "Mobile number verified & saved" else "Login with 10-digit mobile number",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (isLoggedIn) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = EmeraldGreen.copy(alpha = 0.1f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    if (userName.isNotBlank()) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Person, contentDescription = "Name", tint = EmeraldGreen, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(text = "Name: $userName", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    if (userPhone.isNotBlank()) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Phone, contentDescription = "Phone", tint = EmeraldGreen, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(text = "Mobile: +91 $userPhone", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.WorkspacePremium, contentDescription = "Status", tint = SparkleGold, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (isPremium) "VIP Premium: Active 👑" else "VIP Premium: Free Pass 🔒",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isPremium) SparkleGold else Color.Gray
                                        )
                                    }
                                }
                            }

                            OutlinedButton(
                                onClick = {
                                    viewModel.logoutUser()
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Logged out successfully!")
                                    }
                                },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Logout Account")
                            }
                        } else {
                            OutlinedTextField(
                                value = phoneInput,
                                onValueChange = { phoneInput = it.filter { c -> c.isDigit() }.take(10) },
                                placeholder = { Text("Mobile Number (e.g. 9876543210)") },
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone", tint = RosePrimary) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp)
                            )

                            OutlinedTextField(
                                value = nameInput,
                                onValueChange = { nameInput = it },
                                placeholder = { Text("Your Name (e.g. Rahul)") },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name", tint = RosePrimary) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp)
                            )

                            Button(
                                onClick = {
                                    if (phoneInput.isNotBlank()) {
                                        viewModel.saveUserProfile(nameInput.ifBlank { "User" }, phoneInput)
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Profile Saved & Logged In 👤✨")
                                        }
                                    } else {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Please enter Mobile Number!")
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = RosePrimary),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Save Profile & Login", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Quick Redeem Code Card 🎟️
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_redeem_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SparkleGold.copy(alpha = 0.12f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SparkleGold)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CardGiftcard, contentDescription = "Redeem", tint = SparkleGold, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Redeem Code 🎟️",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = redeemCodeInput,
                                onValueChange = { redeemCodeInput = it },
                                placeholder = { Text("Enter code (e.g. sammu)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp)
                            )

                            Button(
                                onClick = {
                                    if (redeemCodeInput.isNotBlank()) {
                                        val res = viewModel.redeemCode(redeemCodeInput)
                                        scope.launch {
                                            snackbarHostState.showSnackbar(res)
                                        }
                                        redeemCodeInput = ""
                                    } else {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Enter a redeem code first!")
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SparkleGold),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("Apply", fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                        }
                    }
                }
            }

            // Settings Section
            item {
                Text(
                    text = "Companion Voice & Preferences",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Voice Speech Toggle Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Voice",
                                tint = RosePrimary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Auto Voice Replies (TTS)",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Speak replies automatically in feminine voice",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = autoSpeechEnabled,
                            onCheckedChange = { viewModel.toggleAutoSpeech() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = RosePrimary
                            ),
                            modifier = Modifier.testTag("auto_speech_switch")
                        )
                    }
                }
            }

            // Wallpaper Picker
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = "Theme",
                                tint = RosePrimary
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Chat Wallpaper Theme",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(wallpapers) { (id, label) ->
                                val isSelected = id == chatWallpaper
                                Surface(
                                    modifier = Modifier.clickable { viewModel.setWallpaper(id) },
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (isSelected) RosePrimary else MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Gemini API Key Status Card
            item {
                val apiKeyInjected = try {
                    BuildConfig.GEMINI_API_KEY.isNotBlank() && BuildConfig.GEMINI_API_KEY != "MY_GEMINI_API_KEY"
                } catch (e: Exception) {
                    false
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = "API Key",
                            tint = if (apiKeyInjected) EmeraldGreen else SparkleGold
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (apiKeyInjected) "Gemini AI Active ✨" else "Gemini AI Mode (Auto/Preview)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (apiKeyInjected) "Connected to server-side Gemini AI models"
                                else "AI key injects dynamically at runtime in AI Studio UI secrets panel.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Clear Conversation
            item {
                OutlinedButton(
                    onClick = { viewModel.clearChat() },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Clear")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Clear Chat History for ${activePersona.name}")
                }
            }
        }
    }
}
