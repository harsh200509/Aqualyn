package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.unit.IntOffset
import coil.compose.AsyncImage
import com.example.model.ChatItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatsScreen(
    onChatClick: (String) -> Unit,
    onNewChatClick: () -> Unit,
    onProfileClick: () -> Unit,
    onFriendProfileClick: (String) -> Unit
) {
    var isFetching by remember { mutableStateOf(true) }
    var activeTab by remember { mutableStateOf("All") }
    var isSelectionMode by remember { mutableStateOf(false) }
    var showSearch by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val context = androidx.compose.ui.platform.LocalContext.current
    val selectedChatIds = remember { mutableStateListOf<String>() }

    var isViewingArchivedChats by remember { mutableStateOf(false) }
    var showArchivePinUnlockDialog by remember { mutableStateOf(false) }
    var archivePinAttemptInput by remember { mutableStateOf("") }

    LaunchedEffect(isSelectionMode) {
        if (!isSelectionMode) {
            selectedChatIds.clear()
        }
    }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(1000)
        isFetching = false
    }

    val visibleChats = com.example.model.GlobalState.chats.filter { chat ->
        val matchesSearch = (chat.groupName ?: chat.user?.name ?: "").contains(searchQuery, ignoreCase = true)
        val isArchived = com.example.model.GlobalState.archivedChats[chat.id] == true

        if (isViewingArchivedChats) {
            isArchived && matchesSearch
        } else {
            val matchesTab = when (activeTab) {
                "All" -> !isArchived
                "Personal" -> !isArchived && !chat.isGroup
                "Groups" -> !isArchived && chat.isGroup
                "Unread" -> !isArchived && (chat.unreadCount > 0)
                "Bots" -> !isArchived && (chat.groupName?.contains("Bot", ignoreCase = true) == true || chat.user?.name?.contains("Bot", ignoreCase = true) == true || chat.groupName?.contains("Secret", ignoreCase = true) == true)
                else -> !isArchived
            }
            matchesSearch && matchesTab
        }
    }

    val filteredPinnedChats = visibleChats.filter { it.isPinned }
    val filteredRecentChats = visibleChats.filter { !it.isPinned }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F7F9))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (showSearch) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        placeholder = { Text("Search chats, messages...", color = Color(0xFF90A4AE)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            unfocusedBorderColor = Color(0xFFECEFF1),
                            focusedBorderColor = Color(0xFF0091EA)
                        ),
                        shape = RoundedCornerShape(25.dp),
                        leadingIcon = {
                            Icon(Icons.Filled.Search, contentDescription = null, tint = Color(0xFF90A4AE))
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                     Icon(Icons.Filled.Clear, contentDescription = "Clear", tint = Color(0xFF90A4AE))
                                }
                            }
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = { showSearch = false; searchQuery = "" }) {
                        Text("Cancel", color = Color(0xFF0091EA), fontWeight = FontWeight.Bold)
                    }
                } else if (isSelectionMode) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { isSelectionMode = false }) {
                            Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color(0xFF546E7A))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "${selectedChatIds.size} Selected",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color(0xFF263238)
                        )
                    }
                    
                    Row {
                        IconButton(onClick = {
                            // Archive all selected
                            selectedChatIds.forEach { id ->
                                com.example.model.GlobalState.archivedChats[id] = true
                            }
                            isSelectionMode = false
                            android.widget.Toast.makeText(context, "Selected chats archived", android.widget.Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Outlined.Archive, contentDescription = "Archive Selected", tint = Color(0xFF0091EA))
                        }
                        IconButton(onClick = {
                            // Delete all selected
                            com.example.model.GlobalState.chats.removeAll { selectedChatIds.contains(it.id) }
                            isSelectionMode = false
                            android.widget.Toast.makeText(context, "Selected chats deleted", android.widget.Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Outlined.Delete, contentDescription = "Delete Selected", tint = Color(0xFFE53935))
                        }
                    }
                } else if (isViewingArchivedChats) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { isViewingArchivedChats = false }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF0091EA))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Archived Chats",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF263238),
                            letterSpacing = (-0.5).sp
                        )
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0091EA))
                                .border(2.dp, Color(0xFFE1F5FE), CircleShape)
                                .clickable { onProfileClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("ME", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Aqualyn",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0091EA),
                            letterSpacing = (-0.5).sp
                        )
                    }
                    Row {
                        IconButton(onClick = { showSearch = true }) {
                            Icon(Icons.Filled.Search, contentDescription = "Search", tint = Color(0xFF0091EA))
                        }
                        var showMorePopup by remember { mutableStateOf(false) }
                        Box {
                            IconButton(onClick = { showMorePopup = true }) {
                                Icon(Icons.Filled.MoreVert, contentDescription = "More", tint = Color(0xFF0091EA))
                            }
                            DropdownMenu(
                                expanded = showMorePopup,
                                onDismissRequest = { showMorePopup = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Mark All Read") },
                                    leadingIcon = { Icon(Icons.Outlined.Check, contentDescription = null) },
                                    onClick = {
                                        showMorePopup = false
                                        com.example.model.GlobalState.chats.forEachIndexed { index, chatItem ->
                                            com.example.model.GlobalState.chats[index] = chatItem.copy(unreadCount = 0)
                                        }
                                        android.widget.Toast.makeText(context, "All chats marked read", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Restore Chat Data") },
                                    leadingIcon = { Icon(Icons.Outlined.Refresh, contentDescription = null) },
                                    onClick = {
                                        showMorePopup = false
                                        com.example.model.GlobalState.chats.clear()
                                        com.example.model.GlobalState.chats.addAll(
                                            listOf(
                                                ChatItem("c1", null, isGroup = true, groupName = "Design Team", lastMessage = "Here is the latest prototype.", "10:24 AM", 3, isPinned = true, isVoiceMessage = false),
                                                ChatItem("c2", null, isGroup = false, groupName = "Secret Project", lastMessage = "Don't tell anyone...", "Yesterday", 0, isPinned = true, isVoiceMessage = false),
                                                ChatItem("c3", null, isGroup = false, groupName = "Harsh", lastMessage = "Check out this place", "11:22 AM", 0, isPinned = false, isVoiceMessage = false),
                                                ChatItem("c4", null, isGroup = false, groupName = "Raj", lastMessage = "Raj suar", "07:21 AM", 0, isPinned = false, isVoiceMessage = false),
                                                ChatItem("c5", null, isGroup = false, groupName = "Mini Militia", lastMessage = "Hello", "05:31 PM", 0, isPinned = false, isVoiceMessage = true)
                                            )
                                        )
                                        com.example.model.GlobalState.archivedChats.clear()
                                        com.example.model.GlobalState.mutedChats.clear()
                                        com.example.model.GlobalState.folderChatMap.clear()
                                        android.widget.Toast.makeText(context, "State reset successful", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Tabs
            if (!isSelectionMode && !showSearch && !isViewingArchivedChats) {
                androidx.compose.foundation.lazy.LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val tabs = listOf("All", "Personal", "Groups", "Unread", "Bots")
                    items(tabs) { tab ->
                        TabChip(tab, activeTab == tab) { activeTab = tab }
                    }
                    item {
                        Box(
                            modifier = Modifier
                                .height(32.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(16.dp))
                                .padding(horizontal = 16.dp)
                                .clickable {
                                    android.widget.Toast.makeText(context, "Create folder action", android.widget.Toast.LENGTH_SHORT).show()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.FolderOpen, contentDescription = null, tint = Color(0xFF546E7A), modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // Main Content area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (isFetching) {
                    // Skeleton Loading
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        for (i in 0..4) {
                            Row(modifier = Modifier.padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(Color(0xFFE0E0E0)))
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Box(modifier = Modifier.height(16.dp).width(120.dp).background(Color(0xFFE0E0E0), RoundedCornerShape(4.dp)))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Box(modifier = Modifier.height(14.dp).fillMaxWidth(0.6f).background(Color(0xFFE0E0E0), RoundedCornerShape(4.dp)))
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        val archivedCount = com.example.model.GlobalState.chats.count { com.example.model.GlobalState.archivedChats[it.id] == true }
                        if (archivedCount > 0 && !isViewingArchivedChats) {
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(Color(0xFFE0F7FA).copy(0.6f))
                                        .clickable {
                                            if (com.example.model.GlobalState.archivePinRequired && com.example.model.GlobalState.archiveLockedState) {
                                                showArchivePinUnlockDialog = true
                                            } else {
                                                isViewingArchivedChats = true
                                            }
                                        }
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Outlined.Archive, contentDescription = "Archived", tint = Color(0xFF0091EA), modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Archived Chats", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF263238))
                                        Text("$archivedCount chat conversation" + (if (archivedCount > 1) "s" else ""), fontSize = 12.sp, color = Color(0xFF78909C))
                                    }
                                    if (com.example.model.GlobalState.archivePinRequired) {
                                        Icon(
                                            imageVector = if (com.example.model.GlobalState.archiveLockedState) Icons.Filled.Lock else Icons.Filled.LockOpen,
                                            contentDescription = "Lock State",
                                            tint = if (com.example.model.GlobalState.archiveLockedState) Color(0xFFEF5350) else Color(0xFF4CAF50),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    } else {
                                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color(0xFF90A4AE))
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }

                        if (filteredPinnedChats.isNotEmpty()) {
                            item {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                                    Text("Pinned", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF263238))
                                    Spacer(modifier = Modifier.weight(1f))
                                    Icon(Icons.Filled.PushPin, contentDescription = null, tint = Color(0xFF0091EA), modifier = Modifier.size(16.dp))
                                }
                            }
                            items(filteredPinnedChats) { chat ->
                                ChatListItemWrapper(
                                    chat = chat,
                                    isSelected = selectedChatIds.contains(chat.id),
                                    isSelectionMode = isSelectionMode,
                                    onToggleSelect = { id ->
                                        if (selectedChatIds.contains(id)) {
                                            selectedChatIds.remove(id)
                                        } else {
                                            selectedChatIds.add(id)
                                        }
                                        isSelectionMode = selectedChatIds.isNotEmpty()
                                    },
                                    onClick = {
                                        if (isSelectionMode) {
                                            if (selectedChatIds.contains(chat.id)) {
                                                selectedChatIds.remove(chat.id)
                                            } else {
                                                selectedChatIds.add(chat.id)
                                            }
                                            isSelectionMode = selectedChatIds.isNotEmpty()
                                        } else {
                                            onChatClick(chat.id)
                                        }
                                    },
                                    onFriendProfileClick = onFriendProfileClick
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                        
                        if (filteredRecentChats.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Recent", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF263238), modifier = Modifier.padding(bottom = 8.dp))
                            }
                            items(filteredRecentChats) { chat ->
                                ChatListItemWrapper(
                                    chat = chat,
                                    isSelected = selectedChatIds.contains(chat.id),
                                    isSelectionMode = isSelectionMode,
                                    onToggleSelect = { id ->
                                        if (selectedChatIds.contains(id)) {
                                            selectedChatIds.remove(id)
                                        } else {
                                            selectedChatIds.add(id)
                                        }
                                        isSelectionMode = selectedChatIds.isNotEmpty()
                                    },
                                    onClick = {
                                        if (isSelectionMode) {
                                            if (selectedChatIds.contains(chat.id)) {
                                                selectedChatIds.remove(chat.id)
                                            } else {
                                                selectedChatIds.add(chat.id)
                                            }
                                            isSelectionMode = selectedChatIds.isNotEmpty()
                                        } else {
                                            onChatClick(chat.id)
                                        }
                                    },
                                    onFriendProfileClick = onFriendProfileClick
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }

                        if (filteredPinnedChats.isEmpty() && filteredRecentChats.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                                    Text("No chats found", color = Color(0xFF90A4AE))
                                }
                            }
                        }
                    }
                }
            }
        }

        // FAB
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 104.dp) // Offset for bottom nav
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Brush.linearGradient(listOf(Color(0xFF0091EA), Color(0xFF637BFE))))
                .clickable { onNewChatClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Edit, contentDescription = "New", tint = Color.White)
        }

        if (showArchivePinUnlockDialog) {
            androidx.compose.ui.window.Dialog(onDismissRequest = { showArchivePinUnlockDialog = false }) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Unlock Archives", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color(0xFF263238))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Enter your 4-digit security lock PIN:", color = Color(0xFF78909C), fontSize = 13.sp, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = archivePinAttemptInput,
                            onValueChange = { if (it.length <= 4) archivePinAttemptInput = it },
                            placeholder = { Text("PIN Code") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(0.8f),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                            )
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            TextButton(onClick = { showArchivePinUnlockDialog = false; archivePinAttemptInput = "" }) {
                                Text("Cancel", color = Color(0xFF546E7A))
                            }
                            Button(
                                onClick = {
                                    if (archivePinAttemptInput == com.example.model.GlobalState.archivePinCode) {
                                        com.example.model.GlobalState.archiveLockedState = false
                                        isViewingArchivedChats = true
                                        showArchivePinUnlockDialog = false
                                        archivePinAttemptInput = ""
                                        com.example.model.GlobalState.showToast("Archive unlocked successfully!", isGreen = true)
                                    } else {
                                        com.example.model.GlobalState.showToast("Incorrect PIN. ACCESS DENIED!", isGreen = false)
                                        archivePinAttemptInput = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0091EA)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Verify PIN", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TabChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .height(32.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) Color(0xFF0091EA) else Color.White.copy(alpha = 0.5f))
            .border(
                1.dp,
                if (isSelected) Color.Transparent else Color(0xFFCFD8DC),
                RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = text,
                color = if (isSelected) Color.White else Color(0xFF546E7A),
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 13.sp
            )
            if (text == "Unread") {
                val unreadCount = com.example.model.GlobalState.chats.count { it.unreadCount > 0 }
                if (unreadCount > 0) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier.size(16.dp).clip(CircleShape).background(if (isSelected) Color.White else Color(0xFF0091EA)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(unreadCount.toString(), color = if (isSelected) Color(0xFF0091EA) else Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ChatListItemWrapper(
    chat: com.example.model.ChatItem,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onToggleSelect: (String) -> Unit,
    onClick: () -> Unit,
    onFriendProfileClick: (String) -> Unit
) {
    var showOptions by remember { mutableStateOf(false) }
    var showFolderSelectorDialog by remember { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current

    Box {
        ChatListItem(
            chat = chat,
            isSelected = isSelected,
            isSelectionMode = isSelectionMode,
            onClick = onClick,
            onLongClick = { showOptions = true },
            onFriendProfileClick = onFriendProfileClick
        )

        if (showFolderSelectorDialog) {
            androidx.compose.ui.window.Dialog(onDismissRequest = { showFolderSelectorDialog = false }) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    var newFolderNameInput by remember { mutableStateOf("") }
                    var isCreatingNewFolder by remember { mutableStateOf(false) }

                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Organize into Folder", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF263238))
                            IconButton(onClick = { showFolderSelectorDialog = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Close")
                            }
                        }
                        
                        Divider(modifier = Modifier.padding(vertical = 12.dp))

                        val currentAssignedFolder = com.example.model.GlobalState.folderChatMap[chat.id]
                        if (currentAssignedFolder != null) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Currently in: $currentAssignedFolder", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF0091EA))
                                TextButton(onClick = {
                                    com.example.model.GlobalState.folderChatMap.remove(chat.id)
                                    showFolderSelectorDialog = false
                                    com.example.model.GlobalState.showToast("Removed from folder", isGreen = false)
                                }) {
                                    Text("Remove", color = Color.Red, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Create new folders + icon
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable { isCreatingNewFolder = !isCreatingNewFolder }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.AddCircle, contentDescription = null, tint = Color(0xFF0091EA))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Create New Folder", color = Color(0xFF0091EA), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        if (isCreatingNewFolder) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = newFolderNameInput,
                                    onValueChange = { newFolderNameInput = it },
                                    placeholder = { Text("Folder Name...") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    onClick = {
                                        if (newFolderNameInput.isNotBlank()) {
                                            val name = newFolderNameInput.trim()
                                            if (!com.example.model.GlobalState.folders.contains(name)) {
                                                com.example.model.GlobalState.folders.add(name)
                                                newFolderNameInput = ""
                                                isCreatingNewFolder = false
                                                com.example.model.GlobalState.showToast("Folder '$name' created!", isGreen = true)
                                            } else {
                                                com.example.model.GlobalState.showToast("Folder already exists!", isGreen = false)
                                            }
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = "Add", tint = Color(0xFF00C853))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Add to Folder List:", color = Color(0xFF546E7A), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            com.example.model.GlobalState.folders.forEach { folder ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (currentAssignedFolder == folder) Color(0xFFE0F7FA) else Color(0xFFF4F7F9))
                                        .clickable {
                                            com.example.model.GlobalState.folderChatMap[chat.id] = folder
                                            showFolderSelectorDialog = false
                                            com.example.model.GlobalState.showToast("Added chat to folder: $folder", isGreen = true)
                                        }
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Filled.Folder, contentDescription = null, tint = if (currentAssignedFolder == folder) Color(0xFF0091EA) else Color(0xFF90A4AE))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(folder, fontWeight = FontWeight.Bold, color = if (currentAssignedFolder == folder) Color(0xFF006064) else Color(0xFF37474F))
                                }
                            }
                        }
                    }
                }
            }
        }
        if (showOptions) {
            Popup(
                alignment = Alignment.CenterEnd,
                onDismissRequest = { showOptions = false },
                offset = IntOffset(-40, 20)
            ) {
                Column(
                    modifier = Modifier
                        .width(220.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White.copy(alpha = 0.95f))
                        .border(1.dp, Color(0xFFECEFF1), RoundedCornerShape(24.dp))
                        .padding(vertical = 12.dp)
                ) {
                    ChatMenuItem(Icons.Outlined.CheckCircle, "Select") {
                        onToggleSelect(chat.id)
                        showOptions = false
                    }
                    val isArchived = com.example.model.GlobalState.archivedChats[chat.id] == true
                    ChatMenuItem(Icons.Outlined.Archive, if (isArchived) "Unarchive" else "Archive") {
                        com.example.model.GlobalState.archivedChats[chat.id] = !isArchived
                        showOptions = false
                        android.widget.Toast.makeText(context, if (isArchived) "Chat unarchived" else "Chat archived", android.widget.Toast.LENGTH_SHORT).show()
                    }
                    ChatMenuItem(Icons.Outlined.PushPin, if (chat.isPinned) "Unpin" else "Pin") {
                        val index = com.example.model.GlobalState.chats.indexOfFirst { it.id == chat.id }
                        if (index != -1) {
                            com.example.model.GlobalState.chats[index] = com.example.model.GlobalState.chats[index].copy(isPinned = !chat.isPinned)
                        }
                        showOptions = false
                        android.widget.Toast.makeText(context, if (chat.isPinned) "Chat unpinned" else "Chat pinned", android.widget.Toast.LENGTH_SHORT).show()
                    }
                    val isMuted = com.example.model.GlobalState.mutedChats[chat.id] == true
                    ChatMenuItem(Icons.Outlined.VolumeOff, if (isMuted) "Unmute" else "Mute") {
                        com.example.model.GlobalState.mutedChats[chat.id] = !isMuted
                        showOptions = false
                        android.widget.Toast.makeText(context, if (isMuted) "Chat unmuted" else "Chat muted", android.widget.Toast.LENGTH_SHORT).show()
                    }
                    val hasUnread = chat.unreadCount > 0
                    ChatMenuItem(Icons.Outlined.MarkChatUnread, if (hasUnread) "Mark as read" else "Mark as unread") {
                        val index = com.example.model.GlobalState.chats.indexOfFirst { it.id == chat.id }
                        if (index != -1) {
                            com.example.model.GlobalState.chats[index] = com.example.model.GlobalState.chats[index].copy(unreadCount = if (hasUnread) 0 else 1)
                        }
                        showOptions = false
                        android.widget.Toast.makeText(context, if (hasUnread) "Marked as read" else "Marked as unread", android.widget.Toast.LENGTH_SHORT).show()
                    }
                    val currentFolder = com.example.model.GlobalState.folderChatMap[chat.id]
                    ChatMenuItem(Icons.Outlined.CreateNewFolder, "Organize Folder") {
                        showOptions = false
                        showFolderSelectorDialog = true
                    }
                    ChatMenuItem(Icons.Outlined.ClearAll, "Clear history") {
                        val index = com.example.model.GlobalState.chats.indexOfFirst { it.id == chat.id }
                        if (index != -1) {
                            com.example.model.GlobalState.chats[index] = com.example.model.GlobalState.chats[index].copy(lastMessage = "", unreadCount = 0)
                        }
                        showOptions = false
                        android.widget.Toast.makeText(context, "History cleared", android.widget.Toast.LENGTH_SHORT).show()
                    }
                    ChatMenuItem(Icons.Outlined.Delete, "Delete chat", Color(0xFFE53935)) {
                        com.example.model.GlobalState.chats.removeAll { it.id == chat.id }
                        showOptions = false
                        android.widget.Toast.makeText(context, "Chat deleted", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}

@Composable
fun ChatMenuItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, color: Color = Color(0xFF263238), onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text, fontSize = 16.sp, color = color)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatListItem(
    chat: com.example.model.ChatItem,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onFriendProfileClick: (String) -> Unit
) {
    val isMuted = com.example.model.GlobalState.mutedChats[chat.id] == true
    val containerColor = if (isSelected) Color(0xFF0091EA).copy(alpha = 0.08f) else Color.White

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(containerColor)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isSelectionMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onClick() },
                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF0091EA)),
                    modifier = Modifier.padding(end = 12.dp)
                )
            }

            if (chat.isGroup) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFE1F5FE))
                        .clickable { onFriendProfileClick(chat.id) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Groups, contentDescription = null, tint = Color(0xFF0091EA))
                }
            } else {
                Box(
                    modifier = Modifier.clickable { onFriendProfileClick(chat.id) }
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFB3E5FC)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(chat.groupName?.take(2)?.uppercase() ?: chat.user?.name?.take(2)?.uppercase() ?: "U", color = Color(0xFF0091EA), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                    if (!chat.isVoiceMessage) { // pseudo logic for indicator
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = 2.dp, y = 2.dp)
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00E676))
                                .border(2.dp, Color.White, CircleShape)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (chat.groupName == "Secret Project") {
                            Icon(Icons.Filled.Lock, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = chat.groupName ?: chat.user?.name ?: "",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF263238),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 16.sp
                        )
                        if (isMuted) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Outlined.VolumeOff, contentDescription = "Muted", tint = Color(0xFF90A4AE), modifier = Modifier.size(14.dp))
                        }
                    }
                    Text(
                        text = chat.timeInfo,
                        fontSize = 12.sp,
                        color = Color(0xFF78909C),
                        fontWeight = if (chat.unreadCount > 0) FontWeight.Bold else FontWeight.Normal
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (chat.isVoiceMessage) {
                        Icon(Icons.Filled.Mic, contentDescription = null, tint = Color(0xFF0091EA), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = if (chat.lastMessage.isEmpty()) "No messages" else chat.lastMessage,
                        fontSize = 14.sp,
                        color = if (chat.unreadCount > 0) Color(0xFF263238) else Color(0xFF78909C),
                        fontWeight = if (chat.unreadCount > 0) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    if (chat.unreadCount > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(Color(0xFF0091EA), Color(0xFF637BFE)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(chat.unreadCount.toString(), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    } else if (chat.groupName == "Secret Project" || chat.isGroup) {
                        // show nothing or done status
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Filled.DoneAll, contentDescription = null, tint = Color(0xFF0091EA), modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ChatCard(chat: ChatItem, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.5f))
            .border(1.dp, MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (chat.isGroup) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Groups, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
            } else {
                Box {
                    AsyncImage(
                        model = chat.user?.avatarUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                    if (chat.user?.isOnline == true) {
                        // Normally handle online indicator
                    }
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = chat.groupName ?: chat.user?.name ?: "",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = chat.timeInfo,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = chat.lastMessage,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (chat.unreadCount > 0) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.primaryContainer)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(chat.unreadCount.toString(), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            } else if (chat.isGroup) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Filled.DoneAll, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun ChatListRow(chat: ChatItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (chat.user?.avatarUrl.isNullOrEmpty()) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.PersonAdd, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
        } else {
            AsyncImage(
                model = chat.user?.avatarUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = chat.user?.name ?: "",
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = chat.timeInfo,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = chat.lastMessage,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (chat.isVoiceMessage) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Filled.Mic, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
        }
    }
}
