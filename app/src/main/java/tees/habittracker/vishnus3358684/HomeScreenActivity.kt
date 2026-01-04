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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ContactSupport
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import tees.habittracker.vishnus3358684.ui.theme.PrimaryC1
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HabitTrackerTheme {
        HomeScreenContent(
            navController = NavHostController(LocalContext.current),
            habits = listOf(
                Habit(
                    1,
                    "Singing Practice",
                    "Practice for 30 mins",
                    "Music",
                    "High",
                    "Morning",
                    "20 Nov 2023",
                    "10:00 AM",
                    "Daily"
                ),
            )
        )
    }
}

@Composable
fun HomeScreen(navController: NavController, viewModel: HabitViewModel) {
    val habits by viewModel.allHabits.collectAsState()
    HomeScreenContent(navController = navController, habits = habits)
}


@Composable
fun HomeScreenAppBar(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(PrimaryC1)
            .padding(top = 36.dp, bottom = 12.dp, start = 12.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(onClick = {
            navController.navigate(AppScreens.Profile.route)

        }) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Account",
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Hello User 👋",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
            val currentDate = sdf.format(Date())
            Text(
                text = "Today $currentDate",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        IconButton(onClick = {
            navController.navigate(AppScreens.AboutUs.route)

        }) {
            Icon(
                imageVector = Icons.Default.ContactSupport,
                contentDescription = "Info",
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
        }

    }
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
            "Evening" -> habit.timeOfDay == "Evening"
            else -> false
        }
    }

    Scaffold(
        topBar = {
            HomeScreenAppBar(navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(AppScreens.AddHabit.route)
                },
                containerColor = Color.Black,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Habit"
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 16.dp)
        ) {

            Spacer(modifier = Modifier.height(8.dp))


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

            HomeNavigationRow(navController)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Today's Habits",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            TimeChipsRow(
                selected = selectedTimeChip,
                onSelected = { selectedTimeChip = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

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
fun HomeNavigationRow(navController: NavController) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        HomeNavCard(
            title = "My Habits",
            icon = Icons.Default.ListAlt,
            onClick = { navController.navigate(AppScreens.ViewHabits.route) },
            modifier = Modifier.weight(1f)
        )

        HomeNavCard(
            title = "Analytics",
            icon = Icons.Default.BarChart,
            onClick = { navController.navigate("analytics") },
            modifier = Modifier.weight(1f)

        )

        HomeNavCard(
            title = "Today",
            icon = Icons.Default.Today,
            onClick = { navController.navigate("todayHabits") },
            modifier = Modifier.weight(1f)
        )
    }
}


@Composable
fun HomeNavCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier
) {
    Card(
        modifier = modifier
            .height(110.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )

            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
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

