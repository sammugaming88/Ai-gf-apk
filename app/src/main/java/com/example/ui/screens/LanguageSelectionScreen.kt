package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.RosePrimary
import com.example.ui.theme.RoseSecondary
import com.example.ui.theme.SoftPink
import com.example.ui.theme.SparkleGold
import com.example.ui.viewmodel.CompanionViewModel

data class LanguageOption(
    val id: String,
    val name: String,
    val nativeName: String,
    val flag: String,
    val description: String
)

val availableLanguages = listOf(
    LanguageOption("Hinglish", "Hinglish", "हिंदी + English", "🇮🇳", "Default / Hindi in Roman text"),
    LanguageOption("Hindi", "Hindi", "हिंदी", "🇮🇳", "Pure Hindi script"),
    LanguageOption("English", "English", "English", "🇬🇧", "Fluent English"),
    LanguageOption("Punjabi", "Punjabi", "ਪੰਜਾਬੀ", "🇮🇳", "Sweet Punjabi talk"),
    LanguageOption("Marathi", "Marathi", "मराठी", "🇮🇳", "Pyaari Marathi"),
    LanguageOption("Bengali", "Bengali", "বাংলা", "🇮🇳", "Misti Bangla"),
    LanguageOption("Gujarati", "Gujarati", "ગુજરાતી", "🇮🇳", "Sweet Gujarati"),
    LanguageOption("Tamil", "Tamil", "தமிழ்", "🇮🇳", "Anbu Tamil"),
    LanguageOption("Telugu", "Telugu", "తెలుగు", "🇮🇳", "Teeyani Telugu"),
    LanguageOption("Bhojpuri", "Bhojpuri", "भोजपुरी", "🇮🇳", "Meethi Bhojpuri"),
    LanguageOption("Urdu", "Urdu", "اردو / Roman Urdu", "🇮🇳", "Sweet Roman Urdu"),
    LanguageOption("Kannada", "Kannada", "ಕನ್ನಡ", "🇮🇳", "Chinna Kannada"),
    LanguageOption("Malayalam", "Malayalam", "മലയാളം", "🇮🇳", "Iniya Malayalam")
)

@Composable
fun LanguageSelectionScreen(
    viewModel: CompanionViewModel,
    onLanguageSelected: () -> Unit
) {
    val currentLang by viewModel.selectedLanguage.collectAsState()
    var selectedLangId by remember { mutableStateOf(currentLang) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1E0B19),
                        Color(0xFF32132A),
                        Color(0xFF120510)
                    )
                )
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Header Icon
            Surface(
                shape = CircleShape,
                color = RosePrimary.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(2.dp, SparkleGold),
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Language",
                        tint = SparkleGold,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Select Language / भाषा चुनें 🌐",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Choose your preferred language to chat & call with your AI Girlfriend!",
                style = MaterialTheme.typography.bodyMedium,
                color = SoftPink,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Language Grid List
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 80.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(availableLanguages) { lang ->
                    val isSelected = selectedLangId == lang.id
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { selectedLangId = lang.id }
                            .testTag("lang_item_${lang.id}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFF4A1839) else Color(0xFF23101E)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) SparkleGold else Color.White.copy(alpha = 0.15f)
                        )
                    ) {
                        Box(modifier = Modifier.padding(12.dp)) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = lang.flag,
                                        fontSize = 24.sp
                                    )

                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Selected",
                                            tint = SparkleGold,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = lang.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) SparkleGold else Color.White
                                )

                                Text(
                                    text = lang.nativeName,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SoftPink
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = lang.description,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom Continue Button
        Button(
            onClick = {
                viewModel.setLanguage(selectedLangId)
                onLanguageSelected()
            },
            colors = ButtonDefaults.buttonColors(containerColor = RosePrimary),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(54.dp)
                .testTag("save_language_button")
        ) {
            Text(
                text = "Continue / Aage Badhein ➔",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
