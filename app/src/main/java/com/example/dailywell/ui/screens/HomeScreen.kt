package com.example.dailywell.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dailywell.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    userId: Int,
    onNavigateToCheckIn: () -> Unit,
    onNavigateToTips: () -> Unit,
    homeViewModel: HomeViewModel = viewModel()
) {
    val currentUser by homeViewModel.currentUser.collectAsState()
    val todayEntry by homeViewModel.todayEntry.collectAsState()
    val isTodayCompleted by homeViewModel.isTodayCompleted.collectAsState()
    val supportPreview by homeViewModel.supportPreview.collectAsState()

    LaunchedEffect(userId) {
        homeViewModel.loadUser(userId)
        homeViewModel.loadHomeData()
    }

    Scaffold(
        containerColor = BackgroundLight
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ── 顶部问候栏 ────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(CardBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = PrimaryNavy,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "${homeViewModel.greeting}, ${currentUser?.name ?: ""}",
                        fontSize = 16.sp,
                        color = TextSecondary
                    )
                }
                IconButton(onClick = { }) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = PrimaryNavy
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── 主标题 ────────────────────────────────────
            Text(
                text = "Wellbeing Dashboard",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Summary of your recent status and reminder plan.",
                fontSize = 13.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── Today's Summary 卡片 ──────────────────────
            HomeCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.BarChart, contentDescription = null, tint = PrimaryNavy)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Today's Summary",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                if (todayEntry != null) {
                    SummaryRow(
                        icon = Icons.Default.SentimentSatisfied,
                        label = "Mood",
                        value = todayEntry!!.mood
                    )
                    SummaryDivider()
                    SummaryRow(
                        icon = Icons.Default.Waves,
                        label = "Stress",
                        value = todayEntry!!.stressLevel
                    )
                    SummaryDivider()
                    SummaryRow(
                        icon = Icons.Default.DarkMode,
                        label = "Sleep",
                        value = "${todayEntry!!.sleepDuration} hours"
                    )
                    SummaryDivider()
                    SummaryRow(
                        icon = Icons.Default.DirectionsRun,
                        label = "Activity",
                        value = todayEntry!!.activityLevel
                    )
                } else {
                    Text(
                        text = "No check-in recorded today yet.",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Reminder 卡片 ─────────────────────────────
            HomeCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = PrimaryNavy)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Reminder",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                SummaryRow(
                    icon = Icons.Default.CheckCircle,
                    label = "Daily check-in",
                    value = if (isTodayCompleted) "Completed ✓" else "Not completed yet"
                )
                SummaryDivider()
                SummaryRow(
                    icon = Icons.Default.CalendarMonth,
                    label = "Next reminder",
                    value = currentUser?.reminderTime ?: "8:00 PM"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Support Preview 卡片 ──────────────────────
            HomeCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Favorite, contentDescription = null, tint = PrimaryNavy)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Support Preview",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = supportPreview,
                    fontSize = 14.sp,
                    color = TextSecondary,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Start Daily Check-in 按钮 ─────────────────
            Button(
                onClick = onNavigateToCheckIn,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy)
            ) {
                Text(
                    text = "Start Daily Check-in",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── View Support Tips 按钮 ────────────────────
            Button(
                onClick = onNavigateToTips,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy)
            ) {
                Text(
                    text = "View Support Tips",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ── 可复用卡片 ────────────────────────────────────────────
@Composable
fun HomeCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

// ── 摘要行 ────────────────────────────────────────────────
@Composable
fun SummaryRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryNavy,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "$label: ",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = TextSecondary
        )
    }
}

// ── 分隔线 ────────────────────────────────────────────────
@Composable
fun SummaryDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 2.dp),
        color = Color(0xFFE0E0E8),
        thickness = 0.5.dp
    )
}