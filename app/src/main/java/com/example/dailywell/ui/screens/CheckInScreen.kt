package com.example.dailywell.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dailywell.viewmodel.CheckInState
import com.example.dailywell.viewmodel.CheckInViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckInScreen(
    entryId: Int? = null,
    onSaveSuccess: () -> Unit,
    onCancel: () -> Unit,
    checkInViewModel: CheckInViewModel = viewModel()
) {
    // Collect all form states from ViewModel
    val selectedDate by checkInViewModel.selectedDate.collectAsState()
    val mood by checkInViewModel.mood.collectAsState()
    val stressLevel by checkInViewModel.stressLevel.collectAsState()
    val sleepDuration by checkInViewModel.sleepDuration.collectAsState()
    val activityLevel by checkInViewModel.activityLevel.collectAsState()
    val activityType by checkInViewModel.activityType.collectAsState()
    val notes by checkInViewModel.notes.collectAsState()
    val checkInState by checkInViewModel.checkInState.collectAsState()

    // Inline error states
    val moodError by checkInViewModel.moodError.collectAsState()
    val stressError by checkInViewModel.stressError.collectAsState()
    val sleepError by checkInViewModel.sleepError.collectAsState()
    val activityLevelError by checkInViewModel.activityLevelError.collectAsState()

    // Dropdown expanded states
    var moodExpanded by remember { mutableStateOf(false) }
    var stressExpanded by remember { mutableStateOf(false) }
    var activityLevelExpanded by remember { mutableStateOf(false) }
    var activityTypeExpanded by remember { mutableStateOf(false) }

    // DatePicker dialog state
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    // Dropdown options
    val moodOptions = listOf("Very Happy", "Happy", "Neutral", "Anxious", "Sad")
    val stressOptions = listOf("Low", "Moderate", "High")
    val activityLevelOptions = listOf("Low", "Moderate", "High")
    val activityTypeOptions = listOf("Walking", "Running", "Gym", "Yoga", "Cycling", "Rest Day", "Other")

    // Load existing entry if editing
    LaunchedEffect(entryId) {
        if (entryId != null) {
            checkInViewModel.loadEntry(entryId)
        }
    }

    // Handle save success
    LaunchedEffect(checkInState) {
        if (checkInState is CheckInState.Success) {
            onSaveSuccess()
            checkInViewModel.resetState()
        }
    }

    // DatePicker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = java.time.Instant.ofEpochMilli(millis)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                        checkInViewModel.onDateChange(
                            date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        )
                    }
                    showDatePicker = false
                }) { Text("OK", color = PrimaryNavy) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

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

            // Screen title
            Text(
                text = "Daily Check-in",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Record your emotional and physical wellbeing for today.",
                fontSize = 13.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Check-in Date field with DatePicker
            // Guideline 3: Clear label above the field
            SectionLabel(text = "Check-in Date")
            OutlinedTextField(
                value = formatDisplayDate(selectedDate),
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = "Select date",
                            tint = PrimaryNavy
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = CardBackground,
                    focusedContainerColor = CardBackground,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = PrimaryNavy
                )
            )

            TextButton(onClick = { showDatePicker = true }) {
                Text("Select date", color = PrimaryNavy, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            //
            // Section 1: Emotional Wellbeing
            // Guideline 4: Related inputs grouped in sections
            //
            SectionDivider(title = "Emotional Wellbeing")

            Spacer(modifier = Modifier.height(12.dp))

            // Mood Level dropdown
            // Guideline 3: Clear label above dropdown
            SectionLabel(text = "Mood Level")
            ExposedDropdownMenuBox(
                expanded = moodExpanded,
                onExpandedChange = { moodExpanded = it }
            ) {
                OutlinedTextField(
                    value = mood,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Mood Level", color = TextSecondary) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = moodExpanded)
                    },
                    // Guideline 5: Inline error when field is empty
                    isError = moodError != null,
                    supportingText = {
                        if (moodError != null) {
                            Text(moodError!!, color = ErrorRed, fontSize = 12.sp)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = CardBackground,
                        focusedContainerColor = CardBackground,
                        unfocusedBorderColor = if (moodError != null) ErrorRed else Color.Transparent,
                        focusedBorderColor = if (moodError != null) ErrorRed else PrimaryNavy,
                        errorBorderColor = ErrorRed
                    )
                )
                ExposedDropdownMenu(
                    expanded = moodExpanded,
                    onDismissRequest = { moodExpanded = false }
                ) {
                    moodOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                checkInViewModel.onMoodChange(option)
                                moodExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            //  Stress Level dropdown
            SectionLabel(text = "Stress Level")
            ExposedDropdownMenuBox(
                expanded = stressExpanded,
                onExpandedChange = { stressExpanded = it }
            ) {
                OutlinedTextField(
                    value = stressLevel,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Stress Level", color = TextSecondary) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = stressExpanded)
                    },
                    isError = stressError != null,
                    supportingText = {
                        if (stressError != null) {
                            Text(stressError!!, color = ErrorRed, fontSize = 12.sp)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = CardBackground,
                        focusedContainerColor = CardBackground,
                        unfocusedBorderColor = if (stressError != null) ErrorRed else Color.Transparent,
                        focusedBorderColor = if (stressError != null) ErrorRed else PrimaryNavy,
                        errorBorderColor = ErrorRed
                    )
                )
                ExposedDropdownMenu(
                    expanded = stressExpanded,
                    onDismissRequest = { stressExpanded = false }
                ) {
                    stressOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                checkInViewModel.onStressChange(option)
                                stressExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            //
            // Section 2: Physical Wellbeing
            // Guideline 4: Separate section for physical inputs
            //
            SectionDivider(title = "Physical Wellbeing")

            Spacer(modifier = Modifier.height(12.dp))

            // Sleep Duration input
            SectionLabel(text = "Sleep Duration (Hours)")
            OutlinedTextField(
                value = sleepDuration,
                onValueChange = { checkInViewModel.onSleepChange(it) },
                placeholder = { Text("Sleep Duration (Hours)", color = TextSecondary) },
                isError = sleepError != null,
                supportingText = {
                    if (sleepError != null) {
                        Text(sleepError!!, color = ErrorRed, fontSize = 12.sp)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = CardBackground,
                    focusedContainerColor = CardBackground,
                    unfocusedBorderColor = if (sleepError != null) ErrorRed else Color.Transparent,
                    focusedBorderColor = if (sleepError != null) ErrorRed else PrimaryNavy,
                    errorBorderColor = ErrorRed
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Activity Level dropdown
            SectionLabel(text = "Activity Level")
            ExposedDropdownMenuBox(
                expanded = activityLevelExpanded,
                onExpandedChange = { activityLevelExpanded = it }
            ) {
                OutlinedTextField(
                    value = activityLevel,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Activity Level", color = TextSecondary) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = activityLevelExpanded)
                    },
                    isError = activityLevelError != null,
                    supportingText = {
                        if (activityLevelError != null) {
                            Text(activityLevelError!!, color = ErrorRed, fontSize = 12.sp)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = CardBackground,
                        focusedContainerColor = CardBackground,
                        unfocusedBorderColor = if (activityLevelError != null) ErrorRed else Color.Transparent,
                        focusedBorderColor = if (activityLevelError != null) ErrorRed else PrimaryNavy,
                        errorBorderColor = ErrorRed
                    )
                )
                ExposedDropdownMenu(
                    expanded = activityLevelExpanded,
                    onDismissRequest = { activityLevelExpanded = false }
                ) {
                    activityLevelOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                checkInViewModel.onActivityLevelChange(option)
                                activityLevelExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Activity Type dropdown
            SectionLabel(text = "Activity Type")
            ExposedDropdownMenuBox(
                expanded = activityTypeExpanded,
                onExpandedChange = { activityTypeExpanded = it }
            ) {
                OutlinedTextField(
                    value = activityType,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Activity Type", color = TextSecondary) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = activityTypeExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = CardBackground,
                        focusedContainerColor = CardBackground,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = PrimaryNavy
                    )
                )
                ExposedDropdownMenu(
                    expanded = activityTypeExpanded,
                    onDismissRequest = { activityTypeExpanded = false }
                ) {
                    activityTypeOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                checkInViewModel.onActivityTypeChange(option)
                                activityTypeExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            //
            // Section 3: Notes
            //
            SectionDivider(title = "Notes")

            Spacer(modifier = Modifier.height(12.dp))

            // Additional notes text box
            OutlinedTextField(
                value = notes,
                onValueChange = { checkInViewModel.onNotesChange(it) },
                placeholder = { Text("Additional Notes (Optional)", color = TextSecondary) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp),
                maxLines = 5,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = CardBackground,
                    focusedContainerColor = CardBackground,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = PrimaryNavy
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            //  Save Record button
            Button(
                onClick = { checkInViewModel.saveEntry() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                enabled = checkInState !is CheckInState.Loading
            ) {
                if (checkInState is CheckInState.Loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Save Record",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            //  Clear button
            OutlinedButton(
                onClick = {
                    checkInViewModel.clearForm()
                    onCancel()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryNavy),
                border = ButtonDefaults.outlinedButtonBorder
            ) {
                Text(
                    text = "Clear",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryNavy
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// Section title with horizontal divider
@Composable
fun SectionDivider(title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.width(12.dp))
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = Color(0xFFE0E0E8),
            thickness = 1.dp
        )
    }
}

//  Field label above each input
// Guideline 3: Clear labels instead of relying only on placeholders
@Composable
fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = TextSecondary,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

//  Format date string for display
// Converts "2026-04-20" to "20 Apr 2026"
fun formatDisplayDate(dateStr: String): String {
    return try {
        val date = LocalDate.parse(dateStr)
        date.format(DateTimeFormatter.ofPattern("d MMM yyyy"))
    } catch (e: Exception) {
        dateStr
    }
}