package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GlobalState
import kotlinx.coroutines.delay

@Composable
fun AquaticToastNotification() {
    val message = GlobalState.activeToastMessage
    val isGreen = GlobalState.activeToastIsGreen

    LaunchedEffect(message) {
        if (message != null) {
            delay(3000)
            GlobalState.activeToastMessage = null
        }
    }

    AnimatedVisibility(
        visible = message != null,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = tween(durationMillis = 350)
        ) + fadeIn(animationSpec = tween(durationMillis = 250)),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(durationMillis = 350)
        ) + fadeOut(animationSpec = tween(durationMillis = 250)),
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        if (message != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(16.dp, RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFE0F7FA).copy(alpha = 0.95f),
                                Color(0xFFB2EBF2).copy(alpha = 0.95f)
                            )
                        )
                    )
                    .border(
                        width = 1.5.dp,
                        brush = Brush.horizontalGradient(
                            colors = if (isGreen) {
                                listOf(Color(0xFF00E676), Color(0xFF00E5FF))
                            } else {
                                listOf(Color(0xFF00E5FF), Color(0xFF2979FF))
                            }
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable { GlobalState.activeToastMessage = null }
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isGreen) Color(0xFF00E676).copy(alpha = 0.15f)
                                else Color(0xFF00B0FF).copy(alpha = 0.15f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isGreen) Icons.Default.CheckCircle else Icons.Default.Opacity,
                            contentDescription = null,
                            tint = if (isGreen) Color(0xFF00C853) else Color(0xFF0091EA),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isGreen) "Aqualyn Sync" else "Aqualyn Wave System",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF006064),
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = message,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF263238)
                        )
                    }
                }
            }
        }
    }
}
