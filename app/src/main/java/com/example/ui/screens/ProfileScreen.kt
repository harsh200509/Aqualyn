package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.GlobalState
import com.example.model.PostItem
import com.example.model.StoryItem
import kotlinx.coroutines.delay

@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onEditProfile: () -> Unit,
    onAddPost: () -> Unit = {}
) {
    // Dialog control states
    var showFollowersList by remember { mutableStateOf(false) }
    var showFollowingList by remember { mutableStateOf(false) }
    var showAddStory by remember { mutableStateOf(false) }
    var showStoryViewer by remember { mutableStateOf(false) }
    var activeStoryIndex by remember { mutableStateOf(0) }
    var showPostCreator by remember { mutableStateOf(false) }

    var activeSubTab by remember { mutableStateOf("Posts") }

    // Dummy followers/following models for dialog list
    val followersSampleList = remember {
        listOf(
            "ansh_7676" to "Ansh",
            "raj_1034" to "Raj",
            "mina_wave" to "Mina Aqua",
            "wave_enthusiast" to "Wave Hunter",
            "aquamarine_glow" to "Aqua Marine"
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F7F9))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF0091EA))
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Aqualyn",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        color = Color(0xFF0091EA),
                        letterSpacing = (-0.5).sp
                    )
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0091EA).copy(alpha = 0.15f))
                        .border(1.5.dp, Color(0xFF00B0FF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Person, contentDescription = null, tint = Color(0xFF0091EA), modifier = Modifier.size(20.dp))
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                item {
                    // Profile Header card with subtle light blue gradient
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFFE0F7FA), Color.Transparent)
                                )
                            ),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(top = 24.dp)
                        ) {
                            // Avatar
                            Box(contentAlignment = Alignment.BottomEnd) {
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.sweepGradient(
                                                colors = listOf(Color(0xFF00E5FF), Color(0xFF2979FF), Color(0xFF00E5FF))
                                            )
                                        )
                                        .padding(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .background(Color.White),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Filled.Person,
                                            contentDescription = null,
                                            tint = Color(0xFF0091EA),
                                            modifier = Modifier.size(60.dp)
                                        )
                                    }
                                }
                                // Edit badge
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .offset(x = (-4).dp, y = (-4).dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF0091EA))
                                        .border(2.dp, Color.White, CircleShape)
                                        .clickable { onEditProfile() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Filled.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Harsh", fontWeight = FontWeight.Bold, fontSize = 28.sp, color = Color(0xFF263238))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("@harsh_7742", fontSize = 15.sp, color = Color(0xFF546E7A))

                            Spacer(modifier = Modifier.height(24.dp))

                            // Interactive Stats
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 32.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { activeSubTab = "Posts" }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(GlobalState.posts.size.toString(), fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color(0xFF263238))
                                        Text("POSTS", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color(0xFF78909C))
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { showFollowersList = true }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("5", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color(0xFF263238))
                                        Text("FOLLOWERS", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color(0xFF78909C))
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { showFollowingList = true }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        val activeFollowingCount = GlobalState.followedUsers.values.count { it }
                                        Text(activeFollowingCount.toString(), fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color(0xFF263238))
                                        Text("FOLLOWING", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color(0xFF78909C))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Action Buttons
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Button(
                                    onClick = onEditProfile,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp),
                                    shape = RoundedCornerShape(24.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                    contentPadding = PaddingValues()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.horizontalGradient(
                                                    listOf(Color(0xFF0091EA), Color(0xFF637BFE))
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("Edit Profile", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Button(
                                    onClick = {
                                        GlobalState.showToast("Profile link copied to clipboard!", isGreen = true)
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp),
                                    shape = RoundedCornerShape(24.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = Color(0xFF263238)
                                    ),
                                    border = BorderStroke(1.dp, Color(0xFFECEFF1))
                                ) {
                                    Text("Share Profile", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                item {
                    // Circular Recent Stories Gallery
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 24.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.BurstMode, contentDescription = null, tint = Color(0xFF0091EA), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Recent Stories", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF263238))
                            }
                            Text(
                                text = "View All",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF0091EA),
                                modifier = Modifier.clickable {
                                    if (GlobalState.stories.isNotEmpty()) {
                                        activeStoryIndex = 0
                                        showStoryViewer = true
                                    } else {
                                        GlobalState.showToast("No active stories to view. Add one!", isGreen = false)
                                    }
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // "Add Story" Circular view option
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable { showAddStory = true }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(68.dp)
                                        .clip(CircleShape)
                                        .border(
                                            width = 2.dp,
                                            brush = Brush.sweepGradient(listOf(Color(0xFF00E5FF), Color(0xFF2979FF), Color(0xFF00E5FF))),
                                            shape = CircleShape
                                        )
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFE0F7FA)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Filled.Add, contentDescription = "Add Story", tint = Color(0xFF0091EA), modifier = Modifier.size(28.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Add Story", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF546E7A))
                            }

                            // Stories loop
                            GlobalState.stories.forEachIndexed { idx, story ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.clickable {
                                        activeStoryIndex = idx
                                        showStoryViewer = true
                                    }
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(68.dp)
                                            .clip(CircleShape)
                                            .border(
                                                width = 2.dp,
                                                brush = Brush.sweepGradient(
                                                    if (story.isViewed) {
                                                        listOf(Color(0xFFB0BEC5), Color(0xFFCFD8DC), Color(0xFFB0BEC5))
                                                    } else {
                                                        listOf(Color(0xFF00E5FF), Color(0xFF2979FF), Color(0xFF00E5FF))
                                                    }
                                                ),
                                                shape = CircleShape
                                            )
                                            .background(Color.White),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(56.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    Brush.verticalGradient(
                                                        colors = listOf(Color(0xFF00E5FF).copy(0.3f), Color(0xFF2979FF).copy(0.3f))
                                                    )
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Outlined.Water, contentDescription = null, tint = Color(0xFF0091EA), modifier = Modifier.size(24.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(story.title, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF263238))
                                }
                            }
                        }
                    }
                }

                item {
                    // Tab Headers Column
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f).clickable { activeSubTab = "Posts" }) {
                                TabItemWithUnderline("Posts", Icons.Outlined.GridView, activeSubTab == "Posts")
                            }
                            Box(modifier = Modifier.weight(1f).clickable { activeSubTab = "Saved" }) {
                                TabItemWithUnderline("Saved", Icons.Outlined.BookmarkBorder, activeSubTab == "Saved")
                            }
                            Box(modifier = Modifier.weight(1f).clickable { activeSubTab = "Collections" }) {
                                TabItemWithUnderline("Collections", Icons.Outlined.Folder, activeSubTab == "Collections")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Tab Contents
                        when (activeSubTab) {
                            "Posts" -> {
                                if (GlobalState.posts.isEmpty()) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(32.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(Icons.Outlined.PhotoCamera, null, modifier = Modifier.size(48.dp), tint = Color(0xFFB0BEC5))
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text("No posts published yet", fontSize = 14.sp, color = Color(0xFF78909C))
                                        Spacer(modifier = Modifier.height(8.dp))
                                        TextButton(onClick = { showPostCreator = true }) {
                                            Text("Publish your first post", color = Color(0xFF0091EA))
                                        }
                                    }
                                } else {
                                    GlobalState.posts.forEach { post ->
                                        PostCardView(
                                            post = post,
                                            isSaved = GlobalState.savedPostIds.contains(post.id),
                                            onLikeClick = {
                                                post.likesCount += 1
                                                GlobalState.showToast("Loved post! +1 Ripple energy wave.", isGreen = true)
                                            },
                                            onSaveClick = {
                                                if (GlobalState.savedPostIds.contains(post.id)) {
                                                    GlobalState.savedPostIds.remove(post.id)
                                                    GlobalState.showToast("Post removed from saved", isGreen = false)
                                                } else {
                                                    GlobalState.savedPostIds.add(post.id)
                                                    GlobalState.showToast("Post saved to collections successfully!", isGreen = true)
                                                }
                                            },
                                            onDeleteClick = {
                                                GlobalState.posts.remove(post)
                                                GlobalState.showToast("Post deleted", isGreen = false)
                                            }
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                    }
                                }
                            }

                            "Saved" -> {
                                val savedItemPosts = GlobalState.posts.filter { GlobalState.savedPostIds.contains(it.id) }
                                if (savedItemPosts.isEmpty()) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(32.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(Icons.Outlined.BookmarkBorder, null, modifier = Modifier.size(48.dp), tint = Color(0xFFB0BEC5))
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text("No saved posts", fontSize = 14.sp, color = Color(0xFF78909C))
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Save posts from the 'Posts' tab to find them here.", fontSize = 12.sp, color = Color(0xFF90A4AE), textAlign = TextAlign.Center)
                                    }
                                } else {
                                    savedItemPosts.forEach { post ->
                                        PostCardView(
                                            post = post,
                                            isSaved = true,
                                            onLikeClick = { post.likesCount += 1 },
                                            onSaveClick = {
                                                GlobalState.savedPostIds.remove(post.id)
                                                GlobalState.showToast("Removed from saved", isGreen = false)
                                            },
                                            onDeleteClick = {
                                                GlobalState.posts.remove(post)
                                                GlobalState.showToast("Post deleted", isGreen = false)
                                            }
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                    }
                                }
                            }

                            "Collections" -> {
                                // List of static aesthetic aqua folders
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    FolderCollectionRow("Aqua Reef Snaps", "5 items", Color(0xFF00E5FF))
                                    FolderCollectionRow("Bioluminescent Memories", "12 items", Color(0xFF637BFE))
                                    FolderCollectionRow("Ocean Currents", "2 items", Color(0xFF00B0FF))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Add Post FAB button at bottom center
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
                .shadow(12.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            color = Color.Transparent
        ) {
            Box(
                modifier = Modifier
                    .background(Brush.horizontalGradient(listOf(Color(0xFF0091EA), Color(0xFF637BFE))))
                    .clickable { showPostCreator = true }
                    .padding(horizontal = 28.dp, vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.AddCircle,
                        contentDescription = "Add Post",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Add Post",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }

        // ==================== DIALOGS ====================

        // 1. Followers List Dialog
        if (showFollowersList) {
            Dialog(onDismissRequest = { showFollowersList = false }) {
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Followers", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF263238))
                            IconButton(onClick = { showFollowersList = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Close")
                            }
                        }
                        Divider(modifier = Modifier.padding(vertical = 12.dp))

                        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            items(followersSampleList) { item ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF0091EA).copy(0.12f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(item.second.take(1), fontWeight = FontWeight.Bold, color = Color(0xFF0091EA))
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(item.second, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                            Text("@${item.first}", fontSize = 12.sp, color = Color(0xFF78909C))
                                        }
                                    }

                                    val isFollowing = GlobalState.followedUsers[item.first] == true
                                    TextButton(
                                        onClick = {
                                            GlobalState.followedUsers[item.first] = !isFollowing
                                            val followTxt = if (!isFollowing) "Followed" else "Unfollowed"
                                            GlobalState.showToast("$followTxt ${item.second}!", isGreen = !isFollowing)
                                        }
                                    ) {
                                        Text(
                                            text = if (isFollowing) "Following" else "Follow back",
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isFollowing) Color(0xFF78909C) else Color(0xFF0091EA)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Following List Dialog
        if (showFollowingList) {
            Dialog(onDismissRequest = { showFollowingList = false }) {
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Following", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF263238))
                            IconButton(onClick = { showFollowingList = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Close")
                            }
                        }
                        Divider(modifier = Modifier.padding(vertical = 12.dp))

                        val activeFollowing = followersSampleList.filter { com.example.model.GlobalState.followedUsers[it.first] == true }
                        if (activeFollowing.isEmpty()) {
                            Text(
                                "You are not following anyone yet.",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                textAlign = TextAlign.Center,
                                color = Color(0xFF78909C)
                            )
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                items(activeFollowing) { item ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFF0091EA).copy(0.12f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(item.second.take(1), fontWeight = FontWeight.Bold, color = Color(0xFF0091EA))
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Text(item.second, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                                Text("@${item.first}", fontSize = 12.sp, color = Color(0xFF78909C))
                                            }
                                        }

                                        Button(
                                            onClick = {
                                                GlobalState.followedUsers[item.first] = false
                                                GlobalState.showToast("Unfollowed ${item.second}.", isGreen = false)
                                            },
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFECEFF1), contentColor = Color(0xFF546E7A)),
                                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 2.dp),
                                            modifier = Modifier.height(32.dp)
                                        ) {
                                            Text("Unfollow", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Instagram-style Video/Aqua Story Viewer Dialog
        if (showStoryViewer) {
            val story = GlobalState.stories.getOrNull(activeStoryIndex)
            if (story != null) {
                // Mark as viewed
                LaunchedEffect(activeStoryIndex) {
                    val index = GlobalState.stories.indexOfFirst { it.id == story.id }
                    if (index != -1) {
                        GlobalState.stories[index] = GlobalState.stories[index].copy(isViewed = true)
                    }
                }

                Dialog(onDismissRequest = { showStoryViewer = false }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(0.65f)
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFF006064), Color(0xFF001122))
                                )
                            )
                    ) {
                        // Drawing flowing waves simulation background
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(Color(0xFF00E5FF).copy(0.18f), Color.Transparent)
                                    )
                                )
                        )

                        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                            // Stories progress bar indicator
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                GlobalState.stories.forEachIndexed { idx, _ ->
                                    val progress = remember { Animatable(0f) }
                                    LaunchedEffect(activeStoryIndex) {
                                        if (idx < activeStoryIndex) {
                                            progress.snapTo(1f)
                                        } else if (idx == activeStoryIndex) {
                                            animationStory(progress, onDone = {
                                                if (activeStoryIndex < GlobalState.stories.size - 1) {
                                                    activeStoryIndex += 1
                                                } else {
                                                    showStoryViewer = false
                                                }
                                            })
                                        } else {
                                            progress.snapTo(0f)
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(3.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(0.35f))
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .fillMaxWidth(progress.value)
                                                .background(Color(0xFF00E5FF))
                                        )
                                    }
                                }
                            }

                            // Header details
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Filled.Person, null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text("@harsh_7742", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                                        Text(story.title, color = Color(0xFF00E5FF), fontWeight = FontWeight.Medium, fontSize = 11.sp)
                                    }
                                }

                                IconButton(onClick = { showStoryViewer = false }) {
                                    Icon(Icons.Default.Close, contentDescription = "Close Story", tint = Color.White)
                                }
                            }

                            Spacer(modifier = Modifier.weight(0.7f))

                            // Immersive Story content box
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.08f)),
                                border = BorderStroke(1.dp, Color.White.copy(0.15f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Outlined.Water, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(48.dp))
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        "Simulated Story Screen",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 18.sp
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "Theme: ${story.title}\nEnjoying the crisp, fluid Aqualyn application mechanics. Pure aquatic peace.",
                                        color = Color.White.copy(0.8f),
                                        fontSize = 13.sp,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 18.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            // Bottom Navigation inside story
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(24.dp))
                                        .background(Color.White.copy(alpha = 0.1f))
                                        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Send, contentDescription = null, tint = Color.White.copy(0.8f), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Reply to Harsh...", color = Color.White.copy(0.7f), fontSize = 13.sp)
                                }
                                
                                Spacer(modifier = Modifier.width(12.dp))
                                IconButton(onClick = {
                                    GlobalState.showToast("Story loved! Ripple sent to creator.", isGreen = true)
                                }) {
                                    Icon(Icons.Filled.Favorite, contentDescription = "Like story", tint = Color.Red, modifier = Modifier.size(28.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Create Story Dialogue
        if (showAddStory) {
            var selectedStoryOption by remember { mutableStateOf("Fresh Aqua Stream") }
            val optionsList = listOf("Fresh Aqua Stream", "Tidal Lagoon Dusk", "Silent Abyss Blue", "Deep Sea bioluminescent")

            Dialog(onDismissRequest = { showAddStory = false }) {
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Share Aqua Story", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF263238))
                            IconButton(onClick = { showAddStory = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Close")
                            }
                        }
                        Divider(modifier = Modifier.padding(vertical = 12.dp))

                        Text("Select ocean vibe:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF546E7A))
                        Spacer(modifier = Modifier.height(10.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            optionsList.forEach { opt ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (selectedStoryOption == opt) Color(0xFFE0F7FA) else Color(0xFFF4F7F9))
                                        .border(1.dp, if (selectedStoryOption == opt) Color(0xFF00B0FF) else Color.Transparent, RoundedCornerShape(12.dp))
                                        .clickable { selectedStoryOption = opt }
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Water,
                                        contentDescription = null,
                                        tint = if (selectedStoryOption == opt) Color(0xFF0091EA) else Color(0xFF78909C)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = opt,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selectedStoryOption == opt) Color(0xFF006064) else Color(0xFF37474F)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = {
                                GlobalState.stories.add(StoryItem(System.currentTimeMillis().toString(), selectedStoryOption, false, true))
                                showAddStory = false
                                GlobalState.showToast("Story '$selectedStoryOption' published successfully!", isGreen = true)
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0091EA))
                        ) {
                            Text("Post Story", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }
        }

        // 5. Instagram-like Fully Functional Post Creator Dialogue
        if (showPostCreator) {
            var inputPostCaption by remember { mutableStateOf("") }
            var isCommentsEnabledState by remember { mutableStateOf(true) }
            var selectedThemeImageDesc by remember { mutableStateOf("Ocean wave bubble close-up") }
            var postLocationTag by remember { mutableStateOf("Bioluminescent Cave, Maldives") }
            var shareToOtherNetworkState by remember { mutableStateOf(false) }

            val mockImageOptionsList = listOf(
                "Ocean wave bubble close-up",
                "Pacific depths sun beam spectrum",
                "Mineral spring waterfall",
                "Golden sunset lagoon reflection"
            )

            Dialog(onDismissRequest = { showPostCreator = false }) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("New Post", fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color(0xFF263238))
                            IconButton(onClick = { showPostCreator = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Close")
                            }
                        }
                        Divider(modifier = Modifier.padding(vertical = 12.dp))

                        // Simulate Instagram square photo preview
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1.2f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color(0xFF00B0FF), Color(0xFF006064))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                                Icon(Icons.Outlined.PhotoSizeSelectActual, contentDescription = null, tint = Color.White.copy(0.9f), modifier = Modifier.size(40.dp))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = selectedThemeImageDesc,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    "Simulated Aquatic Photo Asset",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))

                        // Selection list for simulated photos
                        Text("Pick simulated shot description:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF78909C))
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            mockImageOptionsList.forEach { shot ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (selectedThemeImageDesc == shot) Color(0xFF00E5FF).copy(0.12f) else Color(0xFFF4F7F9))
                                        .border(1.dp, if (selectedThemeImageDesc == shot) Color(0xFF00B0FF) else Color.Transparent, RoundedCornerShape(12.dp))
                                        .clickable { selectedThemeImageDesc = shot }
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text(shot, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Caption block
                        Text("Caption", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF546E7A))
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = inputPostCaption,
                            onValueChange = { inputPostCaption = it },
                            placeholder = { Text("Write a caption...") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Location block
                        Text("Add Location", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF546E7A))
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = postLocationTag,
                            onValueChange = { postLocationTag = it },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF0091EA)) },
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Advanced sliders/switches (instagram options)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Allow Comments", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("Let people write on your post", color = Color(0xFF78909C), fontSize = 11.sp)
                            }
                            Switch(
                                checked = isCommentsEnabledState,
                                onCheckedChange = { isCommentsEnabledState = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E5FF), checkedTrackColor = Color(0xFF0091EA))
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Aqualyn Matrix Broadcast", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("Mirror post directly across all tabs", color = Color(0xFF78909C), fontSize = 11.sp)
                            }
                            Switch(
                                checked = shareToOtherNetworkState,
                                onCheckedChange = { shareToOtherNetworkState = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E5FF), checkedTrackColor = Color(0xFF0091EA))
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                val generatedCaption = inputPostCaption.ifBlank { "Unwinding under refreshing flows of water aura." }
                                val newCreatedPost = PostItem(
                                    id = System.currentTimeMillis().toString(),
                                    imageDescription = selectedThemeImageDesc,
                                    caption = generatedCaption,
                                    likesCount = 0,
                                    timeAgo = "Just now",
                                    isCommentsDisabled = !isCommentsEnabledState,
                                    location = postLocationTag
                                )
                                GlobalState.posts.add(0, newCreatedPost)
                                showPostCreator = false
                                GlobalState.showToast("Aqua Post created successfully!", isGreen = true)
                            },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0091EA))
                        ) {
                            Text("Publish Post", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PostCardView(
    post: PostItem,
    isSaved: Boolean,
    onLikeClick: () -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(20.dp)),
        border = BorderStroke(1.dp, Color(0xFFECEFF1))
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0091EA)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("HA", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Harsh", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        if (post.location.isNotEmpty()) {
                            Text(post.location, fontSize = 11.sp, color = Color(0xFF0091EA))
                        } else {
                            Text(post.timeAgo, fontSize = 11.sp, color = Color(0xFF78909C))
                        }
                    }
                }

                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF5350))
                }
            }

            // Visual Simulation Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF80DEEA), Color(0xFF00838F))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                    Icon(Icons.Outlined.Photo, contentDescription = null, tint = Color.White.copy(0.9f), modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = post.imageDescription,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        fontSize = 15.sp
                    )
                }
            }

            // Interactive Controls row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onLikeClick) {
                        Icon(Icons.Default.Favorite, contentDescription = "Like", tint = Color.Red)
                    }
                    Text(
                        post.likesCount.toString(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF263238)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    if (!post.isCommentsDisabled) {
                        IconButton(onClick = {
                            GlobalState.showToast("Comments section displayed! All crisp wave responses.", isGreen = true)
                        }) {
                            Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = "Comment", tint = Color(0xFF546E7A))
                        }
                        Text("3", fontSize = 13.sp, color = Color(0xFF263238), fontWeight = FontWeight.Bold)
                    } else {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Outlined.CommentsDisabled, null, tint = Color(0xFFB0BEC5), modifier = Modifier.size(16.dp))
                        Text("Disabled", fontSize = 11.sp, color = Color(0xFFB0BEC5), modifier = Modifier.padding(start = 4.dp))
                    }
                }

                IconButton(onClick = onSaveClick) {
                    Icon(
                        imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Save",
                        tint = if (isSaved) Color(0xFF0091EA) else Color(0xFF546E7A)
                    )
                }
            }

            // Caption details
            if (post.caption.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 12.dp, bottom = 16.dp)
                ) {
                    Text(
                        text = "Harsh: ${post.caption}",
                        fontSize = 13.sp,
                        color = Color(0xFF37474F),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun FolderCollectionRow(name: String, itemsCount: String, blockColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFECEFF1), RoundedCornerShape(16.dp))
            .clickable {
                GlobalState.showToast("Loaded collection: $name", isGreen = true)
            }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(blockColor.copy(0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.FolderOpen, contentDescription = null, tint = blockColor, modifier = Modifier.size(24.dp))
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF263238))
            Text(itemsCount, fontSize = 12.sp, color = Color(0xFF78909C))
        }

        Icon(Icons.Outlined.ArrowForwardIos, contentDescription = null, tint = Color(0xFF90A4AE), modifier = Modifier.size(14.dp))
    }
}

private suspend fun animationStory(anim: Animatable<Float, AnimationVector1D>, onDone: () -> Unit) {
    anim.animateTo(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 3500, easing = LinearEasing)
    )
    onDone()
}

@Composable
fun TabItemWithUnderline(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, isSelected: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) Color(0xFF0091EA) else Color(0xFF78909C),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color(0xFF0091EA) else Color(0xFF78909C),
                fontSize = 14.sp
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .height(2.dp)
                .fillMaxWidth()
                .background(if (isSelected) Color(0xFF0091EA) else Color.Transparent)
        )
    }
}
