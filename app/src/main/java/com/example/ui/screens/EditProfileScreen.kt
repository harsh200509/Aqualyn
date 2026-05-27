package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EditProfileScreen(onBack: () -> Unit, onSave: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F7F9))
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFF546E7A),
                modifier = Modifier.clickable { onBack() }
            )
            Spacer(modifier = Modifier.width(24.dp))
            Text(
                "Edit Profile",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFF263238),
                modifier = Modifier.weight(1f)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onSave() }
            ) {
                Icon(Icons.Filled.Save, contentDescription = null, tint = Color(0xFF0D47A1), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Save", fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1), fontSize = 16.sp)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Avatar
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.Black)
                        .border(4.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(64.dp))
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            EditFieldGroup(label = "Display Name", value = "Harsh")
            Spacer(modifier = Modifier.height(24.dp))

            EditFieldGroup(label = "Username", value = "@harsh_7742")
            Text(
                "You can choose a unique username on Aqualyn (lowercase and underscores only). People will be able to find you securely without needing your phone number.",
                color = Color(0xFF78909C),
                fontSize = 12.sp,
                lineHeight = 16.sp,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )

            EditFieldGroup(label = "Role / Title", value = "")
            Spacer(modifier = Modifier.height(24.dp))

            EditFieldGroup(label = "About", value = "Hey there! I am using Aqualyn.", isMultiline = true)
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun EditFieldGroup(label: String, value: String, isMultiline: Boolean = false) {
    Column {
        Text(label, fontWeight = FontWeight.Bold, color = Color(0xFF546E7A), fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isMultiline) 100.dp else 56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = if (isMultiline) 16.dp else 0.dp),
            contentAlignment = if (isMultiline) Alignment.TopStart else Alignment.CenterStart
        ) {
            if (value.isEmpty()) {
                // Empty state handled naturally
            } else {
                Text(value, fontSize = 16.sp, color = if(value.isEmpty()) Color(0xFFB0BEC5) else Color(0xFF263238))
            }
        }
    }
}
