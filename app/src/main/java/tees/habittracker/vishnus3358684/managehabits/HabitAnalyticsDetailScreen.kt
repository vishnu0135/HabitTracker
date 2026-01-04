package tees.habittracker.vishnus3358684.managehabits

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import tees.habittracker.vishnus3358684.database.HabitViewModel
import tees.habittracker.vishnus3358684.ui.theme.PrimaryC1
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import android.graphics.Color as AndroidColor


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitAnalyticsDetailScreen(
    habitId: Int,
    viewModel: HabitViewModel,
    navController: NavController
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Weekly, 1 = Monthly
    var selectedWeekStart by remember { mutableStateOf(LocalDate.now().with(DayOfWeek.MONDAY)) }
    var selectedMonth by remember { mutableStateOf(YearMonth.now()) }

    LaunchedEffect(selectedTab, selectedWeekStart, selectedMonth) {
        if (selectedTab == 0) {
            viewModel.loadWeeklyAnalyticsForHabit(habitId, selectedWeekStart)
        } else {
            viewModel.loadMonthlyAnalyticsForHabit(habitId, selectedMonth)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Habit Analytics", fontWeight = FontWeight.Bold, fontSize = 22.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = PrimaryC1,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Weekly") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Monthly") }
                )
            }

            Spacer(Modifier.height(20.dp))

            if (selectedTab == 0) {
                WeeklyAnalyticsSection(
                    weekStart = selectedWeekStart,
                    onPrev = { selectedWeekStart = selectedWeekStart.minusWeeks(1) },
                    onNext = { selectedWeekStart = selectedWeekStart.plusWeeks(1) },
                    stats = viewModel.weeklyStats
                )
            } else {
                MonthlyAnalyticsSection(
                    month = selectedMonth,
                    onPrev = { selectedMonth = selectedMonth.minusMonths(1) },
                    onNext = { selectedMonth = selectedMonth.plusMonths(1) },
                    stats = viewModel.monthlyHabitStats
                )
            }
        }
    }
}



@Composable
fun WeeklyAnalyticsSection(
    weekStart: LocalDate,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    stats: List<WeeklyDayStat>
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onPrev) { Text("Prev") }

            Text(
                text = "${weekStart} → ${weekStart.plusDays(6)}",
                fontWeight = FontWeight.Bold
            )

            TextButton(onClick = onNext) { Text("Next") }
        }

        WeeklyHabitBarChart(stats)

        WeeklySummary(stats)
    }
}

@Composable
fun WeeklyHabitBarChart(
    stats: List<WeeklyDayStat>
) {
    val context = LocalContext.current

    if (stats.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No data available")
        }
        return
    }

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),
        factory = {
            BarChart(context).apply {

                description.isEnabled = false
                setDrawGridBackground(false)

                axisRight.isEnabled = false

                axisLeft.apply {
                    axisMinimum = 0f
                    granularity = 1f
                    setDrawGridLines(true)
                }

                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    granularity = 1f
                    setDrawGridLines(false)
                }

                legend.isEnabled = true
                legend.textSize = 12f
            }
        },
        update = { chart ->

            val completedEntries = ArrayList<BarEntry>()
            val missedEntries = ArrayList<BarEntry>()
            val labels = ArrayList<String>()

            stats.forEachIndexed { index, day ->
                completedEntries.add(
                    BarEntry(index.toFloat(), day.completed.toFloat())
                )
                missedEntries.add(
                    BarEntry(index.toFloat(), day.missed.toFloat())
                )
                labels.add(day.date) // Mon, Tue, Wed...
            }

            val completedSet = BarDataSet(completedEntries, "Completed").apply {
                color = AndroidColor.parseColor("#4CAF50")
                valueTextSize = 12f
            }

            val missedSet = BarDataSet(missedEntries, "Missed").apply {
                color = AndroidColor.parseColor("#F44336")
                valueTextSize = 12f
            }

            val data = BarData(completedSet, missedSet).apply {
                barWidth = 0.3f
            }

            chart.data = data

            chart.xAxis.valueFormatter =
                IndexAxisValueFormatter(labels)

            // Group bars (Completed + Missed per day)
            chart.xAxis.axisMinimum = -0.5f
            chart.xAxis.axisMaximum = labels.size - 0.5f
            chart.groupBars(0f, 0.4f, 0.05f)

            chart.animateY(800)
            chart.invalidate()
        }
    )
}


