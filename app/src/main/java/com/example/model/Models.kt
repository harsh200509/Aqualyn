package com.example.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateListOf

object GlobalState {
    var searchByNumberEnabled by mutableStateOf(true)
    
    // Custom drop-theme toast notification state
    var activeToastMessage by mutableStateOf<String?>(null)
    var activeToastIsGreen by mutableStateOf(true) // Green for success, Blue for Aqualyn alerts, Red for issues
    var activeToastIconRes by mutableStateOf<androidx.compose.ui.graphics.vector.ImageVector?>(null)

    // The main shared list of chats for interactive functionality
    val chats = mutableStateListOf<ChatItem>(
        ChatItem("c1", null, isGroup = true, groupName = "Design Team", lastMessage = "Here is the latest prototype.", timeInfo = "10:24 AM", unreadCount = 3, isPinned = true, isVoiceMessage = false),
        ChatItem("c2", null, isGroup = false, groupName = "Secret Project", lastMessage = "Don't tell anyone...", timeInfo = "Yesterday", unreadCount = 0, isPinned = true, isVoiceMessage = false),
        ChatItem("c3", null, isGroup = false, groupName = "Harsh", lastMessage = "Check out this place", timeInfo = "11:22 AM", unreadCount = 0, isPinned = false, isVoiceMessage = false),
        ChatItem("c4", null, isGroup = false, groupName = "Raj", lastMessage = "Raj suar", timeInfo = "07:21 AM", unreadCount = 0, isPinned = false, isVoiceMessage = false),
        ChatItem("c5", null, isGroup = false, groupName = "Mini Militia", lastMessage = "Hello", timeInfo = "05:31 PM", unreadCount = 0, isPinned = false, isVoiceMessage = true)
    )

    // Dynamic folders list
    val folders = mutableStateListOf<String>("Work", "Personal @Home", "Secret")

    // Dynamic posts & stories list
    val posts = mutableStateListOf<PostItem>(
        PostItem("p1", "Aqua splash on sunrise beach", "Enjoying the fresh clear morning ripple vibes on Aqualyn application! Clean and fast.", 45, "2 hours ago"),
        PostItem("p2", "Pure flowing mineral spring", "Focusing on dynamic interactions and fluid design patterns.", 82, "1 day ago")
    )
    
    val savedPostIds = mutableStateListOf<String>()

    val stories = mutableStateListOf<StoryItem>(
        StoryItem("s1", "Sea breeze", true),
        StoryItem("s2", "Lagoon dip", false),
        StoryItem("s3", "Coral reef", false)
    )

    // PIN lock for archives
    var archivePinRequired by mutableStateOf(false)
    var archivePinCode by mutableStateOf("") // Default empty (unset)
    var archiveLockedState by mutableStateOf(true)

    // Helper to show custom aquatic toast
    fun showToast(message: String, isGreen: Boolean = true) {
        activeToastMessage = message
        activeToastIsGreen = isGreen
    }

    // Tracks if a chat is archived, muted, or selected
    val archivedChats = mutableStateMapOf<String, Boolean>()
    val mutedChats = mutableStateMapOf<String, Boolean>()
    val folderChatMap = mutableStateMapOf<String, String>() // chatId to folderName

    // Track following status for users
    val followedUsers = mutableStateMapOf<String, Boolean>().apply {
        put("ansh_7676", false)
        put("raj_1034", true)
    }
    
    // Track blocked status
    val blockedUsers = mutableStateMapOf<String, Boolean>()
    
    // Track reported status
    val reportedUsers = mutableStateMapOf<String, Boolean>()
}

data class PostItem(
    val id: String,
    val imageDescription: String,
    val caption: String,
    var likesCount: Int,
    val timeAgo: String,
    val isCommentsDisabled: Boolean = false,
    val location: String = ""
)

data class StoryItem(
    val id: String,
    val title: String,
    val isViewed: Boolean = false,
    val isMe: Boolean = false
)

data class User(
    val id: String,
    val name: String,
    val handle: String = "",
    val role: String = "",
    val description: String = "",
    val avatarUrl: String,
    val isOnline: Boolean = false,
    val followers: Int = 0,
    val following: Int = 0
)

data class Message(
    val id: String,
    val senderId: String,
    val content: String,
    val timeInfo: String,
    val isMine: Boolean,
    val imageUrl: String? = null,
    val videoUrl: String? = null,
    val audioUrl: String? = null,
    val documentName: String? = null,
    val location: String? = null,
    val paymentAmount: String? = null,
    val replyToMsg: Message? = null,
    val reactions: List<String> = emptyList(),
    val isPinned: Boolean = false,
    val isEdited: Boolean = false
)

data class ChatItem(
    val id: String,
    val user: User? = null,
    val isGroup: Boolean = false,
    val groupName: String? = null,
    val lastMessage: String,
    val timeInfo: String,
    val unreadCount: Int = 0,
    val isPinned: Boolean = false,
    val isVoiceMessage: Boolean = false
)

data class Story(
    val id: String,
    val title: String,
    val imageUrl: String,
    val isAdd: Boolean = false
)
