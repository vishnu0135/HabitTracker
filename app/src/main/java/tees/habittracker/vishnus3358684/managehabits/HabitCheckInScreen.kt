package tees.habittracker.vishnus3358684.managehabits

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import tees.habittracker.vishnus3358684.database.HabitViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitCheckInScreen(
    habitId: Int,
    viewModel: HabitViewModel,
    navController: NavController
) {
    val status = viewModel.todayCompletion

    LaunchedEffect(habitId) {
        viewModel.loadTodayCompletion(habitId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Today's Check-in", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Did you complete this habit today?",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(24.dp))

            if (status == true) {
                CompletedBadge()
            } else if (status == false) {
                MissedBadge()
            }

            Spacer(Modifier.height(32.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Button(
                    onClick = {
                        viewModel.markToday(habitId, true)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    )
                ) {
                    Icon(Icons.Default.Check, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Done")
                }

                Button(
                    onClick = {
                        viewModel.markToday(habitId, false)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF44336)
                    )
                ) {
                    Icon(Icons.Default.Close, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Missed")
                }
            }
        }
    }
}


@Composable
fun CompletedBadge() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50))
            Spacer(Modifier.width(8.dp))
            Text("Completed Today 🎉", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun MissedBadge() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Cancel, null, tint = Color(0xFFF44336))
            Spacer(Modifier.width(8.dp))
            Text("Missed Today", fontWeight = FontWeight.Bold)
        }
    }
}


fun isHabitActiveToday(
    startDate: String,
    reminderTime: String
): Boolean {
    if (startDate.isBlank()) return true

    return try {
        val today = LocalDate.now()

        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
        val habitStartDate = LocalDate.parse(startDate, formatter)

        if (today.isBefore(habitStartDate)) {
            return false
        }

        if (today.isEqual(habitStartDate) && reminderTime.isNotBlank()) {
            val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
            val habitTime = LocalTime.parse(reminderTime, timeFormatter)

            val nowTime = LocalTime.now()
            return nowTime.isAfter(habitTime) || nowTime == habitTime
        }

        true
    } catch (e: Exception) {
        true
    }
}
