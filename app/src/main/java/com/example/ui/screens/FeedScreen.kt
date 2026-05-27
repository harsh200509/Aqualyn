package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Backwards-compatible core models with default values to prevent breaking ConnectionsScreen
data class LocalPost(
    val id: String,
    val username: String,
    val caption: String,
    val imageGradient: List<Color>,
    var isLiked: Boolean = false,
    var likeCount: Int = 12,
    var isSaved: Boolean = false,
    val timeInfo: String = "Just now",
    val imageUrl: String? = null,
    val avatarUrl: String? = null,
    val comments: List<Pair<String, String>> = emptyList(), // (username, commentText)
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val location: String? = null
)

data class LocalStory(
    val id: String,
    val username: String,
    val gradient: List<Color>,
    val text: String = "Vibe check! ✨",
    val isMine: Boolean = false,
    val avatarUrl: String? = null,
    val storyImageUrl: String? = null,
    val stickers: List<LocalSticker> = emptyList(),
    val isCloseFriends: Boolean = false
)

data class LocalSticker(
    val id: String,
    val type: String, // "location", "hashtag", "mention", "emoji"
    val content: String,
    val x: Float = 0.5f,
    val y: Float = 0.4f
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    onChatRedirect: () -> Unit = {},
    initiallyOpenPostCreator: Boolean = false,
    onPostCreatorDismissed: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()

    // Interactive Toast feedback
    var toastMessage by remember { mutableStateOf<String?>(null) }
    var showToast by remember { mutableStateOf(false) }

    fun triggerToast(msg: String) {
        toastMessage = msg
        showToast = true
        coroutineScope.launch {
            delay(2000)
            showToast = false
        }
    }

    // Default high-quality real images mimicking photo uploads
    val defaultAvatarYourStr = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&q=80&w=200"
    val defaultAvatarRaj = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?auto=format&fit=crop&q=80&w=200"
    val defaultAvatarAnsh = "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&q=80&w=200"
    val defaultAvatarBhavya = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&q=80&w=200"

    // Initial Stories
    val storiesList = remember {
        mutableStateListOf(
            LocalStory(
                id = "s-mine",
                username = "Your story",
                gradient = listOf(Color(0xFF0091EA), Color(0xFF00E5FF)),
                text = "Developing Android with Kotlin! 🚀📱",
                isMine = true,
                avatarUrl = defaultAvatarYourStr,
                storyImageUrl = "https://images.unsplash.com/photo-1607525428034-b723cf961d3e?auto=format&fit=crop&q=80&w=600"
            ),
            LocalStory(
                id = "s-2",
                username = "Raj",
                gradient = listOf(Color(0xFFFF5252), Color(0xFFFF7A00)),
                text = "Building Aqualyn glassmorphism! ✨⚡️",
                avatarUrl = defaultAvatarRaj,
                storyImageUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&q=80&w=600"
            ),
            LocalStory(
                id = "s-3",
                username = "ansh_7676",
                gradient = listOf(Color(0xFF7C4DFF), Color(0xFFE040FB)),
                text = "Monsoon vibes 🌧️☕",
                avatarUrl = defaultAvatarAnsh,
                storyImageUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&q=80&w=600"
            ),
            LocalStory(
                id = "s-4",
                username = "bhavya_xx",
                gradient = listOf(Color(0xFF69F0AE), Color(0xFF00E676)),
                text = "Weekend vibes! 🌴☀️",
                avatarUrl = defaultAvatarBhavya,
                storyImageUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=600"
            )
        )
    }

    // Initial Posts (Using real Unsplash image load options)
    val postsList = remember {
        mutableStateListOf(
            LocalPost(
                id = "p-1",
                username = "harsh_7742",
                caption = "Building the smoothest glassy frames with Aqualyn ⚡️📱",
                imageGradient = listOf(Color(0xFF0091EA), Color(0xFF637BFE)),
                isLiked = true,
                likeCount = 31,
                imageUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&q=80&w=600",
                avatarUrl = defaultAvatarYourStr,
                location = "San Francisco, CA",
                comments = listOf(
                    "raj_1034" to "This runs incredibly smooth!",
                    "bhavya_xx" to "Stunning design theme and glassy aesthetics 💎✨"
                )
            ),
            LocalPost(
                id = "p-2",
                username = "ansh_7676",
                caption = "Vibes are absolutely gorgeous today! 💎🌈",
                imageGradient = listOf(Color(0xFF2193b0), Color(0xFF6dd5ed)),
                isLiked = false,
                likeCount = 14,
                imageUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&q=80&w=600",
                avatarUrl = defaultAvatarAnsh,
                location = "Goa, India",
                comments = listOf(
                    "harsh_7742" to "That sunset is unbelievable."
                )
            ),
            LocalPost(
                id = "p-3",
                username = "bhavya_xx",
                caption = "Evening tea sessions during monsoon.",
                imageGradient = listOf(Color(0xFFFF5E62), Color(0xFFFF9966)),
                isLiked = true,
                likeCount = 42,
                imageUrl = "https://images.unsplash.com/photo-1576092768241-dec231879fc3?auto=format&fit=crop&q=80&w=600",
                avatarUrl = defaultAvatarBhavya,
                location = "Mumbai",
                comments = listOf(
                    "raj_1034" to "Send a cup of hot ginger tea please! 🍵"
                )
            )
        )
    }

    // Modal view states
    var activeStoryIndex by remember { mutableStateOf<Int?>(null) }
    var activePostForDetail by remember { mutableStateOf<LocalPost?>(null) }
    var showStoryCreator by remember { mutableStateOf(false) }
    var showPostCreator by remember { mutableStateOf(false) }

    LaunchedEffect(initiallyOpenPostCreator) {
        if (initiallyOpenPostCreator) {
            showPostCreator = true
            onPostCreatorDismissed()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFFD6F5F6), Color(0xFFE9E5F8)),
                    start = Offset.Zero,
                    end = Offset.Infinite
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header corresponding perfectly to screen #2 screenshot
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                    .background(Color.White.copy(alpha = 0.85f))
                    .border(
                        1.dp,
                        Color.White.copy(alpha = 0.6f),
                        RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .padding(top = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Aqualyn",
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        color = Color(0xFF0091EA),
                        letterSpacing = (-0.5).sp
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { triggerToast("No new notifications") },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0091EA).copy(alpha = 0.1f))
                        ) {
                            Icon(Icons.Outlined.FavoriteBorder, contentDescription = "Activity", tint = Color(0xFF0091EA), modifier = Modifier.size(22.dp))
                        }
                        IconButton(
                            onClick = onChatRedirect,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0091EA).copy(alpha = 0.1f))
                        ) {
                            Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = "Chats", tint = Color(0xFF0091EA), modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 96.dp, top = 8.dp)
            ) {
                // Stories Horizontal Row Slider
                item {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // "Add Story" direct placeholder
                        item {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable { showStoryCreator = true }
                                    .padding(end = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(76.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.7f))
                                        .border(2.dp, Color.White.copy(alpha = 0.8f), CircleShape)
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .background(
                                                Brush.linearGradient(
                                                    listOf(Color(0xFF637BFE), Color(0xFF0091EA))
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Filled.Add,
                                            contentDescription = "Publish Story",
                                            tint = Color.White,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    "Add Story",
                                    fontSize = 11.sp,
                                    color = Color(0xFF546E7A),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Users Stories loop
                        itemsIndexed(storiesList) { index, story ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable { activeStoryIndex = index }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(76.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.7f))
                                        .border(
                                            width = 2.dp,
                                            brush = Brush.linearGradient(
                                                if (story.isCloseFriends) {
                                                    listOf(Color(0xFF4CAF50), Color(0xFF8BC34A))
                                                } else {
                                                    story.gradient
                                                }
                                            ),
                                            shape = CircleShape
                                        )
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (!story.avatarUrl.isNullOrEmpty()) {
                                        AsyncImage(
                                            model = story.avatarUrl,
                                            contentDescription = story.username,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(CircleShape)
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(CircleShape)
                                                .background(Brush.linearGradient(story.gradient)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = story.username.take(2).uppercase(),
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                fontSize = 16.sp
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = story.username,
                                    fontSize = 11.sp,
                                    color = Color(0xFF263238),
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.width(68.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // Posts Cards View
                itemsIndexed(postsList) { index, post ->
                    LocalPostCard(
                        post = post,
                        onLikeChange = { updatedPost ->
                            postsList[index] = updatedPost
                        },
                        onPostClick = {
                            activePostForDetail = post
                        }
                    )
                }
            }
        }

        // Custom animated feedback Toast
        AnimatedVisibility(
            visible = showToast,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -40 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { -40 }),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 90.dp, start = 20.dp, end = 20.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF263238)),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Check, contentDescription = null, tint = Color(0xFF00FF88))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(toastMessage ?: "", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }

    // --- DIALOGS FOR PHOTO CREATION and STORY CREATION/VIEWING ---

    // STORY VIEWER (Mimicking the React StoryViewer)
    activeStoryIndex?.let { index ->
        val currentStory = storiesList[index]
        Dialog(
            onDismissRequest = { activeStoryIndex = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            var progress by remember { mutableStateOf(0f) }
            var isPaused by remember { mutableStateOf(false) }
            var replyMsg by remember { mutableStateOf("") }

            LaunchedEffect(index, isPaused) {
                if (!isPaused) {
                    val duration = 4000
                    val interval = 50
                    val totalSteps = duration / interval
                    while (progress < 1.0f) {
                        delay(interval.toLong())
                        if (!isPaused) {
                            progress += 1.0f / totalSteps
                        }
                    }
                    // Auto-advance
                    if (index < storiesList.size - 1) {
                        activeStoryIndex = index + 1
                        progress = 0f
                    } else {
                        activeStoryIndex = null
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                // Background image or gradient fallback
                if (!currentStory.storyImageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = currentStory.storyImageUrl,
                        contentDescription = "Story Media",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.linearGradient(currentStory.gradient))
                    )
                }

                // Overlay scrim
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent, Color.Black.copy(alpha = 0.7f))
                            )
                        )
                )

                // Layout Contents
                Column(modifier = Modifier.fillMaxSize()) {
                    // Progress Indicators at top
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp, start = 12.dp, end = 12.dp)
                            .height(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        for (i in storiesList.indices) {
                            val activeFill = when {
                                i < index -> 1.0f
                                i == index -> progress
                                else -> 0.0f
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(Color.White.copy(alpha = 0.3f))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(activeFill)
                                        .background(Color.White)
                                )
                            }
                        }
                    }

                    // Story owner details row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!currentStory.avatarUrl.isNullOrEmpty()) {
                            AsyncImage(
                                model = currentStory.avatarUrl,
                                contentDescription = "",
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(currentStory.username.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(currentStory.username, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                if (currentStory.isCloseFriends) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFF4CAF50))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text("CLOSE FRIENDS", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Black)
                                    }
                                }
                            }
                            Text("Just now", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        
                        // Close item
                        IconButton(onClick = { activeStoryIndex = null }) {
                            Icon(Icons.Filled.Close, contentDescription = "Close Story", tint = Color.White)
                        }
                    }

                    // Content details (with stickers & caption text)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        isPaused = true
                                        tryAwaitRelease()
                                        isPaused = false
                                    },
                                    onTap = { offset ->
                                        val width = size.width
                                        if (offset.x < width * 0.3f) {
                                            // Tap Left: Go back
                                            if (index > 0) {
                                                activeStoryIndex = index - 1
                                                progress = 0f
                                            }
                                        } else {
                                            // Tap Right: Go next
                                            if (index < storiesList.size - 1) {
                                                activeStoryIndex = index + 1
                                                progress = 0f
                                            } else {
                                                activeStoryIndex = null
                                            }
                                        }
                                    }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        // Main Text Caption Overlay styled gracefully
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.Black.copy(alpha = 0.5f))
                                .padding(horizontal = 20.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = currentStory.text,
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 21.sp,
                                textAlign = TextAlign.Center
                            )
                        }

                        // Sticker renders (Mimicking React custom interactive sticker placements!)
                        currentStory.stickers.forEach { sticker ->
                            Box(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .offset(y = (-40).dp)
                            ) {
                                when (sticker.type) {
                                    "mention" -> {
                                        Row(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Color.White)
                                                .padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Filled.AlternateEmail, contentDescription = null, tint = Color(0xFFE91E63), modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(sticker.content, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        }
                                    }
                                    "hashtag" -> {
                                        Row(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Brush.horizontalGradient(listOf(Color(0xFF9C27B0), Color(0xFFE91E63))))
                                                .padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Filled.Tag, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(sticker.content, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        }
                                    }
                                    "location" -> {
                                        Row(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Color(0xFF2196F3))
                                                .padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Filled.Place, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(sticker.content, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        }
                                    }
                                    "emoji" -> {
                                        Text(sticker.content, fontSize = 54.sp)
                                    }
                                }
                            }
                        }
                    }

                    // Bottom reply and quick emojis (matching React StoryViewer bottom UI)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Quick click-to-react emojis
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            val reactionEmojis = listOf("🔥", "😂", "😮", "😢", "😍", "👏")
                            reactionEmojis.forEach { emoji ->
                                Text(
                                    text = emoji,
                                    fontSize = 28.sp,
                                    modifier = Modifier
                                        .clickable {
                                            triggerToast("Sent reaction: $emoji")
                                            activeStoryIndex = null
                                        }
                                )
                            }
                        }

                        // Text Field input
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = replyMsg,
                                onValueChange = { replyMsg = it },
                                placeholder = { Text("Send reply to ${currentStory.username}...", color = Color.White.copy(alpha = 0.6f)) },
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(28.dp)),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White.copy(alpha = 0.15f),
                                    unfocusedContainerColor = Color.White.copy(alpha = 0.15f),
                                    focusedBorderColor = Color.White.copy(alpha = 0.4f),
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            IconButton(
                                onClick = {
                                    if (replyMsg.isNotBlank()) {
                                        triggerToast("Reply sent successfully!")
                                        replyMsg = ""
                                        activeStoryIndex = null
                                    }
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0091EA))
                            ) {
                                Icon(Icons.Filled.Send, contentDescription = "Send", tint = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }

    // STORY CREATION DIALOG (Mimicking React StoryCreator)
    if (showStoryCreator) {
        Dialog(
            onDismissRequest = { showStoryCreator = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            var newStoryCaption by remember { mutableStateOf("") }
            val grOptions = listOf(
                listOf(Color(0xFF0091EA), Color(0xFF00E5FF)),
                listOf(Color(0xFFFF5252), Color(0xFFFF7A00)),
                listOf(Color(0xFF7C4DFF), Color(0xFFE040FB)),
                listOf(Color(0xFF69F0AE), Color(0xFF00E676))
            )
            var activeGrIdx by remember { mutableStateOf(0) }
            var isCloseFriendsOnly by remember { mutableStateOf(false) }

            // Sticker insertion state
            var activeStickerType by remember { mutableStateOf<String?>(null) } // "location", "hashtag", "mention", "emoji"
            var activeStickerValue by remember { mutableStateOf("") }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF121212))
            ) {
                // Layout Scroll Container
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Title Header with close option
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = { showStoryCreator = false }) {
                            Icon(Icons.Filled.Close, contentDescription = "Cancel", tint = Color.White)
                        }
                        Text("Create Story", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Box(modifier = Modifier.width(36.dp))
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Simulated live interactive viewport showing the gradient card and live stickers
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .shadow(12.dp, RoundedCornerShape(24.dp)),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Brush.linearGradient(grOptions[activeGrIdx]))
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (newStoryCaption.isNotBlank()) newStoryCaption else "Tap text input below...",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    textAlign = TextAlign.Center
                                )

                                if (activeStickerValue.isNotBlank() && activeStickerType != null) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    when (activeStickerType) {
                                        "mention" -> {
                                            Row(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(Color.White)
                                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(Icons.Filled.AlternateEmail, contentDescription = null, tint = Color(0xFFE91E63), modifier = Modifier.size(14.dp))
                                                Text(activeStickerValue, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            }
                                        }
                                        "hashtag" -> {
                                            Row(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(Brush.horizontalGradient(listOf(Color(0xFF9C27B0), Color(0xFFE91E63))))
                                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(Icons.Filled.Tag, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                                Text(activeStickerValue, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            }
                                        }
                                        "location" -> {
                                            Row(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(Color(0xFF2196F3))
                                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(Icons.Filled.Place, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                                Text(activeStickerValue, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            }
                                        }
                                        "emoji" -> {
                                            Text(activeStickerValue, fontSize = 32.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Caption editing block
                    OutlinedTextField(
                        value = newStoryCaption,
                        onValueChange = { newStoryCaption = it },
                        placeholder = { Text("What's on your mind? ✨", color = Color.Gray) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF1F1F1F),
                            unfocusedContainerColor = Color(0xFF1F1F1F),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF0091EA),
                            unfocusedBorderColor = Color(0xFF333333)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Background selection
                    Text("Select Background Theme", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        grOptions.forEachIndexed { i, colors ->
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Brush.linearGradient(colors))
                                    .border(
                                        width = if (activeGrIdx == i) 3.dp else 1.dp,
                                        color = if (activeGrIdx == i) Color.White else Color.White.copy(alpha = 0.2f),
                                        shape = CircleShape
                                    )
                                    .clickable { activeGrIdx = i }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Sticker Quick insertion buttons
                    Text("Add Sticker Component", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                activeStickerType = "location"
                                activeStickerValue = "San Francisco, CA"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3).copy(alpha = 0.2f)),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("📍 Place", color = Color(0xFF2196F3), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = {
                                activeStickerType = "hashtag"
                                activeStickerValue = "Aqualyn"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63).copy(alpha = 0.2f)),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("# Tag", color = Color(0xFFE91E63), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = {
                                activeStickerType = "mention"
                                activeStickerValue = "harsh_7742"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0).copy(alpha = 0.2f)),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("@ User", color = Color(0xFF9C27B0), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Emojis shelf triggers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        listOf("🔥", "❤️", "😂", "🙌", "✨", "🚀").forEach { emoji ->
                            IconButton(onClick = {
                                activeStickerType = "emoji"
                                activeStickerValue = emoji
                            }) {
                                Text(emoji, fontSize = 28.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Close Friends slider & Publish trigger
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF1F1F1F))
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFF4CAF50))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Close Friends only", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Make visible only to selected people", color = Color.Gray, fontSize = 11.sp)
                            }
                        }
                        Switch(
                            checked = isCloseFriendsOnly,
                            onCheckedChange = { isCloseFriendsOnly = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF4CAF50)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = {
                            if (newStoryCaption.isNotBlank()) {
                                storiesList.add(
                                    0,
                                    LocalStory(
                                        id = System.currentTimeMillis().toString(),
                                        username = "you",
                                        gradient = grOptions[activeGrIdx],
                                        text = newStoryCaption,
                                        isMine = true,
                                        avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&q=80&w=200",
                                        isCloseFriends = isCloseFriendsOnly,
                                        stickers = if (activeStickerType != null && activeStickerValue.isNotBlank()) {
                                            listOf(LocalSticker(System.currentTimeMillis().toString(), activeStickerType!!, activeStickerValue))
                                        } else emptyList()
                                    )
                                )
                                triggerToast("Story published securely!")
                                showStoryCreator = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        enabled = newStoryCaption.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0091EA),
                            disabledContainerColor = Color(0xFF333333)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Share Story", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                    }
                }
            }
        }
    }

    // POST CREATOR DIALOG (Mimicking React PostCreator)
    if (showPostCreator) {
        Dialog(
            onDismissRequest = { showPostCreator = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            var postCaption by remember { mutableStateOf("") }
            var postLocation by remember { mutableStateOf("") }
            var postHasImage by remember { mutableStateOf(true) } // default true to load real photos
            val defaultPhotoUrl = "https://images.unsplash.com/photo-1501854140801-50d01698950b?auto=format&fit=crop&q=80&w=800"

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = { showPostCreator = false }) {
                            Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.Black)
                        }
                        Text("New Post", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
                        TextButton(
                            onClick = {
                                if (postCaption.isNotBlank()) {
                                    postsList.add(
                                        0,
                                        LocalPost(
                                            id = System.currentTimeMillis().toString(),
                                            username = "you",
                                            caption = postCaption,
                                            imageGradient = listOf(Color(0xFF2193b0), Color(0xFF6dd5ed)),
                                            imageUrl = if (postHasImage) defaultPhotoUrl else null,
                                            avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&q=80&w=200",
                                            location = if (postLocation.isNotBlank()) postLocation else null
                                        )
                                    )
                                    triggerToast("Post uploaded successfully!")
                                    showPostCreator = false
                                }
                            },
                            enabled = postCaption.isNotBlank()
                        ) {
                            Text("Share", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = if (postCaption.isNotBlank()) Color(0xFF0091EA) else Color.LightGray)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // User identity placeholder
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&q=80&w=200",
                            contentDescription = "",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Your Account", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 15.sp)
                            Text("Public Feed", color = Color.Gray, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Text Caption Editor
                    OutlinedTextField(
                        value = postCaption,
                        onValueChange = { postCaption = it },
                        placeholder = { Text("Write a caption... 📸🎨", color = Color.Gray) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF5F5F5),
                            unfocusedContainerColor = Color(0xFFF5F5F5),
                            focusedBorderColor = Color(0xFF0091EA),
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Image attachment simulator
                    Text("Select Attachment", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF5F5F5))
                            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(16.dp))
                            .clickable { postHasImage = !postHasImage }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Image,
                            contentDescription = null,
                            tint = if (postHasImage) Color(0xFF0091EA) else Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                if (postHasImage) "Photo attached (Landscape)" else "No Photo attached",
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                fontSize = 13.sp
                            )
                            Text("Toggle to attach highly aesthetic landscape image", color = Color.Gray, fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Checkbox(
                            checked = postHasImage,
                            onCheckedChange = { postHasImage = it },
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF0091EA))
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Location attachment option
                    Text("Add Location Details", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = postLocation,
                        onValueChange = { postLocation = it },
                        leadingIcon = { Icon(Icons.Filled.Place, contentDescription = null, tint = Color.Gray) },
                        placeholder = { Text("e.g. San Francisco, Goa, India", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = Color(0xFF0091EA),
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        )
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    Button(
                        onClick = {
                            if (postCaption.isNotBlank()) {
                                postsList.add(
                                    0,
                                    LocalPost(
                                        id = System.currentTimeMillis().toString(),
                                        username = "you",
                                        caption = postCaption,
                                        imageGradient = listOf(Color(0xFF2193b0), Color(0xFF6dd5ed)),
                                        imageUrl = if (postHasImage) defaultPhotoUrl else null,
                                        avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&q=80&w=200",
                                        location = if (postLocation.isNotBlank()) postLocation else null
                                    )
                                )
                                triggerToast("Post uploaded successfully!")
                                showPostCreator = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0091EA)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Post to Feed", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                    }
                }
            }
        }
    }

    // POST DETAIL VIEWER (Comments modal mimicking React PostViewer)
    activePostForDetail?.let { post ->
        Dialog(
            onDismissRequest = { activePostForDetail = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            var inputCommentText by remember { mutableStateOf("") }
            val liveComments = remember { mutableStateListOf<Pair<String, String>>().apply { addAll(post.comments) } }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header title
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                            .padding(top = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { activePostForDetail = null }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Comments Detail", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
                        Spacer(modifier = Modifier.weight(1f))
                        Text("${liveComments.size} items", color = Color.Gray, fontSize = 13.sp)
                    }
                    HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)

                    // Main scrolling details
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        // Original Post description card
                        item {
                            Row(verticalAlignment = Alignment.Top) {
                                if (!post.avatarUrl.isNullOrEmpty()) {
                                    AsyncImage(
                                        model = post.avatarUrl,
                                        contentDescription = "",
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFE0F7FA)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(post.username.take(2).uppercase(), color = Color(0xFF0091EA), fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(post.username, fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 15.sp)
                                    if (!post.location.isNullOrEmpty()) {
                                        Text(post.location, color = Color.Gray, fontSize = 11.sp)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(post.caption, color = Color.Black, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(post.timeInfo, color = Color.Gray, fontSize = 10.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                            HorizontalDivider(color = Color(0xFFEEEEEE))
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Comments listing
                        if (liveComments.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No comments yet. Start the conversation!", color = Color.Gray)
                                }
                            }
                        } else {
                            itemsIndexed(liveComments) { _, commentPair ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 10.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFECEFF1)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(commentPair.first.take(2).uppercase(), color = Color(0xFF546E7A), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(commentPair.first, fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 13.sp)
                                        Text(commentPair.second, color = Color(0xFF263238), fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }

                    // Bottom comments text submit tool
                    HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(horizontal = 12.dp, vertical = 12.dp)
                            .navigationBarsPadding()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = inputCommentText,
                                onValueChange = { inputCommentText = it },
                                placeholder = { Text("Add comment on ${post.username}...", color = Color.Gray, fontSize = 13.sp) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp),
                                shape = RoundedCornerShape(25.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFFF9F9F9),
                                    unfocusedContainerColor = Color(0xFFF9F9F9),
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black
                                )
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Button(
                                onClick = {
                                    if (inputCommentText.isNotBlank()) {
                                        val newC = "you" to inputCommentText
                                        liveComments.add(newC)
                                        // Also write back to global state list
                                        val indexInGlobal = postsList.indexOfFirst { it.id == post.id }
                                        if (indexInGlobal != -1) {
                                            postsList[indexInGlobal] = postsList[indexInGlobal].copy(
                                                comments = postsList[indexInGlobal].comments + newC
                                            )
                                        }
                                        inputCommentText = ""
                                        triggerToast("Comment added successfully!")
                                    }
                                },
                                enabled = inputCommentText.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0091EA)),
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                shape = RoundedCornerShape(25.dp),
                                modifier = Modifier.height(48.dp)
                            ) {
                                Text("Publish", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LocalPostCard(
    post: LocalPost,
    onLikeChange: (LocalPost) -> Unit,
    onPostClick: () -> Unit
) {
    var showHeartPopup by remember { mutableStateOf(false) }
    val coroutine = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .border(
                1.dp,
                Color.White.copy(alpha = 0.5f),
                RoundedCornerShape(24.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f)),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Post Header detail
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!post.avatarUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = post.avatarUrl,
                        contentDescription = "User Avatar",
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(post.imageGradient)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(post.username.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(post.username, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF263238))
                    if (!post.location.isNullOrEmpty()) {
                        Text(post.location, fontSize = 10.sp, color = Color.Gray)
                    }
                }
                Icon(Icons.Default.MoreVert, contentDescription = "Comments", tint = Color(0xFF546E7A))
            }

            // Post Imagery block with Double-tap support!
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.2f)
                    .pointerInput(post.id) {
                        detectTapGestures(
                            onDoubleTap = {
                                if (!post.isLiked) {
                                    onLikeChange(post.copy(isLiked = true, likeCount = post.likeCount + 1))
                                }
                                showHeartPopup = true
                                coroutine.launch {
                                    delay(900)
                                    showHeartPopup = false
                                }
                            },
                            onTap = {
                                onPostClick()
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                if (!post.imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = post.imageUrl,
                        contentDescription = "Post Context",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.linearGradient(post.imageGradient)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.Image, contentDescription = null, tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(64.dp))
                    }
                }

                // Smooth double tap heart overlay popup
                androidx.compose.animation.AnimatedVisibility(
                    visible = showHeartPopup,
                    enter = scaleIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)) + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    Icon(
                        Icons.Filled.Favorite,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(80.dp)
                    )
                }
            }

            // Post action items row layout
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (post.isLiked) {
                            onLikeChange(post.copy(isLiked = false, likeCount = post.likeCount - 1))
                        } else {
                            onLikeChange(post.copy(isLiked = true, likeCount = post.likeCount + 1))
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (post.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like Post",
                        tint = if (post.isLiked) Color.Red else Color(0xFF263238),
                        modifier = Modifier.size(26.dp)
                    )
                }
                IconButton(onClick = onPostClick) {
                    Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = "Comment/View", tint = Color(0xFF263238), modifier = Modifier.size(24.dp))
                }
                IconButton(onClick = { /* Share */ }) {
                    Icon(Icons.Filled.Share, contentDescription = "Share", tint = Color(0xFF263238), modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = {
                        onLikeChange(post.copy(isSaved = !post.isSaved))
                    }
                ) {
                    Icon(
                        imageVector = if (post.isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Save Post",
                        tint = if (post.isSaved) Color(0xFF0091EA) else Color(0xFF263238),
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            // Likers info, Captions and Comments
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
            ) {
                Text("${post.likeCount} likes", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF263238))
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text(post.username, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF263238))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(post.caption, fontSize = 13.sp, color = Color(0xFF263238), maxLines = 3, overflow = TextOverflow.Ellipsis)
                }
                
                if (post.comments.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "View all ${post.comments.size} comments",
                        fontSize = 12.sp,
                        color = Color(0xFF78909C),
                        modifier = Modifier.clickable { onPostClick() }
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(post.timeInfo, fontSize = 10.sp, color = Color(0xFF90A4AE))
            }
        }
    }
}
