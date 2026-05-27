package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import com.example.model.Message
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChatDetailScreen(userId: String, onBack: () -> Unit, onProfileClick: (String) -> Unit) {
    val resolvedName = when (userId) {
        "c1" -> "Design Team"
        "c2" -> "Secret Project"
        "c3" -> "Harsh Vardhan"
        "c4" -> "Raj Sharma"
        "c5" -> "Mini Militia Devs"
        "raj_1034" -> "Raj Sharma"
        "harsh_7742" -> "Harsh Vardhan"
        "bhavya_xx" -> "Bhavya Goel"
        "sam_99" -> "Samar Joy"
        else -> "Ansh"
    }

    var messages by remember {
        mutableStateOf(
            listOf(
                Message("1", "other", "Hey, how are you?", "10:00 AM", false),
                Message("2", "me", "I'm good, thanks! What about you?", "10:01 AM", true),
                Message("3", "me", "Check out this place", "10:05 AM", true, location = "San Francisco, CA"),
                Message("4", "other", "", "10:10 AM", false, paymentAmount = "$50.00"),
                Message("5", "me", "Thanks for the payment!", "10:11 AM", true)
            )
        )
    }
    
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var showAttachmentPicker by remember { mutableStateOf(false) }
    
    var replyingToMsg by remember { mutableStateOf<Message?>(null) }
    var editingMsg by remember { mutableStateOf<Message?>(null) }
    var selectedActionMsg by remember { mutableStateOf<Message?>(null) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showReactionsForMsg by remember { mutableStateOf<Message?>(null) }
    var isTyping by remember { mutableStateOf(true) }
    var showProfileDetails by remember { mutableStateOf(false) }

    // Calling and search features configurations
    var isSearchActive by remember { mutableStateOf(false) }
    var searchMessageQuery by remember { mutableStateOf("") }
    var showVoiceCallScreen by remember { mutableStateOf(false) }
    var showVideoCallScreen by remember { mutableStateOf(false) }
    var showMoreMenuOptions by remember { mutableStateOf(false) }
    var isExportingChatProgress by remember { mutableStateOf(false) }
    var exportPercent by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        // Toggle typing indicator for realism
        while (true) {
            delay(5000)
            isTyping = !isTyping
        }
    }

    fun sendMessage(
        content: String = "",
        imageUrl: String? = null,
        videoUrl: String? = null,
        audioUrl: String? = null,
        documentName: String? = null,
        location: String? = null,
        paymentAmount: String? = null
    ) {
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        val time = sdf.format(Date())
        
        if (editingMsg != null && content.isNotBlank()) {
            messages = messages.map { if (it.id == editingMsg!!.id) it.copy(content = content, isEdited = true) else it }
            editingMsg = null
            return
        }

        val newMsg = Message(
            id = UUID.randomUUID().toString(),
            senderId = "me",
            content = content,
            timeInfo = time,
            isMine = true,
            imageUrl = imageUrl,
            videoUrl = videoUrl,
            audioUrl = audioUrl,
            documentName = documentName,
            location = location,
            paymentAmount = paymentAmount,
            replyToMsg = replyingToMsg
        )
        messages = messages + newMsg
        replyingToMsg = null
        coroutineScope.launch {
            if (messages.isNotEmpty()) {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isSearchActive) {
                        OutlinedTextField(
                            value = searchMessageQuery,
                            onValueChange = { searchMessageQuery = it },
                            placeholder = { Text("Search messages...", color = Color(0xFF90A4AE)) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                IconButton(onClick = { 
                                    isSearchActive = false
                                    searchMessageQuery = ""
                                }) {
                                    Icon(Icons.Default.Close, contentDescription = "Close search", tint = Color(0xFF546E7A))
                                }
                            }
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onProfileClick(userId) }) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0091EA)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(resolvedName.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF00B0FF))
                                        .border(2.dp, Color.White, CircleShape)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(resolvedName, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF263238))
                                Text("ONLINE NOW", color = Color(0xFF0091EA), fontSize = 10.sp, letterSpacing = 1.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF546E7A))
                    }
                },
                actions = {
                    if (!isSearchActive) {
                        IconButton(onClick = { showVideoCallScreen = true }) { Icon(Icons.Outlined.Videocam, tint = Color(0xFF0091EA), contentDescription = "Video Call") }
                        IconButton(onClick = { showVoiceCallScreen = true }) { Icon(Icons.Outlined.Call, tint = Color(0xFF0091EA), contentDescription = "Audio Call") }
                        
                        Box {
                            IconButton(onClick = { showMoreMenuOptions = true }) { Icon(Icons.Default.MoreVert, tint = Color(0xFF0091EA), contentDescription = "More") }
                            DropdownMenu(
                                expanded = showMoreMenuOptions,
                                onDismissRequest = { showMoreMenuOptions = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Search Chat") },
                                    leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = Color(0xFF0091EA)) },
                                    onClick = {
                                        showMoreMenuOptions = false
                                        isSearchActive = true
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Clear Chat") },
                                    leadingIcon = { Icon(Icons.Outlined.ClearAll, contentDescription = null, tint = Color(0xFFEF5350)) },
                                    onClick = {
                                        showMoreMenuOptions = false
                                        messages = emptyList()
                                        com.example.model.GlobalState.showToast("Chat feed cleared!", isGreen = true)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Export Chat as Zip") },
                                    leadingIcon = { Icon(Icons.Outlined.FolderZip, contentDescription = null, tint = Color(0xFF00C853)) },
                                    onClick = {
                                        showMoreMenuOptions = false
                                        isExportingChatProgress = true
                                        exportPercent = 0
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White.copy(alpha = 0.9f))
            )
        },
        bottomBar = {
            Column {
                AnimatedVisibility(visible = replyingToMsg != null || editingMsg != null) {
                    val msg = replyingToMsg ?: editingMsg
                    val isEditing = editingMsg != null
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.width(4.dp).height(30.dp).background(if (isEditing) Color(0xFF0091EA) else Color(0xFF0091EA), RoundedCornerShape(2.dp)))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(if (isEditing) "Editing Message" else "Replying to ${if (msg?.isMine == true) "yourself" else "Harsh"}", color = if (isEditing) Color(0xFF0091EA) else Color(0xFF0091EA), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(msg?.content.takeIf { !it.isNullOrBlank() } ?: "Attachment", color = Color(0xFF546E7A), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        IconButton(onClick = { replyingToMsg = null; editingMsg = null; inputText = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Cancel", tint = Color(0xFF90A4AE))
                        }
                    }
                }
                ChatInputBar(
                    text = inputText,
                    onTextChange = { inputText = it },
                    onSend = {
                        if (inputText.isNotBlank()) {
                            sendMessage(content = inputText.trim())
                            inputText = ""
                        }
                    },
                    onAttachClick = { showAttachmentPicker = true },
                    onMicClick = {
                        sendMessage(audioUrl = "voice_note.mp3")
                    }
                )
            }
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFFD6F5F6), Color(0xFFE9E5F8))
                )
            )
        ) {
            val filteredMessages = remember(messages, searchMessageQuery) {
                if (searchMessageQuery.isBlank()) messages else messages.filter { it.content.contains(searchMessageQuery, ignoreCase = true) }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                state = listState,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredMessages) { msg ->
                    MessageBubbleView(
                        msg = msg,
                        onClick = { selectedActionMsg = msg },
                        onLongClick = { showReactionsForMsg = msg }
                    )
                }
                if (isTyping) {
                    item {
                        Row(
                            modifier = Modifier.padding(top = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Ansh is typing...", fontSize = 12.sp, color = Color(0xFF0091EA), fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.width(4.dp))
                            val infiniteTransition = rememberInfiniteTransition()
                            val alpha by infiniteTransition.animateFloat(
                                initialValue = 0.2f, targetValue = 1f,
                                animationSpec = infiniteRepeatable(animation = tween(800, easing = LinearEasing), repeatMode = RepeatMode.Reverse)
                            )
                            Row {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF0091EA).copy(alpha = alpha)))
                                Spacer(modifier = Modifier.width(3.dp))
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF0091EA).copy(alpha = (alpha + 0.3f).coerceAtMost(1f))))
                                Spacer(modifier = Modifier.width(3.dp))
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF0091EA).copy(alpha = (alpha + 0.6f).coerceAtMost(1f))))
                            }
                        }
                    }
                }
            }
        }
        
        if (selectedActionMsg != null && !showDeleteConfirmDialog) {
            val msg = selectedActionMsg!!
            val itemsList = remember(msg) {
                val base = mutableListOf(
                    com.example.ui.components.GlassyDropdownItem(Icons.Outlined.Reply, "Reply") {
                        replyingToMsg = msg
                        selectedActionMsg = null
                    },
                    com.example.ui.components.GlassyDropdownItem(Icons.Outlined.ContentCopy, "Copy") {
                        selectedActionMsg = null
                    },
                    com.example.ui.components.GlassyDropdownItem(Icons.Outlined.Forward, "Forward") {
                        selectedActionMsg = null
                    },
                    com.example.ui.components.GlassyDropdownItem(
                        if (msg.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                        if (msg.isPinned) "Unpin Message" else "Pin Message"
                    ) {
                        messages = messages.map { if (it.id == msg.id) it.copy(isPinned = !it.isPinned) else it }
                        selectedActionMsg = null
                    }
                )
                if (msg.isMine && msg.content.isNotBlank()) {
                    base.add(
                        com.example.ui.components.GlassyDropdownItem(Icons.Outlined.Edit, "Edit") {
                            editingMsg = msg
                            inputText = msg.content
                            selectedActionMsg = null
                        }
                    )
                }
                base.add(
                    com.example.ui.components.GlassyDropdownItem(Icons.Outlined.Delete, "Delete", Color(0xFFE53935)) {
                        showDeleteConfirmDialog = true
                    }
                )
                base
            }

            com.example.ui.components.GlassyIphoneDropdown(
                onDismissRequest = { selectedActionMsg = null },
                alignment = Alignment.Center,
                items = itemsList
            )
        }

        if (showDeleteConfirmDialog && selectedActionMsg != null) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmDialog = false; selectedActionMsg = null },
                title = { Text("Delete This Message?", fontWeight = FontWeight.Bold) },
                text = { Text("Delete this message from your device, or delete it from both sides so your friend Raj also cannot see it in our chat.") },
                containerColor = Color.White,
                confirmButton = {
                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                messages = messages.filter { it.id != selectedActionMsg!!.id }
                                showDeleteConfirmDialog = false
                                selectedActionMsg = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                        ) {
                            Text("Delete for Me & Raj (Both Sides)", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        
                        OutlinedButton(
                            onClick = {
                                messages = messages.filter { it.id != selectedActionMsg!!.id }
                                showDeleteConfirmDialog = false
                                selectedActionMsg = null
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Delete for Me Only", color = Color(0xFF546E7A))
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmDialog = false; selectedActionMsg = null }) {
                        Text("Cancel", color = Color.Gray)
                    }
                }
            )
        }

        if (showReactionsForMsg != null) {
            Popup(
                alignment = Alignment.Center,
                onDismissRequest = { showReactionsForMsg = null },
                offset = IntOffset(0, 0)
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color.White.copy(alpha=0.9f))
                        .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(32.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    listOf("👍", "❤️", "😂", "😮", "😢", "🙏").forEach { emoji ->
                        Text(
                            text = emoji,
                            fontSize = 28.sp,
                            modifier = Modifier.clickable {
                                val msg = showReactionsForMsg!!
                                val currentReactions = msg.reactions.toMutableList()
                                if (currentReactions.contains(emoji)) currentReactions.remove(emoji) else currentReactions.add(emoji)
                                messages = messages.map { if (it.id == msg.id) it.copy(reactions = currentReactions) else it }
                                showReactionsForMsg = null
                            }
                        )
                    }
                }
            }
        }
        
        if (showProfileDetails) {
            ModalBottomSheet(
                onDismissRequest = { showProfileDetails = false },
                containerColor = Color.White
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(modifier = Modifier.size(100.dp).clip(CircleShape).background(Color(0xFF0091EA)), contentAlignment = Alignment.Center) {
                        Text("A", color = Color.White, fontSize = 48.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Ansh", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Color(0xFF263238))
                    Text("+1 234 567 8900", fontSize = 16.sp, color = Color(0xFF546E7A))
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { }) {
                            Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFFE1F5FE)), contentAlignment = Alignment.Center) {
                                Icon(Icons.Filled.Call, contentDescription = "Audio", tint = Color(0xFF0091EA))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Audio", fontSize = 12.sp, color = Color(0xFF0091EA), fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { }) {
                            Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFFE1F5FE)), contentAlignment = Alignment.Center) {
                                Icon(Icons.Filled.Videocam, contentDescription = "Video", tint = Color(0xFF0091EA))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Video", fontSize = 12.sp, color = Color(0xFF0091EA), fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { }) {
                            Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFFE1F5FE)), contentAlignment = Alignment.Center) {
                                Icon(Icons.Filled.Search, contentDescription = "Search", tint = Color(0xFF0091EA))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Search", fontSize = 12.sp, color = Color(0xFF0091EA), fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { }) {
                            Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFFE1F5FE)), contentAlignment = Alignment.Center) {
                                Icon(Icons.Outlined.Notifications, contentDescription = "Mute", tint = Color(0xFF0091EA))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Mute", fontSize = 12.sp, color = Color(0xFF0091EA), fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = Color(0xFFECEFF1))
                    Row(modifier = Modifier.fillMaxWidth().clickable { }.padding(horizontal = 24.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Image, contentDescription = null, tint = Color(0xFF263238))
                        Spacer(modifier = Modifier.width(24.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Media, links, and docs", fontSize = 16.sp, color = Color(0xFF263238))
                            Text("12 items", fontSize = 14.sp, color = Color(0xFF78909C))
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth().clickable { }.padding(horizontal = 24.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFF263238))
                        Spacer(modifier = Modifier.width(24.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Starred messages", fontSize = 16.sp, color = Color(0xFF263238))
                            Text("None", fontSize = 14.sp, color = Color(0xFF78909C))
                        }
                    }
                    HorizontalDivider(color = Color(0xFFECEFF1))
                    Row(modifier = Modifier.fillMaxWidth().clickable { }.padding(horizontal = 24.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Lock, contentDescription = null, tint = Color(0xFF263238))
                        Spacer(modifier = Modifier.width(24.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Encyption", fontSize = 16.sp, color = Color(0xFF263238))
                            Text("Messages are end-to-end encrypted", fontSize = 14.sp, color = Color(0xFF78909C))
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth().clickable { }.padding(horizontal = 24.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Timer, contentDescription = null, tint = Color(0xFF263238))
                        Spacer(modifier = Modifier.width(24.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Disappearing messages", fontSize = 16.sp, color = Color(0xFF263238))
                            Text("Off", fontSize = 14.sp, color = Color(0xFF78909C))
                        }
                    }
                    HorizontalDivider(color = Color(0xFFECEFF1))
                    Row(modifier = Modifier.fillMaxWidth().clickable { }.padding(horizontal = 24.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Block, contentDescription = null, tint = Color(0xFFE53935))
                        Spacer(modifier = Modifier.width(24.dp))
                        Text("Block Ansh", fontSize = 16.sp, color = Color(0xFFE53935))
                    }
                    Row(modifier = Modifier.fillMaxWidth().clickable { }.padding(horizontal = 24.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Report, contentDescription = null, tint = Color(0xFFE53935))
                        Spacer(modifier = Modifier.width(24.dp))
                        Text("Report Ansh", fontSize = 16.sp, color = Color(0xFFE53935))
                    }
                }
            }
        }

        
        if (showAttachmentPicker) {
            ModalBottomSheet(
                onDismissRequest = { showAttachmentPicker = false },
                containerColor = Color.White
            ) {
                MediaAttachmentPickerView(
                    onSelect = { option ->
                        showAttachmentPicker = false
                        when (option) {
                            "Camera" -> sendMessage(imageUrl = "photo")
                            "Photo & Video" -> sendMessage(imageUrl = "photo")
                            "Document" -> sendMessage(documentName = "Project_Proposal.pdf")
                            "Location" -> sendMessage(location = "Mountain View, CA")
                            "Send Money" -> sendMessage(paymentAmount = "$50.00")
                        }
                    }
                )
            }
        }

        // --- ZIP EXPORT PROGRESS DIALOG ---
        if (isExportingChatProgress) {
            LaunchedEffect(Unit) {
                while (exportPercent < 100) {
                    delay(80)
                    exportPercent += 10
                }
                isExportingChatProgress = false
                com.example.model.GlobalState.showToast("aqualyn_chat_ansh.zip exported!", isGreen = true)
            }

            androidx.compose.ui.window.Dialog(onDismissRequest = { }) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            progress = exportPercent / 100f,
                            color = Color(0xFF0091EA),
                            trackColor = Color(0xFFECEFF1)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Exporting conversation...", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Preparing ZIP artifact ($exportPercent%)", fontSize = 13.sp, color = Color(0xFF78909C))
                    }
                }
            }
        }

        // --- SIMULATED VOICE CALL SCREEN ---
        if (showVoiceCallScreen) {
            var isMutedCallState by remember { mutableStateOf(false) }
            var isSpeakerCallState by remember { mutableStateOf(false) }
            var timerSeconds by remember { mutableStateOf(0) }
            LaunchedEffect(Unit) {
                while (true) {
                    delay(1000)
                    timerSeconds += 1
                }
            }

            androidx.compose.ui.window.Dialog(
                properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
                onDismissRequest = { }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(listOf(Color(0xFF00365C), Color(0xFF000E1C))))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(top = 48.dp)
                        ) {
                            Text("AQUALYN SECURE VOICE CALL", color = Color(0xFF00E5FF), fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                            Spacer(modifier = Modifier.height(24.dp))
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(0.1f))
                                    .border(2.dp, Color(0xFF00E5FF), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF0091EA)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("AN", fontWeight = FontWeight.Bold, fontSize = 32.sp, color = Color.White)
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Ansh", fontWeight = FontWeight.Bold, fontSize = 28.sp, color = Color.White)
                            Spacer(modifier = Modifier.height(8.dp))
                            val minutesStr = (timerSeconds / 60).toString().padStart(2, '0')
                            val secondsStr = (timerSeconds % 60).toString().padStart(2, '0')
                            Text("Connected • $minutesStr:$secondsStr", fontSize = 15.sp, color = Color.White.copy(0.7f))
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 48.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { isMutedCallState = !isMutedCallState },
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(if (isMutedCallState) Color.White else Color.White.copy(0.15f))
                            ) {
                                Icon(
                                    imageVector = if (isMutedCallState) Icons.Default.MicOff else Icons.Default.Mic,
                                    contentDescription = "Mute",
                                    tint = if (isMutedCallState) Color(0xFF00365C) else Color.White
                                )
                            }

                            IconButton(
                                onClick = {
                                    showVoiceCallScreen = false
                                    com.example.model.GlobalState.showToast("Voice call completed details logged", isGreen = true)
                                },
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEF5350))
                            ) {
                                Icon(Icons.Default.CallEnd, contentDescription = "Decline", tint = Color.White, modifier = Modifier.size(36.dp))
                            }

                            IconButton(
                                onClick = { isSpeakerCallState = !isSpeakerCallState },
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(if (isSpeakerCallState) Color.White else Color.White.copy(0.15f))
                            ) {
                                Icon(
                                    imageVector = if (isSpeakerCallState) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                                    contentDescription = "Speaker",
                                    tint = if (isSpeakerCallState) Color(0xFF00365C) else Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- SIMULATED VIDEO CALL SCREEN ---
        if (showVideoCallScreen) {
            var isCameraOn by remember { mutableStateOf(true) }
            var isVoiceMuted by remember { mutableStateOf(false) }
            var callConnectedTime by remember { mutableStateOf(0) }
            LaunchedEffect(Unit) {
                while (true) {
                    delay(1000)
                    callConnectedTime += 1
                }
            }

            androidx.compose.ui.window.Dialog(
                properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
                onDismissRequest = { }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF001122))
                ) {
                    if (isCameraOn) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color(0xFF00E5FF).copy(0.4f), Color(0xFF006064).copy(0.8f))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Filled.Person, contentDescription = null, tint = Color.White.copy(0.3f), modifier = Modifier.size(100.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Ansh's Feed Camera Simulator", color = Color.White.copy(0.6f), fontSize = 12.sp)
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Ansh's Video Paused", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    Card(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 80.dp, end = 24.dp)
                            .size(100.dp, 150.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.DarkGray),
                        border = BorderStroke(1.5.dp, Color.White)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(listOf(Color(0xFF0091EA), Color(0xFF637BFE)))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(imageVector = Icons.Outlined.PhotoCamera, contentDescription = null, tint = Color.White.copy(0.8f), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Your Video", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 40.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Ansh Video Call Session", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
                                val minStr = (callConnectedTime / 60).toString().padStart(2, '0')
                                val secStr = (callConnectedTime % 60).toString().padStart(2, '0')
                                Text("Aqualyn HD Link • Connected $minStr:$secStr", color = Color(0xFF00E5FF), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 40.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { isCameraOn = !isCameraOn },
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(if (isCameraOn) Color.White.copy(0.2f) else Color.White)
                            ) {
                                Icon(
                                    imageVector = if (isCameraOn) Icons.Filled.VideocamOff else Icons.Filled.Videocam,
                                    tint = if (isCameraOn) Color.White else Color(0xFF001122),
                                    contentDescription = "Toggle Video"
                                )
                            }

                            IconButton(
                                onClick = {
                                    showVideoCallScreen = false
                                    com.example.model.GlobalState.showToast("Video session terminated. Connection secure.", isGreen = true)
                                },
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEF5350))
                            ) {
                                Icon(Icons.Default.CallEnd, contentDescription = "Decline call", tint = Color.White, modifier = Modifier.size(36.dp))
                            }

                            IconButton(
                                onClick = { isVoiceMuted = !isVoiceMuted },
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(if (isVoiceMuted) Color.White else Color.White.copy(0.2f))
                            ) {
                                Icon(
                                    imageVector = if (isVoiceMuted) Icons.Filled.MicOff else Icons.Filled.Mic,
                                    tint = if (isVoiceMuted) Color(0xFF001122) else Color.White,
                                    contentDescription = "Mute Voice"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActionTab(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onClick).padding(8.dp)) {
        Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFF0091EA).copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = label, tint = Color(0xFF0091EA))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = 12.sp, color = Color(0xFF546E7A))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageBubbleView(msg: Message, onClick: () -> Unit, onLongClick: () -> Unit) {
    val isMe = msg.isMine
    val bubbleBrush = if (isMe) Brush.linearGradient(listOf(Color(0xFF0091EA), Color(0xFF637BFE))) else Brush.linearGradient(listOf(Color.White, Color.White))
    val textColor = if (isMe) Color.White else Color(0xFF263238)
    val alignment = if (isMe) Alignment.End else Alignment.Start
    val shape = if (isMe) RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp) else RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp)

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalAlignment = alignment
    ) {
        if (msg.isPinned) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 4.dp)) {
                Icon(Icons.Filled.PushPin, contentDescription = "Pinned", tint = Color(0xFF0091EA), modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Pinned Message", fontSize = 10.sp, color = Color(0xFF90A4AE), fontWeight = FontWeight.Bold)
            }
        }
        
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(shape)
                .background(bubbleBrush)
                .then(if (!isMe) Modifier.border(1.dp, Color(0xFFECEFF1), shape) else Modifier)
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick
                )
                .padding(14.dp)
        ) {
            Column {
                if (msg.replyToMsg != null) {
                    val reply = msg.replyToMsg
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.width(4.dp).height(30.dp).background(if (isMe) Color.White else Color(0xFF0091EA), RoundedCornerShape(2.dp)))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(if (reply.isMine) "You" else "Harsh", color = if (isMe) Color.White else Color(0xFF0091EA), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(reply.content.takeIf { it.isNotBlank() } ?: "Attachment", color = textColor.copy(alpha = 0.8f), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                if (msg.imageUrl != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📷 Image", color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
                
                if (msg.location != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFFF9C4)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Filled.Place, contentDescription = "Location", tint = Color(0xFFE53935), modifier = Modifier.size(32.dp))
                            Text(msg.location!!, color = Color(0xFF263238), fontSize = 12.sp, modifier = Modifier.padding(8.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
                
                if (msg.paymentAmount != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isMe) Color.White.copy(alpha=0.2f) else Color(0xFFE8F5E9))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFF0091EA)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.AttachMoney, contentDescription = "Money", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(msg.paymentAmount!!, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textColor)
                            Text(if (isMe) "You sent payment" else "Payment received", fontSize = 12.sp, color = textColor.copy(alpha=0.8f))
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
                
                if (msg.documentName != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isMe) Color.White.copy(alpha=0.2f) else Color(0xFFE3F2FD))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFF2196F3)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.InsertDriveFile, contentDescription = "File", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(msg.documentName!!, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = textColor)
                            Text("2.4 MB", fontSize = 12.sp, color = textColor.copy(alpha=0.8f))
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
                
                if (msg.audioUrl != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = "Play", tint = textColor)
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(modifier = Modifier.weight(1f).height(4.dp).background(if(isMe) Color.White.copy(alpha=0.5f) else Color.LightGray)) {
                            Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(0.3f).background(textColor))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("0:14", fontSize = 12.sp, color = textColor)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                if (msg.content.isNotBlank()) {
                    Text(msg.content, color = textColor, fontSize = 16.sp)
                }
                
                if (msg.reactions.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.8f))
                            .border(1.dp, Color(0xFFECEFF1), RoundedCornerShape(12.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        msg.reactions.forEach { reaction ->
                            Text(reaction, fontSize = 14.sp)
                        }
                    }
                }
                
                Row(
                    modifier = Modifier.align(Alignment.End).padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (msg.isEdited) {
                        Text("Edited", fontSize = 10.sp, color = textColor.copy(alpha = 0.7f), fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(msg.timeInfo, fontSize = 10.sp, color = textColor.copy(alpha = 0.7f))
                    if (isMe) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.Check, contentDescription = "Read", tint = textColor.copy(alpha = 0.7f), modifier = Modifier.size(12.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInputBar(text: String, onTextChange: (String) -> Unit, onSend: () -> Unit, onAttachClick: () -> Unit, onMicClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onAttachClick) {
            Icon(Icons.Filled.Add, contentDescription = "Add", tint = Color(0xFF0091EA), modifier = Modifier.size(28.dp))
        }
        
        Spacer(modifier = Modifier.width(4.dp))
        
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            placeholder = { Text("Message...", color = Color(0xFF90A4AE)) },
            modifier = Modifier
                .weight(1f)
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF4F7F9),
                focusedContainerColor = Color(0xFFF4F7F9),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            ),
            trailingIcon = {
                Icon(Icons.Outlined.Mood, contentDescription = "Emoji", tint = Color(0xFF90A4AE))
            }
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        if (text.isNotBlank()) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0091EA))
                    .clickable { onSend() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
            }
        } else {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0091EA))
                    .clickable { onMicClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Mic, contentDescription = "Mic", tint = Color.White)
            }
        }
    }
}

@Composable
fun MediaAttachmentPickerView(onSelect: (String) -> Unit) {
    val options = listOf(
        Triple("Camera", Icons.Filled.CameraAlt, Color(0xFF0091EA)),
        Triple("Photo & Video", Icons.Filled.Photo, Color(0xFF637BFE)),
        Triple("Document", Icons.Filled.InsertDriveFile, Color(0xFF00B0FF)),
        Triple("Location", Icons.Filled.Place, Color(0xFF0091EA)),
        Triple("Send Money", Icons.Filled.AttachMoney, Color(0xFF00B0FF)),
        Triple("Schedule", Icons.Filled.AccessTime, Color(0xFFFF9800))
    )

    Column(modifier = Modifier.padding(24.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            options.take(3).forEach { (label, icon, color) ->
                AttachmentOptionItem(label, icon, color) { onSelect(label) }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            options.drop(3).forEach { (label, icon, color) ->
                AttachmentOptionItem(label, icon, color) { onSelect(label) }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun AttachmentOptionItem(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(32.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, fontSize = 12.sp, color = Color(0xFF546E7A), fontWeight = FontWeight.Medium)
    }
}
