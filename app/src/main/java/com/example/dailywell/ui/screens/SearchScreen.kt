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
import androidx.navigation.NavController
import com.example.dailywell.data.model.WellbeingEntry
import com.example.dailywell.data.remote.TipResponse
import com.example.dailywell.ui.components.BottomNavBar
import com.example.dailywell.viewmodel.ApiState
import com.example.dailywell.viewmodel.SearchViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    searchViewModel: SearchViewModel = viewModel()
) {
    val searchQuery by searchViewModel.searchQuery.collectAsState()
    val localResults by searchViewModel.localResults.collectAsState()
    val apiResults by searchViewModel.apiResults.collectAsState()
    val apiState by searchViewModel.apiState.collectAsState()
    val selectedMood by searchViewModel.selectedMood.collectAsState()
    val selectedActivity by searchViewModel.selectedActivity.collectAsState()

    var moodFilterExpanded by remember { mutableStateOf(false) }
    var activityFilterExpanded by remember { mutableStateOf(false) }

    val moodOptions = listOf("All", "Very Happy", "Happy", "Neutral", "Anxious", "Sad")
    val activityOptions = listOf("All", "Walking", "Running", "Gym", "Yoga", "Cycling", "Rest Day")

    Scaffold(
        containerColor = BackgroundLight,
        bottomBar = { BottomNavBar(navController = navController) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Screen title
            item {
                Text(text = "Search", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(text = "Search local records and external support content.", fontSize = 13.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Search bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchViewModel.onQueryChange(it) },
                    placeholder = { Text("Search by date, mood, or activity", color = TextSecondary) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchViewModel.resetSearch() }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextSecondary)
                            }
                        }
                    },
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
            }

            // Search Local / Search API buttons
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { searchViewModel.searchLocal() },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                        modifier = Modifier.weight(1f)
                    ) { Text("Search Local", fontSize = 14.sp, color = Color.White) }

                    Button(
                        onClick = { searchViewModel.searchApi() },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                        modifier = Modifier.weight(1f),
                        enabled = apiState !is ApiState.Loading
                    ) { Text("Search API", fontSize = 14.sp, color = Color.White) }
                }
            }

            // Filter chips: Mood + Activity
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ExposedDropdownMenuBox(expanded = moodFilterExpanded, onExpandedChange = { moodFilterExpanded = it }) {
                        FilterChip(
                            selected = selectedMood != "All",
                            onClick = { moodFilterExpanded = true },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = if (selectedMood == "All") "Mood" else selectedMood, fontSize = 13.sp)
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            },
                            modifier = Modifier.menuAnchor(),
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PrimaryNavy, selectedLabelColor = Color.White)
                        )
                        ExposedDropdownMenu(expanded = moodFilterExpanded, onDismissRequest = { moodFilterExpanded = false }) {
                            moodOptions.forEach { option ->
                                DropdownMenuItem(text = { Text(option) }, onClick = { searchViewModel.onMoodFilterChange(option); moodFilterExpanded = false })
                            }
                        }
                    }

                    ExposedDropdownMenuBox(expanded = activityFilterExpanded, onExpandedChange = { activityFilterExpanded = it }) {
                        FilterChip(
                            selected = selectedActivity != "All",
                            onClick = { activityFilterExpanded = true },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = if (selectedActivity == "All") "Activity" else selectedActivity, fontSize = 13.sp)
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            },
                            modifier = Modifier.menuAnchor(),
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PrimaryNavy, selectedLabelColor = Color.White)
                        )
                        ExposedDropdownMenu(expanded = activityFilterExpanded, onDismissRequest = { activityFilterExpanded = false }) {
                            activityOptions.forEach { option ->
                                DropdownMenuItem(text = { Text(option) }, onClick = { searchViewModel.onActivityFilterChange(option); activityFilterExpanded = false })
                            }
                        }
                    }
                }
            }

            // Local Records section header
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(36.dp).background(CardBackground, shape = RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Description, contentDescription = null, tint = PrimaryNavy, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "Local Records", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                }
            }

            // Local results
            if (localResults.isEmpty()) {
                item {
                    Text(text = "No local records found. Try searching above.", fontSize = 13.sp, color = TextSecondary, modifier = Modifier.padding(vertical = 8.dp))
                }
            } else {
                items(localResults, key = { it.id }) { entry -> LocalRecordItem(entry = entry) }
            }

            // External Support Content section header
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(36.dp).background(CardBackground, shape = RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Language, contentDescription = null, tint = PrimaryNavy, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "External Support Content", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    }
                    // Progress indicator while API is loading
                    if (apiState is ApiState.Loading) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = PrimaryNavy)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Loading...", fontSize = 12.sp, color = TextSecondary)
                        }
                    }
                }
            }

            // API error
            if (apiState is ApiState.Error) {
                item {
                    Text(text = (apiState as ApiState.Error).message, fontSize = 13.sp, color = ErrorRed, modifier = Modifier.padding(vertical = 4.dp))
                }
            }

            // API results
            if (apiResults.isEmpty() && apiState !is ApiState.Loading) {
                item {
                    Text(text = "Tap 'Search API' to load external wellbeing tips.", fontSize = 13.sp, color = TextSecondary, modifier = Modifier.padding(vertical = 8.dp))
                }
            } else {
                items(apiResults.take(5)) { tip -> ApiResultItem(tip = tip) }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

// Local record search result item
@Composable
fun LocalRecordItem(entry: WellbeingEntry) {
    val displayDate = try {
        LocalDate.parse(entry.date).format(DateTimeFormatter.ofPattern("d MMM yyyy"))
    } catch (e: Exception) { entry.date }

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = CardBackground), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = PrimaryNavy, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = "$displayDate · Mood: ${entry.mood} · Activity: ${entry.activityType}", fontSize = 13.sp, color = TextPrimary, modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary)
        }
    }
}

// External API tip result item
@Composable
fun ApiResultItem(tip: TipResponse) {
    val label = if (tip.q.length < 80) "Tip" else "Guide"

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = CardBackground), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.Top) {
            Box(modifier = Modifier.size(32.dp).background(Color(0xFFE8E8F5), shape = RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                Icon(if (label == "Tip") Icons.Default.Lightbulb else Icons.Default.MenuBook, contentDescription = null, tint = PrimaryNavy, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "$label:  ${tip.q}", fontSize = 13.sp, color = TextPrimary, lineHeight = 18.sp, maxLines = 3)
                if (tip.a.isNotBlank()) {
                    Text(text = "— ${tip.a}", fontSize = 11.sp, color = TextSecondary)
                }
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary)
        }
    }
}