package com.example.dailywell.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dailywell.ui.components.BottomNavBar
import com.example.dailywell.viewmodel.ChartDataPoint
import com.example.dailywell.viewmodel.ChartViewModel
import com.example.dailywell.viewmodel.WeeklySummary

// Chart colors matching UI design
val MoodGreen = Color(0xFF4CAF50)
val StressOrange = Color(0xFFFFA726)
val SleepBlue = Color(0xFF42A5F5)

@Composable
fun ChartScreen(
    navController: NavController,
    chartViewModel: ChartViewModel = viewModel()
) {
    val chartData by chartViewModel.chartData.collectAsState()
    val weeklySummary by chartViewModel.weeklySummary.collectAsState()
    val insights by chartViewModel.insights.collectAsState()
    val dateRangeLabel by chartViewModel.dateRangeLabel.collectAsState()

    Scaffold(
        containerColor = BackgroundLight,
        bottomBar = { BottomNavBar(navController = navController) }
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
            Text(text = "Weekly Report", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(text = "Visual summary of mood, stress, sleep, and activity trends.", fontSize = 13.sp, color = TextSecondary)

            Spacer(modifier = Modifier.height(16.dp))

            // Week navigation: prev / date range / next
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = CardBackground), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)) {
                Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { chartViewModel.previousWeek() }) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Previous week", tint = PrimaryNavy)
                    }
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = PrimaryNavy, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = dateRangeLabel, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    IconButton(onClick = { chartViewModel.nextWeek() }) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Next week", tint = PrimaryNavy)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Summary cards row
            weeklySummary?.let { summary ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SummaryStatCard(icon = "😊", label = "Average Mood", value = String.format("%.1f", summary.avgMood), unit = "/ 5", subLabel = summary.avgMoodLabel, valueColor = MoodGreen, modifier = Modifier.weight(1f))
                    SummaryStatCard(icon = "〜", label = "Average Stress", value = String.format("%.1f", summary.avgStress), unit = "/ 5", subLabel = summary.avgStressLabel, valueColor = StressOrange, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SummaryStatCard(icon = "🌙", label = "Average Sleep", value = String.format("%.1f", summary.avgSleep), unit = "hrs", subLabel = summary.avgSleepLabel, valueColor = SleepBlue, modifier = Modifier.weight(1f))
                    SummaryStatCard(icon = "🏃", label = "Activity", value = summary.activeDays.toString(), unit = "/ 7", subLabel = "Active days", valueColor = PrimaryNavy, modifier = Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Mood Trend bar chart
            ChartCard(title = "Mood Trend", legendLabel = "1 (Low) – 5 (High)", legendColor = MoodGreen) {
                BarChart(data = chartData, valueSelector = { it.moodScore }, maxValue = 5f, barColor = MoodGreen)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stress Trend bar chart
            ChartCard(title = "Stress Trend", legendLabel = "1 (Low) – 5 (High)", legendColor = StressOrange) {
                BarChart(data = chartData, valueSelector = { it.stressScore }, maxValue = 5f, barColor = StressOrange)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sleep Trend bar chart
            ChartCard(title = "Sleep Trend (Hours)", legendLabel = "Hours", legendColor = SleepBlue) {
                BarChart(data = chartData, valueSelector = { it.sleepHours }, maxValue = 10f, barColor = SleepBlue, yAxisMax = 10)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Insights card
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = CardBackground), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                    Box(modifier = Modifier.size(36.dp).background(Color(0xFFE8E8F5), shape = RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = PrimaryNavy, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        insights.forEach { insight ->
                            Row(verticalAlignment = Alignment.Top) {
                                Text("• ", fontSize = 13.sp, color = TextPrimary)
                                Text(text = insight, fontSize = 13.sp, color = TextPrimary, lineHeight = 18.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // View details link
            TextButton(onClick = { }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("View details", color = PrimaryNavy, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = PrimaryNavy, modifier = Modifier.size(16.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// Summary stat card
@Composable
fun SummaryStatCard(icon: String, label: String, value: String, unit: String, subLabel: String, valueColor: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = CardBackground), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = icon, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = label, fontSize = 11.sp, color = TextSecondary, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = valueColor)
                Text(text = " $unit", fontSize = 12.sp, color = TextSecondary, modifier = Modifier.padding(bottom = 3.dp))
            }
            Text(text = subLabel, fontSize = 11.sp, color = TextSecondary)
        }
    }
}

// Chart card wrapper
@Composable
fun ChartCard(title: String, legendLabel: String, legendColor: Color, content: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).background(legendColor, shape = RoundedCornerShape(2.dp)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = legendLabel, fontSize = 11.sp, color = TextSecondary)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

// Bar chart drawn with Canvas
@Composable
fun BarChart(data: List<ChartDataPoint>, valueSelector: (ChartDataPoint) -> Float, maxValue: Float, barColor: Color, yAxisMax: Int = 5) {
    val chartHeight = 160.dp
    val axisColor = Color(0xFFE0E0E8)

    Column {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.height(chartHeight).width(24.dp), verticalArrangement = Arrangement.SpaceBetween) {
                for (i in yAxisMax downTo 0 step (yAxisMax / 5).coerceAtLeast(1)) {
                    Text(text = i.toString(), fontSize = 10.sp, color = TextSecondary, textAlign = TextAlign.End)
                }
            }
            Canvas(modifier = Modifier.fillMaxWidth().height(chartHeight).padding(start = 28.dp)) {
                val barCount = data.size
                if (barCount == 0) return@Canvas
                val totalWidth = size.width
                val totalHeight = size.height
                val barWidth = (totalWidth / barCount) * 0.5f
                val gapWidth = (totalWidth / barCount) * 0.5f

                for (i in 0..5) {
                    val y = totalHeight - (totalHeight * i / 5f)
                    drawLine(color = axisColor, start = Offset(0f, y), end = Offset(totalWidth, y), strokeWidth = 1f)
                }

                data.forEachIndexed { index, point ->
                    val value = valueSelector(point)
                    val barHeight = if (maxValue > 0) (value / maxValue) * totalHeight else 0f
                    val x = index * (barWidth + gapWidth) + gapWidth / 2f
                    val y = totalHeight - barHeight
                    if (barHeight > 0) {
                        drawRoundRect(color = barColor, topLeft = Offset(x, y), size = Size(barWidth, barHeight), cornerRadius = CornerRadius(6f, 6f))
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth().padding(start = 28.dp), horizontalArrangement = Arrangement.SpaceAround) {
            data.forEach { point ->
                Text(text = point.day, fontSize = 11.sp, color = TextSecondary, textAlign = TextAlign.Center)
            }
        }
    }
}