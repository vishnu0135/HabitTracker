package tees.habittracker.vishnus3358684

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import tees.habittracker.vishnus3358684.database.HabitViewModel
import tees.habittracker.vishnus3358684.ui.theme.HabitTrackerTheme
import java.text.SimpleDateFormat
import java.util.*

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HabitTrackerTheme {
        HomeScreenContent(
            navController = NavHostController(LocalContext.current),
            habits = listOf(
                Habit(1, "Singing Practice", "Practice for 30 mins", "Music", "High", "Morning", "20 Nov 2023", "10:00 AM", "Daily"),
            )
        )
    }
}

@Composable
fun HomeScreen(navController: NavController, viewModel: HabitViewModel) {
    val habits by viewModel.allHabits.collectAsState()
    HomeScreenContent(navController = navController, habits = habits)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(navController: NavController, habits: List<Habit>) {

    val pagerState = rememberPagerState(pageCount = { 3 })
    var selectedTimeChip by remember { mutableStateOf("Morning") }

    LaunchedEffect(Unit) {
        while (true) {
            delay(2500)
            val nextPage = (pagerState.currentPage + 1) % 3
            pagerState.animateScrollToPage(page = nextPage)
        }
    }

    val filteredHabits = habits.filter { habit ->
        when (selectedTimeChip) {
            "Morning" -> habit.timeOfDay == "Morning"
            "Afternoon" -> habit.timeOfDay == "Afternoon" || habit.timeOfDay == "Mid-day"
            "Evening" -> habit.timeOfDay == "Night" // Assuming "Evening" corresponds to "Night"
            else -> false
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 16.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.outline_account_circle_24),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Hello User 👋",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
                    val currentDate = sdf.format(Date())
                    Text(
                        text = "Today $currentDate",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Image(
                    painter = painterResource(id = R.drawable.outline_add_circle_24),
                    contentDescription = "Add Habit",
                    modifier = Modifier
                        .size(48.dp)
                        .clickable {
                            navController.navigate(AppScreens.AddHabit.route)
                        }
                )
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .height(200.dp)
                    .clip(RoundedCornerShape(18.dp))
            ) { page ->
                val images = listOf(
                    R.drawable.h1,
                    R.drawable.h2,
                    R.drawable.h3,
                )
                Image(
                    painter = painterResource(id = images[page]),
                    contentDescription = "Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .size(if (pagerState.currentPage == index) 10.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (pagerState.currentPage == index) MaterialTheme.colorScheme.primary
                                else Color.LightGray
                            )
                            .padding(4.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate(AppScreens.ViewHabits.route) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text("View All Habits", color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold)
            }


            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Today's Habits",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            /** Time Chips **/
            TimeChipsRow(
                selected = selectedTimeChip,
                onSelected = { selectedTimeChip = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            /** Filtered Habit List **/
            if (filteredHabits.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No habits for ${selectedTimeChip.lowercase()}.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
            } else {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    filteredHabits.forEach { habit ->
                        HabitCard(habit = habit)
                    }
                }
            }
        }
    }
}

@Composable
fun TimeChipsRow(selected: String, onSelected: (String) -> Unit) {
    val chips = listOf("Morning", "Afternoon", "Evening")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        chips.forEach { label ->
            TimeChip(label, selected == label) { onSelected(label) }
        }
    }
}

@Composable
fun TimeChip(label: String, isSelected: Boolean, onSelected: (String) -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(30.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                shape = RoundedCornerShape(30.dp)
            )
            .clickable { onSelected(label) }
            .padding(horizontal = 18.dp, vertical = 10.dp)
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun HabitCard(habit: Habit) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = habit.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = habit.reminderTime,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}


@Composable
fun RequestNotificationPermissionsIfNeeded() {
    val context = LocalContext.current
    val activity = context as ComponentActivity

    // Launcher for POST_NOTIFICATIONS
    val notificationPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            // handle result if needed
        }

    LaunchedEffect(Unit) {

        // 1️⃣ Android 13+ requires POST_NOTIFICATIONS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!granted) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // 2️⃣ Android 12+ exact alarm check (cannot use launcher, requires intent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager =
                context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }
        }
    }
}
