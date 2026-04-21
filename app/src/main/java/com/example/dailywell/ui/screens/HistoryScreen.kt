package com.example.dailywell.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dailywell.data.model.WellbeingEntry
import com.example.dailywell.viewmodel.HistoryViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNavigateToEdit: (Int) -> Unit,
    historyViewModel: HistoryViewModel = viewModel()
) {
    val filteredEntries by historyViewModel.filteredEntries.collectAsState()
    val selectedMoodFilter by historyViewModel.selectedMoodFilter.collectAsState()
    val selectedActivityFilter by historyViewModel.selectedActivityFilter.collectAsState()
    val entryToDelete by historyViewModel.entryToDelete.collectAsState()

    // Dropdown expanded states for filters
    var moodFilterExpanded by remember { mutableStateOf(false) }
    var activityFilterExpanded by remember { mutableStateOf(false) }

    // Filter options
    val moodOptions = listOf("All", "Very Happy", "Happy", "Neutral", "Anxious", "Sad")
    val activityOptions = listOf("All", "Walking", "Running", "Gym", "Yoga", "Cycling", "Rest Day", "Other")

    // Delete confirmation dialog
    if (entryToDelete != null) {
        AlertDialog(
            onDismissRequest = { historyViewModel.onDeleteCancel() },
            title = { Text("Delete Record") },
            text = { Text("Are you sure you want to delete this record? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = { historyViewModel.deleteEntry(entryToDelete!!) }
                ) {
                    Text("Delete", color = ErrorRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { historyViewModel.onDeleteCancel() }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            //  Top header area
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Wellbeing History",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Review, edit, or delete previously saved records.",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }

                    // Filter icon button
                    IconButton(
                        onClick = { historyViewModel.resetFilters() }
                    ) {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = "Reset filters",
                            tint = PrimaryNavy
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                //  Filter chips row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Mood filter dropdown
                    ExposedDropdownMenuBox(
                        expanded = moodFilterExpanded,
                        onExpandedChange = { moodFilterExpanded = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        FilterChip(
                            selected = selectedMoodFilter != "All",
                            onClick = { moodFilterExpanded = true },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.SentimentSatisfied,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (selectedMoodFilter == "All") "Mood" else selectedMoodFilter,
                                        fontSize = 13.sp
                                    )
                                    Icon(
                                        Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            },
                            modifier = Modifier.menuAnchor(),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryNavy,
                                selectedLabelColor = Color.White,
                                selectedLeadingIconColor = Color.White
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = moodFilterExpanded,
                            onDismissRequest = { moodFilterExpanded = false }
                        ) {
                            moodOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        historyViewModel.onMoodFilterChange(option)
                                        moodFilterExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Activity filter dropdown
                    ExposedDropdownMenuBox(
                        expanded = activityFilterExpanded,
                        onExpandedChange = { activityFilterExpanded = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        FilterChip(
                            selected = selectedActivityFilter != "All",
                            onClick = { activityFilterExpanded = true },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.DirectionsRun,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (selectedActivityFilter == "All") "Activity" else selectedActivityFilter,
                                        fontSize = 13.sp
                                    )
                                    Icon(
                                        Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            },
                            modifier = Modifier.menuAnchor(),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryNavy,
                                selectedLabelColor = Color.White,
                                selectedLeadingIconColor = Color.White
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = activityFilterExpanded,
                            onDismissRequest = { activityFilterExpanded = false }
                        ) {
                            activityOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        historyViewModel.onActivityFilterChange(option)
                                        activityFilterExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Record list using LazyColumn
            if (filteredEntries.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.FolderOpen,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No records found.",
                            fontSize = 16.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "Start your first daily check-in!",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                }
            } else {
                // LazyColumn for wellbeing entries
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = filteredEntries,
                        key = { entry -> entry.id }
                    ) { entry ->
                        WellbeingEntryCard(
                            entry = entry,
                            onEditClick = { onNavigateToEdit(entry.id) },
                            onDeleteClick = { historyViewModel.onDeleteClick(entry) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

// Wellbeing entry card
@Composable
fun WellbeingEntryCard(
    entry: WellbeingEntry,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    // Parse date for display
    val displayDate = try {
        val date = LocalDate.parse(entry.date)
        Pair(
            date.dayOfMonth.toString(),
            date.format(DateTimeFormatter.ofPattern("MMM yyyy"))
        )
    } catch (e: Exception) {
        Pair("--", "--")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {

            //  Date block on the left
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFDDDDEE))
            ) {
                Column(
                    modifier = Modifier
                        .width(56.dp)
                        .padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = displayDate.first,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = displayDate.second,
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Entry details
            Column(modifier = Modifier.weight(1f)) {

                // Row 1: Mood + Stress
                Row(modifier = Modifier.fillMaxWidth()) {
                    EntryDetailItem(
                        icon = Icons.Default.SentimentSatisfied,
                        label = "Mood",
                        value = entry.mood,
                        modifier = Modifier.weight(1f)
                    )
                    EntryDetailItem(
                        icon = Icons.Default.BarChart,
                        label = "Stress",
                        value = entry.stressLevel,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Row 2: Sleep + Activity
                Row(modifier = Modifier.fillMaxWidth()) {
                    EntryDetailItem(
                        icon = Icons.Default.DarkMode,
                        label = "Sleep",
                        value = "${entry.sleepDuration}h",
                        modifier = Modifier.weight(1f)
                    )
                    EntryDetailItem(
                        icon = Icons.Default.DirectionsRun,
                        label = "Activity",
                        value = entry.activityType,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Notes preview (if available)
                if (entry.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            Icons.Default.Comment,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Note: ${entry.notes}",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Edit and Delete buttons
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onEditClick,
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryNavy),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Edit", fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = onDeleteClick,
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(ErrorRed)
                        ),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Delete", fontSize = 13.sp, color = ErrorRed)
                    }
                }
            }
        }
    }
}

//  Single detail item inside a card
@Composable
fun EntryDetailItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryNavy,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$label: ",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary
        )
        Text(
            text = value,
            fontSize = 12.sp,
            color = TextSecondary
        )
    }
}