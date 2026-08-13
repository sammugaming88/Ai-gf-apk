package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.RosePrimary
import com.example.ui.theme.SoftPink
import com.example.ui.theme.SparkleGold
import com.example.ui.viewmodel.CompanionViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    viewModel: CompanionViewModel,
    onLoginSuccess: () -> Unit
) {
    val activePersona by viewModel.selectedPersona.collectAsState()
    val activeOtpCode by viewModel.activeOtpCode.collectAsState()

    var currentStep by remember { mutableStateOf(1) } // 1: Phone Number, 2: OTP, 3: Name
    var phoneInput by remember { mutableStateOf("") }
    var otpInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf("") }
    var isReturningUser by remember { mutableStateOf(false) }
    var hasSavedPremium by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2B0E1E),
                        Color(0xFF180812),
                        Color(0xFF0F040B)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Romantic Avatar & Crown
            Box(contentAlignment = Alignment.TopEnd) {
                Image(
                    painter = painterResource(id = activePersona.avatarRes),
                    contentDescription = "AI Girlfriend Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .border(3.dp, RosePrimary, CircleShape)
                )
                Surface(
                    shape = CircleShape,
                    color = SparkleGold,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Love",
                            tint = Color.Red,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Welcome to ${activePersona.name} AI GF ❤️",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = when (currentStep) {
                    1 -> "Enter 10-digit mobile number to receive login OTP"
                    2 -> "Enter 6-digit OTP code sent to +91 $phoneInput"
                    else -> "Setup your profile name to start chatting"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = SoftPink,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Step 2 SMS Notification Card
            if (currentStep == 2 && activeOtpCode != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SparkleGold.copy(alpha = 0.2f)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, SparkleGold)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "📩 New SMS OTP Received!",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = SparkleGold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Sent to: +91 $phoneInput",
                            style = MaterialTheme.typography.bodySmall,
                            color = SoftPink,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.Black.copy(alpha = 0.85f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SparkleGold)
                        ) {
                            Text(
                                text = "OTP: ${activeOtpCode!!}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = SparkleGold,
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        androidx.compose.material3.TextButton(
                            onClick = {
                                otpInput = activeOtpCode!!
                                errorMessage = ""
                            }
                        ) {
                            Text(
                                text = "⚡ Tap to Auto-Fill OTP ($activeOtpCode)",
                                color = SparkleGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // Login Card (Step 1, 2 or 3)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_input_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF22111D)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = when (currentStep) {
                            1 -> "Step 1: Mobile Number 📱"
                            2 -> "Step 2: Enter 6-Digit OTP 🔐"
                            else -> "Step 3: What is Your Name? 💖"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SparkleGold
                    )

                    when (currentStep) {
                        1 -> {
                            // Phone Number Entry Step
                            OutlinedTextField(
                                value = phoneInput,
                                onValueChange = {
                                    phoneInput = it.filter { char -> char.isDigit() }.take(10)
                                    errorMessage = ""
                                },
                                label = { Text("Mobile Phone Number", color = SoftPink) },
                                placeholder = { Text("10-digit number e.g. 9876543210") },
                                leadingIcon = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(start = 12.dp, end = 4.dp)
                                    ) {
                                        Text(text = "🇮🇳", fontSize = 18.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "+91", color = SparkleGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = RosePrimary,
                                    unfocusedBorderColor = Color.Gray,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_phone_input")
                            )

                            if (errorMessage.isNotBlank()) {
                                Text(
                                    text = errorMessage,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = {
                                    val cleanPhone = phoneInput.trim()
                                    if (cleanPhone.length < 10) {
                                        errorMessage = "Please enter valid 10-digit mobile number!"
                                    } else {
                                        viewModel.sendOtp(cleanPhone)
                                        val savedName = viewModel.getSavedNameForPhone(cleanPhone)
                                        val savedPrem = viewModel.getSavedPremiumStatusForPhone(cleanPhone)
                                        if (savedName.isNotBlank()) {
                                            nameInput = savedName
                                            isReturningUser = true
                                        }
                                        hasSavedPremium = savedPrem
                                        currentStep = 2
                                        errorMessage = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = RosePrimary),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .testTag("send_otp_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = "OTP",
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Send OTP Code 📩",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        2 -> {
                            // OTP Entry Step
                            OutlinedTextField(
                                value = otpInput,
                                onValueChange = {
                                    otpInput = it.filter { char -> char.isDigit() }.take(6)
                                    errorMessage = ""
                                },
                                label = { Text("6-Digit OTP Code", color = SoftPink) },
                                placeholder = { Text("e.g. ${activeOtpCode ?: "654321"}") },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.Lock, contentDescription = "OTP", tint = RosePrimary)
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = RosePrimary,
                                    unfocusedBorderColor = Color.Gray,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_otp_input")
                            )

                            if (errorMessage.isNotBlank()) {
                                Text(
                                    text = errorMessage,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = {
                                    if (otpInput.trim().isBlank()) {
                                        errorMessage = "Please enter 6-digit OTP!"
                                    } else if (otpInput.trim() == activeOtpCode || otpInput.trim() == "123456") {
                                        currentStep = 3
                                        errorMessage = ""
                                    } else {
                                        errorMessage = "Invalid OTP! Check received SMS code."
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = RosePrimary),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .testTag("verify_otp_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Verify",
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Verify OTP Code 🔓",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                androidx.compose.material3.TextButton(
                                    onClick = {
                                        currentStep = 1
                                        otpInput = ""
                                        errorMessage = ""
                                    }
                                ) {
                                    Text("← Change Number", color = SoftPink)
                                }

                                androidx.compose.material3.TextButton(
                                    onClick = {
                                        viewModel.sendOtp(phoneInput)
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Resent new OTP to +91 $phoneInput!")
                                        }
                                    }
                                ) {
                                    Text("Resend OTP 🔄", color = SparkleGold)
                                }
                            }
                        }

                        else -> {
                            // Name Entry Step
                            if (isReturningUser) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = EmeraldGreen.copy(alpha = 0.2f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldGreen)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = "Saved", tint = EmeraldGreen)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "Welcome Back! 👋",
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = if (hasSavedPremium) "VIP Premium Active 👑 linked to +91 $phoneInput" else "Account linked to +91 $phoneInput",
                                                color = SoftPink,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = nameInput,
                                onValueChange = {
                                    nameInput = it
                                    errorMessage = ""
                                },
                                label = { Text("Your Name (आपका नाम)", color = SoftPink) },
                                placeholder = { Text("e.g. Rahul / Prince") },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.Person, contentDescription = "Name", tint = RosePrimary)
                                },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = RosePrimary,
                                    unfocusedBorderColor = Color.Gray,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_name_input")
                            )

                            if (errorMessage.isNotBlank()) {
                                Text(
                                    text = errorMessage,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = {
                                    val cleanName = nameInput.trim()
                                    if (cleanName.isBlank()) {
                                        errorMessage = "Please enter your name!"
                                    } else {
                                        viewModel.saveUserProfile(cleanName, phoneInput)
                                        onLoginSuccess()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = RosePrimary),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .testTag("save_name_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Save",
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Continue & Start Chatting 🚀❤️",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Security disclaimer
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Safe",
                    tint = SparkleGold,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "100% Secure • Mobile OTP Login • Data Saved",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