@Composable
fun WeeklyPieChart(
    completed: Int,
    missed: Int
) {
    val total = completed + missed
    if (total == 0) {
        Text("No data for this week")
        return
    }

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        factory = { context ->
            PieChart(context).apply {
                description.isEnabled = false
                isDrawHoleEnabled = true
                holeRadius = 55f
                transparentCircleRadius = 60f
                setUsePercentValues(true)
                legend.isEnabled = true
                setEntryLabelColor(AndroidColor.BLACK)
            }
        },
        update = { chart ->
            val entries = listOf(
                PieEntry(completed.toFloat(), "Completed"),
                PieEntry(missed.toFloat(), "Missed")
            )

            val dataSet = PieDataSet(entries, "").apply {
                colors = listOf(
                    AndroidColor.parseColor("#4CAF50"),
                    AndroidColor.parseColor("#F44336")
                )
                sliceSpace = 3f
                valueTextSize = 14f
                valueTextColor = AndroidColor.WHITE
            }

            chart.data = PieData(dataSet)
            chart.centerText = "${(completed * 100 / total)}% Completed"
            chart.setCenterTextSize(16f)

            chart.animateY(800)
            chart.invalidate()
        }
    )
}


@Composable
fun WeeklySummary(stats: List<WeeklyDayStat>) {
    val completed = stats.sumOf { it.completed }
    val missed = stats.sumOf { it.missed }

    Card(
        elevation = CardDefaults.cardElevation(6.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Weekly Summary",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            WeeklyPieChart(
                completed = completed,
                missed = missed
            )

            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatPill(
                    "Completed",
                    completed,
                    androidx.compose.ui.graphics.Color(0xFF4CAF50)
                )
                StatPill(
                    "Missed",
                    missed,
                    androidx.compose.ui.graphics.Color(0xFFF44336)
                )
            }
        }
    }
}



@Composable
fun MonthlyAnalyticsSection(
    month: YearMonth,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    stats: MonthlyHabitStats?
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

        // Month selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onPrev) { Text("Prev") }

            Text(
                text = "${month.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${month.year}",
                fontWeight = FontWeight.Bold
            )

            TextButton(onClick = onNext) { Text("Next") }
        }

        MonthlyHabitSummary(stats)
    }
}


@Composable
fun MonthlyPieChart(
    completed: Int,
    missed: Int
) {
    val total = completed + missed
    if (total == 0) {
        Text("No completion data available")
        return
    }

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        factory = { context ->
            PieChart(context).apply {
                description.isEnabled = false
                isDrawHoleEnabled = true
                setHoleColor(AndroidColor.TRANSPARENT)
                holeRadius = 55f
                transparentCircleRadius = 60f
                setUsePercentValues(true)
                setEntryLabelColor(AndroidColor.BLACK)
                legend.isEnabled = true
            }
        },
        update = { chart ->
            val entries = listOf(
                PieEntry(completed.toFloat(), "Completed"),
                PieEntry(missed.toFloat(), "Missed")
            )

            val dataSet = PieDataSet(entries, "").apply {
                colors = listOf(
                    AndroidColor.parseColor("#4CAF50"),
                    AndroidColor.parseColor("#F44336")
                )
                sliceSpace = 3f
                valueTextSize = 14f
                valueTextColor = AndroidColor.WHITE
            }

            chart.data = PieData(dataSet)
            chart.centerText =
                "${(completed * 100 / total)}% Completed"
            chart.setCenterTextSize(16f)

            chart.animateY(800)
            chart.invalidate()
        }
    )
}


@Composable
fun MonthlyHabitSummary(stat: MonthlyHabitStats?) {
    if (stat == null) {
        Text("No data for this month")
        return
    }

    Card(
        elevation = CardDefaults.cardElevation(6.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                "Monthly Summary",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            MonthlyPieChart(
                completed = stat.completedDays,
                missed = stat.missedDays
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                StatPill(
                    "Completed",
                    stat.completedDays,
                    androidx.compose.ui.graphics.Color(0xFF4CAF50)
                )
                StatPill(
                    "Missed",
                    stat.missedDays,
                    androidx.compose.ui.graphics.Color(0xFFF44336)
                )
            }
        }
    }
}



@Composable
fun StatPill(label: String, value: Int, color: androidx.compose.ui.graphics.Color) {
    Row(
        modifier = Modifier
            .background(color.copy(alpha = 0.15f), MaterialTheme.shapes.small)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text("$label: $value", color = color, fontSize = 14.sp)
    }
}

