package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.example.model.GlobalState

data class ContactUser(
    val id: String,
    val username: String,
    val fullName: String,
    val phoneNumber: String,
    val followersCount: Int,
    val followingCount: Int,
    val postsList: List<LocalPost>,
    val isContact: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectionsScreen(onUserClick: (String) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    var activeTab by remember { mutableStateOf(0) } // 0: All Contacts, 1: Followers, 2: Following
    var activeProfileUser by remember { mutableStateOf<ContactUser?>(null) }
    var isBlockedState by remember { mutableStateOf(false) }
    var isReportedState by remember { mutableStateOf(false) }

    // Mock Directory with real contents
    val sampleContacts = remember {
        mutableStateListOf(
            ContactUser(
                "raj_1034", "raj_1034", "Raj Sharma", "+91 98765 43210", 1420, 182,
                listOf(
                    LocalPost("p1", "raj_1034", "Building modern composables", listOf(Color(0xFF2193b0), Color(0xFF6dd5ed)), likeCount = 20),
                    LocalPost("p2", "raj_1034", "Prisms of colors!", listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0)), likeCount = 45)
                )
            ),
            ContactUser(
                "harsh_7742", "harsh_7742", "Harsh Vardhan", "+91 91111 22222", 852, 345,
                listOf(
                    LocalPost("h1", "harsh_7742", "Late night design sessions ⚡️💻", listOf(Color(0xFF11998e), Color(0xFF38ef7d)), likeCount = 88)
                )
            ),
            ContactUser(
                "bhavya_xx", "bhavya_xx", "Bhavya Goel", "+91 88888 77777", 2109, 450,
                listOf(
                    LocalPost("b1", "bhavya_xx", "Stardust and oceans...", listOf(Color(0xFFfc00ff), Color(0xFF00dbde)), likeCount = 104)
                )
            )
        )
    }

    // Global simulation user (For when they search for someone not in their contacts by number)
    val externalUser = remember {
        ContactUser(
            "sam_99", "sam_99", "Samar Joy", "+91 99999 88888", 3450, 620,
            listOf(
                LocalPost("ex1", "sam_99", "Journey across the world! 🌍✈️", listOf(Color(0xFFff9966), Color(0xFFff5e62)), likeCount = 230)
            ),
            isContact = false
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFFD6F5F6), Color(0xFFE9E5F8))
                )
            )
    ) {
        if (activeProfileUser == null) {
            // MAIN CONTACT VIEW
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                        .background(Color.White.copy(alpha = 0.8f))
                        .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .padding(top = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Directory",
                            fontWeight = FontWeight.Black,
                            fontSize = 24.sp,
                            color = Color(0xFF0091EA),
                            letterSpacing = (-0.5).sp
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            IconButton(onClick = {}) {
                                Icon(Icons.Filled.PersonAddAlt1, contentDescription = "Add Contact", tint = Color(0xFF0091EA))
                            }
                        }
                    }
                }

                // Quick buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.72f))
                            .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            .clickable { }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0091EA).copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.Share, contentDescription = null, tint = Color(0xFF0091EA), modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Invite Friends", fontWeight = FontWeight.Bold, color = Color(0xFF0091EA), fontSize = 11.sp)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.72f))
                            .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            .clickable { }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF637BFE).copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.Sync, contentDescription = null, tint = Color(0xFF637BFE), modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Sync List", fontWeight = FontWeight.Bold, color = Color(0xFF637BFE), fontSize = 11.sp)
                        }
                    }
                }

                // Global Search text field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by name or number...", color = Color.Gray, fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = Color.Gray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(alpha = 0.85f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.85f),
                        unfocusedBorderColor = Color.White,
                        focusedBorderColor = Color(0xFF0091EA)
                    ),
                    singleLine = true
                )

                // Tab Selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf("All Contacts", "Followers", "Following").forEachIndexed { index, label ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { activeTab = index }
                        ) {
                            Text(
                                text = label,
                                fontWeight = if (activeTab == index) FontWeight.Bold else FontWeight.Medium,
                                color = if (activeTab == index) Color(0xFF0091EA) else Color(0xFF546E7A),
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .height(3.dp)
                                    .width(48.dp)
                                    .clip(CircleShape)
                                    .background(if (activeTab == index) Color(0xFF0091EA) else Color.Transparent)
                            )
                        }
                    }
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.5f), thickness = 1.dp)

                // Render matching directory items
                val filtered = sampleContacts.filter {
                    val nameMatch = it.fullName.contains(searchQuery, ignoreCase = true) || it.username.contains(searchQuery, ignoreCase = true)
                    val numberMatch = GlobalState.searchByNumberEnabled && it.phoneNumber.contains(searchQuery)
                    nameMatch || numberMatch
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 100.dp, top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (filtered.isNotEmpty()) {
                        items(filtered) { user ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onUserClick(user.id)
                                    },
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f)),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.linearGradient(
                                                    listOf(Color(0xFF0091EA), Color(0xFF637BFE))
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(user.fullName.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(user.fullName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF263238))
                                        Text("@${user.username}", fontSize = 12.sp, color = Color(0xFF546E7A))
                                        if (GlobalState.searchByNumberEnabled) {
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(user.phoneNumber, fontSize = 11.sp, color = Color.Gray)
                                        }
                                    }
                                    IconButton(onClick = { onUserClick(user.id) }) {
                                        Icon(Icons.Filled.ArrowForwardIos, contentDescription = "View Profile", tint = Color(0xFF90A4AE), modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }

                    // Check if Search by Number matches external simulation user
                    val externalMatch = searchQuery.isNotBlank() && searchQuery.replace(" ", "").contains(externalUser.phoneNumber.replace(" ", ""))
                    if (externalMatch && GlobalState.searchByNumberEnabled) {
                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("AQUALYN GLOBAL REVENUE DIRECTORY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0091EA), modifier = Modifier.padding(start = 4.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onUserClick(externalUser.id) },
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                                border = BorderStroke(1.dp, Color(0xFF81C784))
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .clip(CircleShape)
                                            .background(Brush.linearGradient(listOf(Color(0xFF4CAF50), Color(0xFF8BC34A)))),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Filled.Public, contentDescription = null, tint = Color.White)
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(externalUser.fullName, fontWeight = FontWeight.Black, fontSize = 15.sp, color = Color(0xFF1B5E20))
                                        Text("Global Account • ${externalUser.phoneNumber}", fontSize = 12.sp, color = Color(30, 70, 30))
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Tap to visit and Follow profile", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                    }
                                    Icon(Icons.Filled.PersonAddAlt1, contentDescription = "Join", tint = Color(0xFF2E7D32))
                                }
                            }
                        }
                    }

                    if (filtered.isEmpty() && !externalMatch) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 80.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Filled.SearchOff, contentDescription = null, tint = Color(0xFF90A4AE), modifier = Modifier.size(56.dp))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("No matching user found", fontWeight = FontWeight.Bold, color = Color(0xFF546E7A), fontSize = 15.sp)
                                if (!GlobalState.searchByNumberEnabled && searchQuery.any { it.isDigit() }) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Tip: Enable 'Search by Number' in Security Settings!", fontSize = 11.sp, color = Color.Gray, textAlign = TextAlign.Center)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // DETAILED CONTACT PROFILE SCREEN
            val user = activeProfileUser!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(
                            Brush.linearGradient(
                                user.postsList.firstOrNull()?.imageGradient ?: listOf(Color(0xFF0091EA), Color(0xFF637BFE))
                            )
                        )
                ) {
                    IconButton(
                        onClick = { activeProfileUser = null },
                        modifier = Modifier
                            .padding(top = 40.dp, start = 16.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.3f))
                    ) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }

                // Avatar and Floating Profile Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .offset(y = (-40).dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .padding(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            user.postsList.firstOrNull()?.imageGradient ?: listOf(Color(0xFF0091EA), Color(0xFF637BFE))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(user.fullName.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 36.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(user.fullName, fontWeight = FontWeight.Black, fontSize = 22.sp, color = Color(0xFF263238))
                        Text("@${user.username}", fontSize = 14.sp, color = Color(0xFF78909C))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(user.phoneNumber, fontSize = 12.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stats Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(user.followersCount.toString(), fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color(0xFF263238))
                                Text("Followers", fontSize = 11.sp, color = Color.Gray)
                            }
                            Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color(0xFFECEFF1)))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(user.followingCount.toString(), fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color(0xFF263238))
                                Text("Following", fontSize = 11.sp, color = Color.Gray)
                            }
                            Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color(0xFFECEFF1)))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(user.postsList.size.toString(), fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color(0xFF263238))
                                Text("Posts", fontSize = 11.sp, color = Color.Gray)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Follow Action and Interaction Buttons
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val followed = remember { mutableStateOf(GlobalState.followedUsers[user.id] == true) }
                            Button(
                                onClick = {
                                    if (followed.value) {
                                        GlobalState.followedUsers.remove(user.id)
                                    } else {
                                        GlobalState.followedUsers[user.id] = true
                                    }
                                    followed.value = !followed.value
                                },
                                modifier = Modifier.weight(1.3f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (followed.value) Color(0xFF90A4AE) else Color(0xFF0091EA)
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(if (followed.value) Icons.Filled.HowToReg else Icons.Filled.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (followed.value) "Following" else "Follow Account", fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { /* Direct message toggle */ activeProfileUser = null },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF637BFE)),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(Icons.Filled.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Chat", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // USER TIMELINE / POST GALLERY
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .offset(y = (-20).dp)
                ) {
                    Text("Highlights & Stories", fontWeight = FontWeight.Bold, color = Color(0xFF263238), fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        for (i in 1..3) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .border(
                                        2.dp,
                                        Brush.linearGradient(user.postsList.firstOrNull()?.imageGradient ?: listOf(Color(0xFF0091EA), Color(0xFF637BFE))),
                                        CircleShape
                                    )
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .background(Color(0xFFF4F7F9)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFF0091EA), modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Posts & Portfolio (${user.postsList.size})", fontWeight = FontWeight.Bold, color = Color(0xFF263238), fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    user.postsList.forEach { p ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
                        ) {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp)
                                        .background(Brush.linearGradient(p.imageGradient)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Outlined.Image, contentDescription = null, tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(40.dp))
                                }
                                Text(
                                    text = p.caption,
                                    modifier = Modifier.padding(12.dp),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF263238)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = Color(0xFFECEFF1))
                    Spacer(modifier = Modifier.height(16.dp))

                    // REPORT AND BLOCK ACTIONS
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = {
                                if (isBlockedState) {
                                    GlobalState.blockedUsers.remove(user.id)
                                    isBlockedState = false
                                } else {
                                    GlobalState.blockedUsers[user.id] = true
                                    isBlockedState = true
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = if (isBlockedState) Color.Gray else Color(0xFFE53935)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(if (isBlockedState) Icons.Filled.LockOpen else Icons.Filled.Block, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isBlockedState) "Unblock" else "Block User", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                if (isReportedState) {
                                    GlobalState.reportedUsers.remove(user.id)
                                    isReportedState = false
                                } else {
                                    GlobalState.reportedUsers[user.id] = true
                                    isReportedState = true
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = if (isReportedState) Color(0xFFFFA000) else Color(0xFFFFB300)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Filled.Report, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isReportedState) "Reported" else "Report Abuses", fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }
        }
    }
}
