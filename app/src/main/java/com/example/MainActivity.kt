package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.ui.screens.CallScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.GiftsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LanguageSelectionScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MemoriesScreen
import com.example.ui.screens.MoodTrackerScreen
import com.example.ui.screens.PremiumScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.theme.AriaTheme
import com.example.ui.theme.RosePrimary
import com.example.ui.viewmodel.CompanionViewModel
import androidx.compose.material.icons.filled.Language

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Login : Screen("login", "Login", Icons.Default.Lock)
    object Language : Screen("language", "Language", Icons.Default.Language)
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Chat : Screen("chat", "Chat", Icons.Default.ChatBubble)
    object Call : Screen("call", "Call", Icons.Default.Call)
    object Gifts : Screen("gifts", "Gifts", Icons.Default.CardGiftcard)
    object Memories : Screen("memories", "Memories", Icons.Default.Person)
    object Mood : Screen("mood", "Mood", Icons.Default.Person)
    object Premium : Screen("premium", "VIP", Icons.Default.Star)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Chat,
    Screen.Call,
    Screen.Gifts,
    Screen.Profile
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AriaTheme {
                MainAppScreen()
            }
        }
    }
}

@Composable
fun MainAppScreen() {
    val navController = rememberNavController()
    val viewModel: CompanionViewModel = viewModel()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    // Redirect to login if not logged in
    androidx.compose.runtime.LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    // Hide bottom navigation bar during full-screen voice call, login screen, or language selection screen
    val showBottomBar = isLoggedIn && currentRoute != Screen.Call.route && currentRoute != Screen.Login.route && currentRoute != Screen.Language.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    bottomNavItems.forEach { screen ->
                        val isSelected = currentRoute == screen.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (screen == Screen.Call) {
                                    viewModel.startCall()
                                }
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = screen.title,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = RosePrimary,
                                selectedTextColor = RosePrimary,
                                indicatorColor = RosePrimary.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.testTag("nav_${screen.route}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (isLoggedIn) Screen.Home.route else Screen.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = {
                        navController.navigate(Screen.Language.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Language.route) {
                LanguageSelectionScreen(
                    viewModel = viewModel,
                    onLanguageSelected = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Language.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToChat = { navController.navigate(Screen.Chat.route) },
                    onNavigateToCall = {
                        viewModel.startCall()
                        navController.navigate(Screen.Call.route)
                    },
                    onNavigateToGifts = { navController.navigate(Screen.Gifts.route) },
                    onNavigateToMemories = { navController.navigate(Screen.Memories.route) },
                    onNavigateToMood = { navController.navigate(Screen.Mood.route) },
                    onNavigateToPremium = { navController.navigate(Screen.Premium.route) },
                    onNavigateToLanguage = { navController.navigate(Screen.Language.route) }
                )
            }

            composable(Screen.Chat.route) {
                ChatScreen(
                    viewModel = viewModel,
                    onNavigateToCall = {
                        viewModel.startCall()
                        navController.navigate(Screen.Call.route)
                    },
                    onNavigateToGifts = { navController.navigate(Screen.Gifts.route) }
                )
            }

            composable(Screen.Call.route) {
                CallScreen(
                    viewModel = viewModel,
                    onEndCall = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.Gifts.route) {
                GiftsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Memories.route) {
                MemoriesScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Mood.route) {
                MoodTrackerScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Premium.route) {
                PremiumScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    viewModel = viewModel,
                    onNavigateToPremium = { navController.navigate(Screen.Premium.route) },
                    onNavigateToLanguage = { navController.navigate(Screen.Language.route) }
                )
            }
        }
    }
}
