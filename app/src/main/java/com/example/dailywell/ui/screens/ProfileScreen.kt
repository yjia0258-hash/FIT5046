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
import com.example.dailywell.viewmodel.ProfileState
import com.example.dailywell.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    userId: Int,
    onLogout: () -> Unit,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val currentUser by profileViewModel.currentUser.collectAsState()
    val name by profileViewModel.name.collectAsState()
    val reminderTime by profileViewModel.reminderTime.collectAsState()
    val reminderEnabled by profileViewModel.reminderEnabled.collectAsState()
    val reminderFrequency by profileViewModel.reminderFrequency.collectAsState()
    val wellbeingGoal by profileViewModel.wellbeingGoal.collectAsState()
    val breathingChecked by profileViewModel.breathingChecked.collectAsState()
    val sleepTipsChecked by profileViewModel.sleepTipsChecked.collectAsState()
    val activityPromptsChecked by profileViewModel.activityPromptsChecked.collectAsState()
    val profileState by profileViewModel.profileState.collectAsState()

    // Snackbar for save success/error feedback
    val snackbarHostState = remember { SnackbarHostState() }

    // Load user data on screen open
    LaunchedEffect(userId) {
        profileViewModel.loadUser(userId)
    }

    // Show snackbar on save result
    LaunchedEffect(profileState) {
        when (profileState) {
            is ProfileState.Success -> {
                snackbarHostState.showSnackbar("Preferences saved successfully.")
                profileViewModel.resetState()
            }
            is ProfileState.Error -> {
                snackbarHostState.showSnackbar((profileState as ProfileState.Error).message)
                profileViewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        containerColor = BackgroundLight,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Screen title
            Text(
                text = "Profile and Preferences",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            //  Profile summary card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // User avatar
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFDDDDEE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = PrimaryNavy,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentUser?.name ?: "Loading...",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = currentUser?.email ?: "",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }

                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Name input field
            // Guideline 3: Clear label above the field
            SectionLabel(text = "Name")
            OutlinedTextField(
                value = name,
                onValueChange = { profileViewModel.onNameChange(it) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = CardBackground,
                    focusedContainerColor = CardBackground,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = PrimaryNavy
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Reminder time input field
            SectionLabel(text = "Reminder Time")
            OutlinedTextField(
                value = reminderTime,
                onValueChange = { profileViewModel.onReminderTimeChange(it) },
                placeholder = { Text("e.g. 8:00 PM", color = TextSecondary) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = CardBackground,
                    focusedContainerColor = CardBackground,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = PrimaryNavy
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Enable Daily Reminders switch
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Enable Daily Reminders",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    // Switch component
                    Switch(
                        checked = reminderEnabled,
                        onCheckedChange = { profileViewModel.onReminderEnabledChange(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = PrimaryNavy
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            //  Support Preferences section
            ProfileSectionTitle(text = "Support Preferences")

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    // Checkbox 1: Breathing exercise suggestions
                    CheckboxRow(
                        label = "Breathing exercise suggestions",
                        checked = breathingChecked,
                        onCheckedChange = { profileViewModel.onBreathingCheckedChange(it) }
                    )
                    HorizontalDivider(color = Color(0xFFE0E0E8), thickness = 0.5.dp)

                    // Checkbox 2: Sleep improvement tips
                    CheckboxRow(
                        label = "Sleep improvement tips",
                        checked = sleepTipsChecked,
                        onCheckedChange = { profileViewModel.onSleepTipsCheckedChange(it) }
                    )
                    HorizontalDivider(color = Color(0xFFE0E0E8), thickness = 0.5.dp)

                    // Checkbox 3: Physical activity prompts
                    CheckboxRow(
                        label = "Physical activity prompts",
                        checked = activityPromptsChecked,
                        onCheckedChange = { profileViewModel.onActivityPromptsCheckedChange(it) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Reminder Frequency section
            ProfileSectionTitle(text = "Reminder Frequency")

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    // RadioButton options for reminder frequency
                    listOf("Daily", "Weekdays", "Twice a Week").forEach { option ->
                        RadioButtonRow(
                            label = option,
                            selected = reminderFrequency == option,
                            onClick = { profileViewModel.onReminderFrequencyChange(option) }
                        )
                        if (option != "Twice a Week") {
                            HorizontalDivider(color = Color(0xFFE0E0E8), thickness = 0.5.dp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            //  Primary Wellbeing Goal section
            ProfileSectionTitle(text = "Primary Wellbeing Goal")

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    // RadioButton options for wellbeing goal
                    listOf("Reduce Stress", "Improve Sleep").forEach { option ->
                        RadioButtonRow(
                            label = option,
                            selected = wellbeingGoal == option,
                            onClick = { profileViewModel.onWellbeingGoalChange(option) }
                        )
                        if (option != "Improve Sleep") {
                            HorizontalDivider(color = Color(0xFFE0E0E8), thickness = 0.5.dp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Account section
            ProfileSectionTitle(text = "Account")

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column {
                    AccountRow(
                        icon = Icons.Default.Person,
                        label = "Edit profile",
                        onClick = { /* TODO: navigate to edit profile */ }
                    )
                    HorizontalDivider(color = Color(0xFFE0E0E8), thickness = 0.5.dp)
                    AccountRow(
                        icon = Icons.Default.Lock,
                        label = "Change password",
                        onClick = { /* TODO: navigate to change password */ }
                    )
                    HorizontalDivider(color = Color(0xFFE0E0E8), thickness = 0.5.dp)
                    AccountRow(
                        icon = Icons.Default.Logout,
                        label = "Logout",
                        onClick = onLogout
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save Preferences button
            Button(
                onClick = { profileViewModel.savePreferences() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                enabled = profileState !is ProfileState.Loading
            ) {
                if (profileState is ProfileState.Loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        Icons.Default.Save,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Save Preferences",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

//  Section title
@Composable
fun ProfileSectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextPrimary
    )
}

// Checkbox row
@Composable
fun CheckboxRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = PrimaryNavy,
                uncheckedColor = TextSecondary
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, fontSize = 14.sp, color = TextPrimary)
    }
}

//  RadioButton row
@Composable
fun RadioButtonRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = PrimaryNavy)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, fontSize = 14.sp, color = TextPrimary)
    }
}

// Account row with icon and chevron
@Composable
fun AccountRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(0.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                fontSize = 14.sp,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary)
        }
    }
}