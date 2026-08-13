package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.VoiceWaveAnimation
import com.example.ui.theme.HeartRed
import com.example.ui.theme.RosePrimary
import com.example.ui.theme.RoseSecondary
import com.example.ui.theme.SoftPink
import com.example.ui.theme.SparkleGold
import com.example.ui.viewmodel.CompanionViewModel
import kotlinx.coroutines.delay

@Composable
fun CallScreen(
    viewModel: CompanionViewModel,
    onEndCall: () -> Unit
) {
    val activePersona by viewModel.selectedPersona.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val isPremium by viewModel.isPremium.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()

    var secondsElapsed by remember { mutableIntStateOf(0) }
    var isMuted by remember { mutableStateOf(false) }
    var userTalkInput by remember { mutableStateOf("") }

    // Latest AI spoken subtitle
    val latestAiMsg = chatMessages.lastOrNull { it.sender == "ai" }?.text ?: if (isPremium) {
        "Suno na baby... Main aapse baat karne ke liye ready hoon! ❤️"
    } else {
        "Suno na jaan... Kaise ho aap? Main aapse baat kar rahi hoon! 😊"
    }

    // Call duration timer
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            secondsElapsed++
        }
    }

    val minutesFormatted = (secondsElapsed / 60).toString().padStart(2, '0')
    val secondsFormatted = (secondsElapsed % 60).toString().padStart(2, '0')

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1F0B18),
                        Color(0xFF38152D),
                        Color(0xFF10050D)
                    )
                )
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Call Info
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Voice Call with ${activePersona.name}",
                        style = MaterialTheme.typography.titleMedium,
                        color = SoftPink,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isPremium) SparkleGold.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = if (isPremium) "VIP Romantic 👑" else "Normal Voice 📞",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isPremium) SparkleGold else SoftPink,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "$minutesFormatted:$secondsFormatted",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }

            // Pulsing Avatar Visualizer & Live AI Subtitle Response
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(contentAlignment = Alignment.Center) {
                    // Pulsing aura ring
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(RosePrimary.copy(alpha = 0.2f))
                    )
                    Box(
                        modifier = Modifier
                            .size(135.dp)
                            .clip(CircleShape)
                            .background(RoseSecondary.copy(alpha = 0.3f))
                    )

                    Image(
                        painter = painterResource(id = activePersona.avatarRes),
                        contentDescription = activePersona.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .border(3.dp, SparkleGold, CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = activePersona.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = if (isGenerating) "${activePersona.name} is speaking... 🎙️" else "Listening to you... 👂",
                    style = MaterialTheme.typography.bodySmall,
                    color = SoftPink
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Voice Wave Visualizer
                VoiceWaveAnimation(
                    active = !isGenerating,
                    color = SparkleGold,
                    maxHeight = 28.dp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Live AI Spoken Response Subtitle Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, RoseSecondary.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "💬 ${activePersona.name}: \"$latestAiMsg\"",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    )
                }
            }

            // Interactive Voice Call Talking Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Quick Talk Chips
                val quickChips = if (isPremium) {
                    listOf("I love you ❤️", "Suno na baby... ✨", "Aapki yaad aa rahi thi 🥰", "Khana khaya baby?")
                } else {
                    listOf("Suno na jaan... ✨", "Kaise ho jaan? 😊", "Khana khaya na jaan? 🍲", "Aaj ka din kaisa raha?")
                }

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(quickChips) { chipText ->
                        CallQuickTalkChip(
                            text = chipText,
                            onClick = { viewModel.sendMessage(chipText) }
                        )
                    }
                }

                // Live Speech / Type Input on Voice Call
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = userTalkInput,
                        onValueChange = { userTalkInput = it },
                        placeholder = { Text("Speak / Type to ${activePersona.name}...", color = Color.Gray, fontSize = 13.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RosePrimary,
                            unfocusedBorderColor = Color.Gray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("call_talk_input")
                    )

                    IconButton(
                        onClick = {
                            if (userTalkInput.isNotBlank()) {
                                viewModel.sendMessage(userTalkInput)
                                userTalkInput = ""
                            }
                        },
                        modifier = Modifier
                            .size(46.dp)
                            .background(RosePrimary, CircleShape)
                            .testTag("call_send_talk_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Call Control Buttons (Mute, Speaker, End Call)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mute Button
                    Surface(
                        onClick = { isMuted = !isMuted },
                        shape = CircleShape,
                        color = if (isMuted) RosePrimary else Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.size(52.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                contentDescription = "Mute",
                                tint = Color.White
                            )
                        }
                    }

                    // End Call Red Button
                    Surface(
                        onClick = {
                            viewModel.endCall()
                            onEndCall()
                        },
                        shape = CircleShape,
                        color = HeartRed,
                        modifier = Modifier
                            .size(64.dp)
                            .testTag("end_call_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.CallEnd,
                                contentDescription = "End Call",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    // Speaker Button
                    Surface(
                        onClick = { },
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.size(52.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Speaker",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CallQuickTalkChip(text: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, RoseSecondary.copy(alpha = 0.5f))
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        )
    }
}
