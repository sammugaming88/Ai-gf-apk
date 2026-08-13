package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.HeartRed
import com.example.ui.theme.RosePrimary
import com.example.ui.theme.RoseSecondary
import com.example.ui.theme.SoftPink
import com.example.ui.theme.SparkleGold
import com.example.ui.viewmodel.CompanionViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumScreen(
    viewModel: CompanionViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val isPremium by viewModel.isPremium.collectAsState()
    val remainingTimeText by viewModel.premiumRemainingTime.collectAsState()
    val activePersona by viewModel.selectedPersona.collectAsState()
    val userPhone by viewModel.userPhone.collectAsState()
    val userEmail by viewModel.userEmail.collectAsState()

    var showPaymentConfirmDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val payeeName = "samir"
    val upiId = "8860291758@fam"
    
    // Selected plan state: "7_days" (₹10), "18_days" (₹20), "30_days" (₹30)
    var selectedPlan by remember { mutableStateOf("30_days") }

    val amount = when (selectedPlan) {
        "7_days" -> "10"
        "18_days" -> "20"
        else -> "30"
    }

    val planTitle = when (selectedPlan) {
        "7_days" -> "7 Days VIP Pass"
        "18_days" -> "18 Days VIP Pass"
        else -> "1 Month (30 Days) VIP Pass"
    }

    fun launchUpiPayment() {
        val upiUri = Uri.parse("upi://pay?pa=$upiId&pn=${Uri.encode(payeeName)}&am=$amount&cu=INR&tn=${Uri.encode("AI GF $planTitle")}")
        val intent = Intent(Intent.ACTION_VIEW, upiUri)
        try {
            val chooser = Intent.createChooser(intent, "Pay ₹$amount using UPI App")
            context.startActivity(chooser)
        } catch (e: Exception) {
            Toast.makeText(context, "No UPI app found. Please copy UPI ID: $upiId to pay.", Toast.LENGTH_LONG).show()
        }
    }

    fun copyUpiId() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("UPI ID", upiId)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "UPI ID ($upiId) copied to clipboard!", Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Samnoor VIP Pass 👑",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
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
            contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // VIP Pass Hero Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("vip_pass_hero_card"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFF38152D),
                                            RosePrimary,
                                            SparkleGold.copy(alpha = 0.8f)
                                        )
                                    )
                                )
                        )

                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = SparkleGold,
                                shadowElevation = 4.dp
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "VIP Star",
                                    tint = Color.Black,
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = if (isPremium) "AI GF VIP Pass Active 👑" else "Choose Your VIP Pass 👑",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = if (isPremium) "Unlimited Voice Chat, 18+ Mode & Unlimited Time Unlocked ✨" else "Unlock Voice Chat, 18+ Romantic Talks & No Time Limit!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = SoftPink,
                                textAlign = TextAlign.Center
                            )

                            if (isPremium) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = Color.Black.copy(alpha = 0.5f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, SparkleGold)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Schedule,
                                            contentDescription = "Timer",
                                            tint = SparkleGold,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = remainingTimeText,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = SparkleGold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Plan Selection Cards (7 Days - ₹10 | 18 Days - ₹20 | 1 Month - ₹30)
            if (!isPremium) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Select Premium Plan 💎",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            PlanSelectionCard(
                                title = "7 Days",
                                price = "₹10",
                                isSelected = selectedPlan == "7_days",
                                modifier = Modifier.weight(1f),
                                onClick = { selectedPlan = "7_days" }
                            )

                            PlanSelectionCard(
                                title = "18 Days",
                                price = "₹20",
                                isSelected = selectedPlan == "18_days",
                                modifier = Modifier.weight(1f),
                                onClick = { selectedPlan = "18_days" }
                            )

                            PlanSelectionCard(
                                title = "1 Month (30 Days)",
                                price = "₹30",
                                badge = "POPULAR",
                                isSelected = selectedPlan == "30_days",
                                modifier = Modifier.weight(1.2f),
                                onClick = { selectedPlan = "30_days" }
                            )
                        }
                    }
                }
            }

            // Free vs VIP Benefits Comparison Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "VIP Premium Unlocks 🔓❤️",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        VipFeatureRow(
                            icon = Icons.Default.Call,
                            text = "Voice Chat Unlocked 🎙️",
                            subtext = "Free: Voice Chat Locked 🔒"
                        )
                        VipFeatureRow(
                            icon = Icons.Default.Favorite,
                            text = "18+ Romantic & Spicy Talks 🔞",
                            subtext = "Free: 18+ Baatein Locked 🔒"
                        )
                        VipFeatureRow(
                            icon = Icons.Default.LockOpen,
                            text = "No Time Limit / Unlimited Chat ⏳♾️",
                            subtext = "Free: Max 30 mins talk per day ⏱️"
                        )
                        VipFeatureRow(
                            icon = Icons.Default.Verified,
                            text = "Ultra Fast AI & Deep Memory Recall",
                            subtext = "Remembers all details about you"
                        )
                    }
                }
            }

            if (isPremium) {
                // Active Subscription Status
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = EmeraldGreen.copy(alpha = 0.15f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(2.dp, EmeraldGreen)
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Active",
                                tint = EmeraldGreen,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "VIP Membership Active 👑",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Thank you! You have full unlimited access to Samnoor.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            } else {
                // UPI Payment Card & QR Code Details
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("upi_payment_card"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Payment,
                                        contentDescription = "Payment",
                                        tint = RosePrimary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Pay via UPI App (₹$amount)",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = RosePrimary.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = planTitle,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = RosePrimary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            // Stylized QR Box displaying user payment details
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFF1E1E28),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = payeeName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = upiId,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = SparkleGold
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // QR Visual Box
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color.White,
                                        modifier = Modifier.size(160.dp)
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.padding(12.dp)
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Icon(
                                                    imageVector = Icons.Default.QrCodeScanner,
                                                    contentDescription = "UPI QR Code",
                                                    tint = Color.Black,
                                                    modifier = Modifier.size(80.dp)
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = "Scan to Pay ₹$amount",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.Black
                                                )
                                                Text(
                                                    text = "GPay / PhonePe / Paytm",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontSize = 9.sp,
                                                    color = Color.Gray
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier
                                            .clickable { copyUpiId() }
                                            .padding(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy",
                                            tint = SoftPink,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Tap to Copy UPI ID: $upiId",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = SoftPink,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }

                            // Direct UPI Payment Button
                            Button(
                                onClick = {
                                    val planDays = when (selectedPlan) {
                                        "7_days" -> 7
                                        "18_days" -> 18
                                        else -> 30
                                    }
                                    showPaymentConfirmDialog = true
                                    launchUpiPayment()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = RosePrimary),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("launch_upi_app_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LockOpen,
                                    contentDescription = "Pay",
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Pay ₹$amount via Any UPI App 💸", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Button(
                                onClick = {
                                    val planDays = when (selectedPlan) {
                                        "7_days" -> 7
                                        "18_days" -> 18
                                        else -> 30
                                    }
                                    viewModel.activatePremium(planDays)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("🎉 Payment Received! VIP Premium Unlocked! 👑")
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("confirm_payment_received_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Confirm",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("I Have Completed Payment (Unlock VIP) 👑", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // Payment Receive Confirmation Dialog
    if (showPaymentConfirmDialog) {
        val planDays = when (selectedPlan) {
            "7_days" -> 7
            "18_days" -> 18
            else -> 30
        }
        AlertDialog(
            onDismissRequest = { showPaymentConfirmDialog = false },
            title = {
                Text("Confirm Payment Receive 👑💸", fontWeight = FontWeight.Bold, color = SparkleGold)
            },
            text = {
                Column {
                    Text("Have you completed the payment of ₹$amount via UPI?")
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "Once confirmed, VIP Premium Access ($planDays Days) will be instantly unlocked on your account!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.activatePremium(planDays)
                        scope.launch {
                            snackbarHostState.showSnackbar("🎉 Payment Received! VIP Premium Unlocked for $planDays Days! 👑")
                        }
                        showPaymentConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                ) {
                    Text("Yes, Unlock Premium VIP 🔓", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPaymentConfirmDialog = false }) {
                    Text("Not Yet")
                }
            }
        )
    }
}

@Composable
fun PlanSelectionCard(
    title: String,
    price: String,
    badge: String? = null,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) RosePrimary else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) RosePrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (badge != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SparkleGold
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = price,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) RosePrimary else SparkleGold
            )
        }
    }
}

@Composable
fun VipFeatureRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    subtext: String? = null
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(RosePrimary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = RosePrimary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subtext != null) {
                Text(
                    text = subtext,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
