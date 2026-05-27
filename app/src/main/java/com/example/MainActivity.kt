package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.AqualynBottomNavBar
import com.example.ui.components.AquaticToastNotification
import com.example.ui.navigation.MainTabs
import com.example.ui.navigation.Routes
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AqualynApp()
            }
        }
    }
}

@Composable
fun AqualynApp() {
    val navController = rememberNavController()
    val context = androidx.compose.ui.platform.LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences("aqualyn_prefs", android.content.Context.MODE_PRIVATE)
    }
    val isOnboardingComplete = remember {
        mutableStateOf(sharedPreferences.getBoolean("onboarding_complete", false))
    }
    val startDest = if (isOnboardingComplete.value) Routes.MAIN else Routes.WELCOME

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(navController = navController, startDestination = startDest) {
            composable(Routes.WELCOME) {
                WelcomeScreen(
                    onLoginSuccess = {
                        navController.navigate(Routes.JOIN)
                    }
                )
            }
            composable(Routes.JOIN) {
                JoinScreen(
                    onContinue = { phone ->
                        navController.navigate("${Routes.VERIFY}/$phone")
                    }
                )
            }
            composable("${Routes.VERIFY}/{phone}") { backStackEntry ->
                val phone = backStackEntry.arguments?.getString("phone") ?: ""
                VerificationScreen(
                    phone = phone,
                    onVerified = {
                        navController.navigate(Routes.COMPLETE_PROFILE) {
                            popUpTo(Routes.JOIN) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.COMPLETE_PROFILE) {
                CompleteProfileScreen(
                    onComplete = {
                        sharedPreferences.edit().putBoolean("onboarding_complete", true).apply()
                        isOnboardingComplete.value = true
                        navController.navigate(Routes.MAIN) {
                            popUpTo(Routes.WELCOME) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.MAIN) {
                MainLayoutScreen(
                    onChatClick = { chatId ->
                        navController.navigate("${Routes.CHAT_DETAIL}/$chatId")
                    },
                    onUserClick = { userId ->
                        navController.navigate("${Routes.FRIEND_PROFILE}/$userId")
                    },
                    onNewChatClick = {
                        navController.navigate(Routes.NEW_CHAT)
                    },
                    onLogout = {
                        sharedPreferences.edit().putBoolean("onboarding_complete", false).apply()
                        isOnboardingComplete.value = false
                        navController.navigate(Routes.WELCOME) {
                            popUpTo(Routes.MAIN) { inclusive = true }
                        }
                    }
                )
            }
            composable("${Routes.CHAT_DETAIL}/{chatId}") { backStackEntry ->
                val chatId = backStackEntry.arguments?.getString("chatId") ?: "1"
                ChatDetailScreen(
                    userId = chatId,
                    onBack = { navController.popBackStack() },
                    onProfileClick = { friendId ->
                        navController.navigate("${Routes.FRIEND_PROFILE}/$friendId")
                    }
                )
            }
            composable("${Routes.FRIEND_PROFILE}/{friendId}") { backStackEntry ->
                val friendId = backStackEntry.arguments?.getString("friendId") ?: "1"
                FriendProfileScreen(
                    friendId = friendId,
                    onBack = { navController.popBackStack() },
                    onStartSecretChat = {
                        navController.navigate("${Routes.CHAT_DETAIL}/c2")
                    }
                )
            }
            composable(Routes.NEW_CHAT) {
                NewChatScreen(
                    onBack = { navController.popBackStack() },
                    onChatCreated = {
                        navController.popBackStack()
                    }
                )
            }
        }

        // Aqua Niche Notification Banner
        AquaticToastNotification()
    }
}

@Composable
fun MainLayoutScreen(
    onChatClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onNewChatClick: () -> Unit,
    onLogout: () -> Unit
) {
    var currentTab by remember { mutableStateOf(MainTabs.CHATS) }
    var openPostCreatorTrigger by remember { mutableStateOf(false) }
    var showEditProfileInProfileTab by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            AqualynBottomNavBar(
                currentTab = currentTab,
                onTabSelected = { currentTab = it }
            )
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
        ) {
            when (currentTab) {
                MainTabs.CHATS -> ChatsScreen(
                    onChatClick = onChatClick,
                    onNewChatClick = onNewChatClick,
                    onProfileClick = { currentTab = MainTabs.PROFILE },
                    onFriendProfileClick = onUserClick
                )
                MainTabs.CONTACTS -> ConnectionsScreen(onUserClick = onUserClick)
                MainTabs.FEED -> FeedScreen(
                    onChatRedirect = { currentTab = MainTabs.CHATS },
                    initiallyOpenPostCreator = openPostCreatorTrigger,
                    onPostCreatorDismissed = { openPostCreatorTrigger = false }
                )
                MainTabs.SETTINGS -> SettingsScreen(onLogout = onLogout)
                MainTabs.PROFILE -> {
                    if (showEditProfileInProfileTab) {
                        EditProfileScreen(
                            onBack = { showEditProfileInProfileTab = false },
                            onSave = { showEditProfileInProfileTab = false }
                        )
                    } else {
                        ProfileScreen(
                            onBack = { currentTab = MainTabs.CHATS },
                            onEditProfile = { showEditProfileInProfileTab = true },
                            onAddPost = {
                                openPostCreatorTrigger = true
                                currentTab = MainTabs.FEED
                            }
                        )
                    }
                }
            }
        }
    }
}
