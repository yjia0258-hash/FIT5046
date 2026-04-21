package com.example.dailywell.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dailywell.viewmodel.HomeViewModel

// Accent colors for tip category icons
val TipPurple = Color(0xFF7B6FE0)
val TipGreen = Color(0xFF4CAF50)
val TipRed = Color(0xFFE57373)
val TipPurpleLight = Color(0xFFEEEBFB)
val TipGreenLight = Color(0xFFE8F5E9)
val TipRedLight = Color(0xFFFFEBEE)

@Composable
fun TipsScreen(
    userId: Int,
    onNavigateToHome: () -> Unit,
    homeViewModel: HomeViewModel = viewModel()
) {
    val recentEntries by homeViewModel.recentEntries.collectAsState()
    val supportPreview by homeViewModel.supportPreview.collectAsState()

    // Load recent data to generate personalised tips
    LaunchedEffect(userId) {
        homeViewModel.loadUser(userId)
        homeViewModel.loadHomeData()
    }

    // Calculate context values from recent entries
    val avgSleep = if (recentEntries.isNotEmpty())
        recentEntries.map { it.sleepDuration }.average() else 0.0
    val avgStress = if (recentEntries.isNotEmpty())
        recentEntries.map { it.stressScore }.average() else 0.0
    val avgActivity = if (recentEntries.isNotEmpty())
        recentEntries.map { it.activityLevel }.groupingBy { it }.eachCount()
            .maxByOrNull { it.value }?.key ?: "Unknown"
    else "Unknown"
    val avgMood = if (recentEntries.isNotEmpty())
        recentEntries.map { it.moodScore }.average() else 0.0

    // Generate personalised support cards based on context
    val personalisedTips = buildPersonalisedTips(avgSleep, avgStress, avgMood)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            //  Screen title
            Text(
                text = "Support Tips",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Personalised suggestions based on your recent wellbeing patterns.",
                fontSize = 13.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            //  Recent Summary card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(TipPurpleLight, shape = RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.ShowChart,
                                contentDescription = null,
                                tint = TipPurple,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Recent Summary",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Summary grid: 2x2 layout
                    Row(modifier = Modifier.fillMaxWidth()) {
                        SummaryChip(
                            icon = Icons.Default.SentimentSatisfied,
                            label = "Mood: ${getMoodLabel(avgMood)}",
                            modifier = Modifier.weight(1f)
                        )
                        SummaryChip(
                            icon = Icons.Default.DarkMode,
                            label = "Sleep: ${getSleepLabel(avgSleep)}",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        SummaryChip(
                            icon = Icons.Default.Favorite,
                            label = "Stress: ${getStressLabel(avgStress)}",
                            modifier = Modifier.weight(1f)
                        )
                        SummaryChip(
                            icon = Icons.Default.DirectionsRun,
                            label = "Activity: $avgActivity",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            //  Personalised Support section
            Text(
                text = "Personalised Support",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Context-aware personalised tip cards
            personalisedTips.forEach { tip ->
                PersonalisedTipCard(
                    icon = tip.icon,
                    iconBackground = tip.iconBackground,
                    iconTint = tip.iconTint,
                    message = tip.message
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            //  General Wellbeing Tips section
            Text(
                text = "General Wellbeing Tips",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Sleep tip
            GeneralTipCard(
                icon = Icons.Default.DarkMode,
                iconBackground = TipPurpleLight,
                iconTint = TipPurple,
                message = "Sleep better with a calming bedtime routine."
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Stress tip
            GeneralTipCard(
                icon = Icons.Default.SelfImprovement,
                iconBackground = Color(0xFFE3F2FD),
                iconTint = Color(0xFF1E88E5),
                message = "Manage stress with daily mindfulness."
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Activity tip
            GeneralTipCard(
                icon = Icons.Default.DirectionsWalk,
                iconBackground = TipGreenLight,
                iconTint = TipGreen,
                message = "Stay active with simple daily movement."
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Save Tip button
                Button(
                    onClick = { /* TODO: save tip to local */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy)
                ) {
                    Icon(
                        Icons.Default.BookmarkBorder,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save Tip", color = Color.White, fontSize = 14.sp)
                }

                // View Related Content button
                OutlinedButton(
                    onClick = { /* TODO: navigate to related content */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryNavy)
                ) {
                    Icon(
                        Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = PrimaryNavy,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("View Related Content", color = PrimaryNavy, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Back to Home link
            TextButton(
                onClick = onNavigateToHome,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Back to Home",
                    color = PrimaryNavy,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// Personalised tip card with icon and arrow
@Composable
fun PersonalisedTipCard(
    icon: ImageVector,
    iconBackground: Color,
    iconTint: Color,
    message: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(iconBackground, shape = RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                fontSize = 13.sp,
                color = TextPrimary,
                lineHeight = 18.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

//  General tip card (same layout, lighter style)
@Composable
fun GeneralTipCard(
    icon: ImageVector,
    iconBackground: Color,
    iconTint: Color,
    message: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(iconBackground, shape = RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                fontSize = 13.sp,
                color = TextPrimary,
                lineHeight = 18.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// Summary chip for Recent Summary card
@Composable
fun SummaryChip(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(vertical = 2.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = PrimaryNavy,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, fontSize = 12.sp, color = TextSecondary)
    }
}

// Data class for a personalised tip
data class TipItem(
    val icon: ImageVector,
    val iconBackground: Color,
    val iconTint: Color,
    val message: String
)

//Build personalised tips based on recent context
fun buildPersonalisedTips(
    avgSleep: Double,
    avgStress: Double,
    avgMood: Double
): List<TipItem> {
    val tips = mutableListOf<TipItem>()

    // Sleep-based tip
    if (avgSleep < 6.5) {
        tips.add(
            TipItem(
                icon = Icons.Default.DarkMode,
                iconBackground = TipPurpleLight,
                iconTint = TipPurple,
                message = "You have had lower sleep recently.\nTry a consistent bedtime tonight."
            )
        )
    }

    // Stress-based tip
    if (avgStress > 3.0) {
        tips.add(
            TipItem(
                icon = Icons.Default.Air,
                iconBackground = Color(0xFFE3F2FD),
                iconTint = Color(0xFF1E88E5),
                message = "Stress has been higher this week.\nA short breathing exercise may help."
            )
        )
    }

    // Mood-based tip
    if (avgMood < 3.0) {
        tips.add(
            TipItem(
                icon = Icons.Default.DirectionsWalk,
                iconBackground = TipGreenLight,
                iconTint = TipGreen,
                message = "Your activity level is lower than usual.\nA 10-minute walk could improve your energy."
            )
        )
    }

    // Default tip if everything is fine
    if (tips.isEmpty()) {
        tips.add(
            TipItem(
                icon = Icons.Default.Favorite,
                iconBackground = Color(0xFFFFEBEE),
                iconTint = Color(0xFFE57373),
                message = "You are doing well! Keep up your healthy routine."
            )
        )
    }

    return tips
}

// Helper label functions
fun getMoodLabel(avg: Double): String = when {
    avg >= 4.5 -> "Very Happy"
    avg >= 3.5 -> "Happy"
    avg >= 2.5 -> "Neutral"
    avg >= 1.5 -> "slightly low"
    avg > 0 -> "Low"
    else -> "No data"
}

fun getSleepLabel(avg: Double): String = when {
    avg >= 7.0 -> "sufficient"
    avg >= 5.5 -> "below usual"
    avg > 0 -> "low"
    else -> "No data"
}

fun getStressLabel(avg: Double): String = when {
    avg >= 4.0 -> "high"
    avg >= 2.5 -> "elevated"
    avg > 0 -> "low"
    else -> "No data"
}