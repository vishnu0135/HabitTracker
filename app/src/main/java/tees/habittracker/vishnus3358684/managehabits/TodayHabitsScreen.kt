package tees.habittracker.vishnus3358684.managehabits

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Schedule
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
import androidx.navigation.NavController
import tees.habittracker.vishnus3358684.database.HabitStatus
import tees.habittracker.vishnus3358684.database.HabitViewModel
import tees.habittracker.vishnus3358684.database.TodayHabitUI
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayHabitsScreen(
    viewModel: HabitViewModel,
    navController: NavController
) {
    val habits = viewModel.todayHabits

    LaunchedEffect(Unit) {
        viewModel.loadTodayHabits()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Today's Habits", fontWeight = FontWeight.Bold, modifier = Modifier.clickable{
                    viewModel.seedTestDataFromNovember()
                }) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(habits) { item ->
                TodayHabitCard(
                    ui = item,
                    onCompleted = {
                        viewModel.markHabitForToday(item.habit.id, true)
                    },
                    onMissed = {
                        viewModel.markHabitForToday(item.habit.id, false)
                    }
                )
            }

        }



    }
}

@Composable
fun StreakChip(streak: Int) {
    if (streak <= 0) return

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color(0xFFFFF3E0))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.LocalFireDepartment,
            contentDescription = null,
            tint = Color(0xFFFF9800),
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = "$streak",
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFF9800),
            fontSize = 13.sp
        )
    }
}


@Composable
fun HabitStreakBadge(streak: Int) {
    if (streak <= 0) return

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color(0xFFFFF3E0))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.LocalFireDepartment,
            contentDescription = null,
            tint = Color(0xFFFF9800)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = "$streak day streak",
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFF9800)
        )
    }
}


@Composable
fun TodayHabitCard(
    ui: TodayHabitUI,
    onCompleted: () -> Unit,
    onMissed: () -> Unit
) {
    val bgColor = when (ui.status) {
        HabitStatus.LOCKED -> Color(0xFFF1F1F1)
        HabitStatus.PENDING -> Color(0xFFFFFDF7)
        HabitStatus.COMPLETED -> Color(0xFFF1F8F5)
        HabitStatus.MISSED -> Color(0xFFFFF1F1)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(Modifier.padding(18.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {
                    Text(
                        text = ui.habit.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Spacer(Modifier.height(2.dp))

                    Text(
                        text = "${ui.habit.category} • ${ui.habit.frequency}",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }

                StreakChip(ui.streak)
            }

            Spacer(Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                DetailPill(
                    icon = Icons.Default.AccessTime,
                    text = ui.habit.reminderTime
                )

                DetailPill(
                    icon = Icons.Default.Schedule,
                    text = ui.habit.timeOfDay
                )
            }

            Spacer(Modifier.height(14.dp))

            when (ui.status) {

                HabitStatus.LOCKED -> {
                    StatusText(
                        text = "Starts at ${ui.habit.reminderTime}",
                        color = Color.Gray
                    )
                }

                HabitStatus.PENDING -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        Button(
                            onClick = onCompleted,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50)
                            )
                        ) {
                            Icon(Icons.Default.Check, null)
                            Spacer(Modifier.width(6.dp))
                            Text("Completed")
                        }

                        Button(
                            onClick = onMissed,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFF44336)
                            )
                        ) {
                            Icon(Icons.Default.Close, null)
                            Spacer(Modifier.width(6.dp))
                            Text("Missed")
                        }
                    }
                }

                HabitStatus.COMPLETED -> {
                    StatusBadge(
                        text = "Completed Today 🎉",
                        color = Color(0xFF4CAF50),
                        icon = Icons.Default.CheckCircle
                    )
                }

                HabitStatus.MISSED -> {
                    StatusBadge(
                        text = "Missed Today",
                        color = Color(0xFFF44336),
                        icon = Icons.Default.Cancel
                    )
                }
            }
        }
    }
}

@Composable
fun DetailPill(
    icon: ImageVector,
    text: String
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color(0xFFF0F0FF))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = Color(0xFF6A5AE0), modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text(text, fontSize = 12.sp, color = Color(0xFF6A5AE0))
    }
}


@Composable
fun StatusText(text: String, color: Color) {
    Text(
        text = text,
        fontSize = 14.sp,
        color = color,
        fontWeight = FontWeight.Medium
    )
}



@Composable
fun StatusBadge(
    text: String,
    color: Color,
    icon: ImageVector
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = color)
        Spacer(Modifier.width(6.dp))
        Text(text, fontWeight = FontWeight.Bold, color = color)
    }
}

